package vn.mbm.phimp.me.wordpress;

/**
 * Created by rohanagarwal94 on 9/4/17.
 */

import org.wordpress.android.fluxc.action.MediaAction;
import org.wordpress.android.fluxc.annotations.action.Action;
import org.wordpress.android.fluxc.annotations.action.ActionBuilder;
import org.wordpress.android.fluxc.model.MediaModel;
import org.wordpress.android.fluxc.store.MediaStore.FetchMediaListPayload;
import org.wordpress.android.fluxc.store.MediaStore.FetchMediaListResponsePayload;
import org.wordpress.android.fluxc.store.MediaStore.MediaPayload;
import org.wordpress.android.fluxc.store.MediaStore.ProgressPayload;

public final class MediaActionBuilder extends ActionBuilder {
    public MediaActionBuilder() {
    }

    public static Action<MediaPayload> newPushMediaAction(MediaPayload payload) {
        return new Action(MediaAction.PUSH_MEDIA, payload);
    }

    public static Action<MediaPayload> newUploadMediaAction(MediaPayload payload) {
        return new Action(MediaAction.UPLOAD_MEDIA, payload);
    }

    public static Action<FetchMediaListPayload> newFetchMediaListAction(FetchMediaListPayload payload) {
        return new Action(MediaAction.FETCH_MEDIA_LIST, payload);
    }

    public static Action<MediaPayload> newFetchMediaAction(MediaPayload payload) {
        return new Action(MediaAction.FETCH_MEDIA, payload);
    }

    public static Action<MediaPayload> newDeleteMediaAction(MediaPayload payload) {
        return new Action(MediaAction.DELETE_MEDIA, payload);
    }

    public static Action<MediaPayload> newCancelMediaUploadAction(MediaPayload payload) {
        return new Action(MediaAction.CANCEL_MEDIA_UPLOAD, payload);
    }

    public static Action<MediaPayload> newPushedMediaAction(MediaPayload payload) {
        return new Action(MediaAction.PUSHED_MEDIA, payload);
    }

    public static Action<ProgressPayload> newUploadedMediaAction(ProgressPayload payload) {
        return new Action(MediaAction.UPLOADED_MEDIA, payload);
    }

    public static Action<FetchMediaListResponsePayload> newFetchedMediaListAction(FetchMediaListResponsePayload payload) {
        return new Action(MediaAction.FETCHED_MEDIA_LIST, payload);
    }

    public static Action<MediaPayload> newFetchedMediaAction(MediaPayload payload) {
        return new Action(MediaAction.FETCHED_MEDIA, payload);
    }

    public static Action<MediaPayload> newDeletedMediaAction(MediaPayload payload) {
        return new Action(MediaAction.DELETED_MEDIA, payload);
    }

    public static Action<ProgressPayload> newCanceledMediaUploadAction(ProgressPayload payload) {
        return new Action(MediaAction.CANCELED_MEDIA_UPLOAD, payload);
    }

    public static Action<MediaModel> newUpdateMediaAction(MediaModel payload) {
        return new Action(MediaAction.UPDATE_MEDIA, payload);
    }

    public static Action<MediaModel> newRemoveMediaAction(MediaModel payload) {
        return new Action(MediaAction.REMOVE_MEDIA, payload);
    }

    public static Action<Void> newRemoveAllMediaAction() {
        return generateNoPayloadAction(MediaAction.REMOVE_ALL_MEDIA);
    }
}
