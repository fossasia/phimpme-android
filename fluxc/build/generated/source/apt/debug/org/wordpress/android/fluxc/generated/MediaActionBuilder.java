package org.wordpress.android.fluxc.generated;

import java.lang.Void;
import org.wordpress.android.fluxc.action.MediaAction;
import org.wordpress.android.fluxc.annotations.action.Action;
import org.wordpress.android.fluxc.annotations.action.ActionBuilder;
import org.wordpress.android.fluxc.model.MediaModel;
import org.wordpress.android.fluxc.store.MediaStore;

public final class MediaActionBuilder extends ActionBuilder {
  public static Action<MediaStore.MediaPayload> newPushMediaAction(MediaStore.MediaPayload payload) {
    return new Action<>(MediaAction.PUSH_MEDIA, payload);
  }

  public static Action<MediaStore.MediaPayload> newUploadMediaAction(MediaStore.MediaPayload payload) {
    return new Action<>(MediaAction.UPLOAD_MEDIA, payload);
  }

  public static Action<MediaStore.FetchMediaListPayload> newFetchMediaListAction(MediaStore.FetchMediaListPayload payload) {
    return new Action<>(MediaAction.FETCH_MEDIA_LIST, payload);
  }

  public static Action<MediaStore.MediaPayload> newFetchMediaAction(MediaStore.MediaPayload payload) {
    return new Action<>(MediaAction.FETCH_MEDIA, payload);
  }

  public static Action<MediaStore.MediaPayload> newDeleteMediaAction(MediaStore.MediaPayload payload) {
    return new Action<>(MediaAction.DELETE_MEDIA, payload);
  }

  public static Action<MediaStore.MediaPayload> newCancelMediaUploadAction(MediaStore.MediaPayload payload) {
    return new Action<>(MediaAction.CANCEL_MEDIA_UPLOAD, payload);
  }

  public static Action<MediaStore.MediaPayload> newPushedMediaAction(MediaStore.MediaPayload payload) {
    return new Action<>(MediaAction.PUSHED_MEDIA, payload);
  }

  public static Action<MediaStore.ProgressPayload> newUploadedMediaAction(MediaStore.ProgressPayload payload) {
    return new Action<>(MediaAction.UPLOADED_MEDIA, payload);
  }

  public static Action<MediaStore.FetchMediaListResponsePayload> newFetchedMediaListAction(MediaStore.FetchMediaListResponsePayload payload) {
    return new Action<>(MediaAction.FETCHED_MEDIA_LIST, payload);
  }

  public static Action<MediaStore.MediaPayload> newFetchedMediaAction(MediaStore.MediaPayload payload) {
    return new Action<>(MediaAction.FETCHED_MEDIA, payload);
  }

  public static Action<MediaStore.MediaPayload> newDeletedMediaAction(MediaStore.MediaPayload payload) {
    return new Action<>(MediaAction.DELETED_MEDIA, payload);
  }

  public static Action<MediaStore.ProgressPayload> newCanceledMediaUploadAction(MediaStore.ProgressPayload payload) {
    return new Action<>(MediaAction.CANCELED_MEDIA_UPLOAD, payload);
  }

  public static Action<MediaModel> newUpdateMediaAction(MediaModel payload) {
    return new Action<>(MediaAction.UPDATE_MEDIA, payload);
  }

  public static Action<MediaModel> newRemoveMediaAction(MediaModel payload) {
    return new Action<>(MediaAction.REMOVE_MEDIA, payload);
  }

  public static Action<Void> newRemoveAllMediaAction() {
    return generateNoPayloadAction(MediaAction.REMOVE_ALL_MEDIA);
  }
}
