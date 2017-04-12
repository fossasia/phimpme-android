package vn.mbm.phimp.me.wordpress;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.model.MediaModel;
import org.wordpress.android.fluxc.model.MediaModel.UploadState;
import org.wordpress.android.fluxc.model.SiteModel;
import org.wordpress.android.fluxc.store.MediaStore;
import org.wordpress.android.fluxc.store.MediaStore.MediaPayload;
import org.wordpress.android.fluxc.store.MediaStore.OnMediaUploaded;
import org.wordpress.android.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import vn.mbm.phimp.me.MyApplication;

/**
 * Started with explicit list of media to upload.
 */

public class MediaUploadService extends Service {
    private static final String MEDIA_LIST_KEY = "mediaList";

    private SiteModel mSite;
    private MediaModel mCurrentUpload;

    private List<MediaModel> mUploadQueue = new ArrayList<>();
    private SparseArray<Long> mUploadQueueTime = new SparseArray<>();

    @Inject Dispatcher mDispatcher;
    @Inject MediaStore mMediaStore;

    public static void startService(Context context, SiteModel siteModel, ArrayList<MediaModel> mediaList) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, MediaUploadService.class);
        intent.putExtra(MyApplication.SITE, siteModel);
        intent.putExtra(MediaUploadService.MEDIA_LIST_KEY, mediaList);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((MyApplication) getApplication()).component().inject(this);
        mDispatcher.register(this);
        EventBus.getDefault().register(this);
        mCurrentUpload = null;
    }

    @Override
    public void onDestroy() {
        cancelCurrentUpload();
        mDispatcher.unregister(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || !intent.hasExtra(MyApplication.SITE)) {
            stopServiceIfUploadsComplete();
            return START_NOT_STICKY;
        }

        unpackIntent(intent);
        uploadNextInQueue();

        return START_REDELIVER_INTENT;
    }

    private void handleOnMediaUploadedSuccess(@NonNull OnMediaUploaded event) {
        if (event.canceled) {
            // Upload canceled
            completeCurrentUpload();
            uploadNextInQueue();
        } else if (event.completed) {
            // Upload completed
            mCurrentUpload.setMediaId(event.media.getMediaId());
            completeCurrentUpload();
            uploadNextInQueue();
        } else {
            // Upload Progress
            // TODO check if we need to broadcast event.media, event.progress or we're just fine with
            // listening to  event.media, event.progress
        }
    }

    private void handleOnMediaUploadedError(@NonNull OnMediaUploaded event) {
        // TODO: Don't update the state here, it needs to be done in FluxC
        mCurrentUpload.setUploadState(UploadState.FAILED.name());
        mDispatcher.dispatch(MediaActionBuilder.newUpdateMediaAction(mCurrentUpload));
        // TODO: check whether we need to broadcast the error or maybe it is enough to register for FluxC events
        // event.media, event.error
        Map<String, Object> properties = new HashMap<>();
        properties.put("error_type", event.error.type.name());
        completeCurrentUpload();
        uploadNextInQueue();
    }

    private void uploadNextInQueue() {
        // waiting for response to current upload request
        if (mCurrentUpload != null) {
            return;
        }

        // somehow lost our reference to the site, complete this action
        if (mSite == null) {
            stopServiceIfUploadsComplete();
            return;
        }
        mCurrentUpload = getNextMediaToUpload();
        if (mCurrentUpload == null) {
            stopServiceIfUploadsComplete();
            return;
        }
        dispatchUploadAction(mCurrentUpload);
    }

    private void completeCurrentUpload() {
        if (mCurrentUpload != null) {
            mUploadQueue.remove(mCurrentUpload);
            mCurrentUpload = null;
        }
    }

    private MediaModel getNextMediaToUpload() {
        if (!mUploadQueue.isEmpty()) {
            return mUploadQueue.get(0);
        }
        return null;
    }

    private void addUniqueMediaToQueue(MediaModel media) {
        for (MediaModel queuedMedia : mUploadQueue) {
            if (queuedMedia.getLocalSiteId() == media.getLocalSiteId() &&
                    StringUtils.equals(queuedMedia.getFilePath(), media.getFilePath())) {
                return;
            }
        }

        // no match found in queue
        mUploadQueue.add(media);
    }

    private void unpackIntent(@NonNull Intent intent) {
        mSite = (SiteModel) intent.getSerializableExtra(MyApplication.SITE);

        // add local queued media from store
        List<MediaModel> localMedia = mMediaStore.getLocalSiteMedia(mSite);
        if (localMedia != null && !localMedia.isEmpty()) {
            // uploading is updated to queued, queued media added to the queue, failed media added to completed list
            for (MediaModel mediaItem : localMedia) {
                if (MediaUploadState.UPLOADING.name().equals(mediaItem.getUploadState())) {
                    mediaItem.setUploadState(MediaUploadState.QUEUED.name());
                    mDispatcher.dispatch(MediaActionBuilder.newUpdateMediaAction(mediaItem));
                }

                if (MediaUploadState.QUEUED.name().equals(mediaItem.getUploadState())) {
                    addUniqueMediaToQueue(mediaItem);
                }
            }
        }

        // add new media
        @SuppressWarnings("unchecked")
        List<MediaModel> mediaList = (List<MediaModel>) intent.getSerializableExtra(MEDIA_LIST_KEY);
        if (mediaList != null) {
            for (MediaModel media : mediaList) {
                addUniqueMediaToQueue(media);
            }
        }
    }

    private boolean matchesInProgressMedia(final @NonNull MediaModel media) {
        return mCurrentUpload != null && media.getLocalSiteId() == mCurrentUpload.getLocalSiteId();
    }

    private void cancelCurrentUpload() {
        if (mCurrentUpload != null) {
            dispatchCancelAction(mCurrentUpload);
            mCurrentUpload = null;
        }
    }

    private void cancelAllUploads() {
        mUploadQueue.clear();
        mUploadQueueTime.clear();
        cancelCurrentUpload();
    }

    private void cancelUpload(int localMediaId) {
        // Cancel if it's currently uploading
        if (mCurrentUpload != null && mCurrentUpload.getId() == localMediaId) {
            cancelCurrentUpload();
        }
        // Remove from the queue
        for(Iterator<MediaModel> i = mUploadQueue.iterator(); i.hasNext();) {
            MediaModel mediaModel = i.next();
            if (mediaModel.getId() == localMediaId) {
                i.remove();
            }
        }
    }

    private void dispatchUploadAction(@NonNull final MediaModel media) {
        mDispatcher.dispatch(MediaActionBuilder.newUpdateMediaAction(media));

        MediaPayload payload = new MediaPayload(mSite, media);
        mDispatcher.dispatch(MediaActionBuilder.newUploadMediaAction(payload));
    }

    private void dispatchCancelAction(@NonNull final MediaModel media) {
        MediaPayload payload = new MediaPayload(mSite, mCurrentUpload);
        mDispatcher.dispatch(MediaActionBuilder.newCancelMediaUploadAction(payload));
    }

    private void stopServiceIfUploadsComplete(){
        if (mUploadQueue.size() == 0) {
            stopSelf();
        }
    }

    // App events

    @SuppressWarnings("unused")
    public void onEventMainThread(PostEvents.PostMediaCanceled event) {
        if (event.all) {
            cancelAllUploads();
            return;
        }
        cancelUpload(event.localMediaId);
    }

    // FluxC events

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMediaUploaded(OnMediaUploaded event) {
        // event for unknown media, ignoring
        if (event.media == null || !matchesInProgressMedia(event.media)) {
            return;
        }

        if (event.isError()) {
            handleOnMediaUploadedError(event);
        } else {
            handleOnMediaUploadedSuccess(event);
        }
    }
}
