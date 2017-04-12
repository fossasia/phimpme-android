package org.wordpress.android.fluxc.generated;

import java.lang.Void;
import org.wordpress.android.fluxc.action.CommentAction;
import org.wordpress.android.fluxc.annotations.action.Action;
import org.wordpress.android.fluxc.annotations.action.ActionBuilder;
import org.wordpress.android.fluxc.model.CommentModel;
import org.wordpress.android.fluxc.model.SiteModel;
import org.wordpress.android.fluxc.store.CommentStore;

public final class CommentActionBuilder extends ActionBuilder {
  public static Action<CommentStore.FetchCommentsPayload> newFetchCommentsAction(CommentStore.FetchCommentsPayload payload) {
    return new Action<>(CommentAction.FETCH_COMMENTS, payload);
  }

  public static Action<CommentStore.RemoteCommentPayload> newFetchCommentAction(CommentStore.RemoteCommentPayload payload) {
    return new Action<>(CommentAction.FETCH_COMMENT, payload);
  }

  public static Action<CommentStore.RemoteCreateCommentPayload> newCreateNewCommentAction(CommentStore.RemoteCreateCommentPayload payload) {
    return new Action<>(CommentAction.CREATE_NEW_COMMENT, payload);
  }

  public static Action<CommentStore.RemoteCommentPayload> newPushCommentAction(CommentStore.RemoteCommentPayload payload) {
    return new Action<>(CommentAction.PUSH_COMMENT, payload);
  }

  public static Action<CommentStore.RemoteCommentPayload> newDeleteCommentAction(CommentStore.RemoteCommentPayload payload) {
    return new Action<>(CommentAction.DELETE_COMMENT, payload);
  }

  public static Action<CommentStore.RemoteCommentPayload> newLikeCommentAction(CommentStore.RemoteCommentPayload payload) {
    return new Action<>(CommentAction.LIKE_COMMENT, payload);
  }

  public static Action<CommentStore.FetchCommentsResponsePayload> newFetchedCommentsAction(CommentStore.FetchCommentsResponsePayload payload) {
    return new Action<>(CommentAction.FETCHED_COMMENTS, payload);
  }

  public static Action<CommentStore.RemoteCommentResponsePayload> newFetchedCommentAction(CommentStore.RemoteCommentResponsePayload payload) {
    return new Action<>(CommentAction.FETCHED_COMMENT, payload);
  }

  public static Action<CommentStore.RemoteCommentResponsePayload> newCreatedNewCommentAction(CommentStore.RemoteCommentResponsePayload payload) {
    return new Action<>(CommentAction.CREATED_NEW_COMMENT, payload);
  }

  public static Action<CommentStore.RemoteCommentResponsePayload> newPushedCommentAction(CommentStore.RemoteCommentResponsePayload payload) {
    return new Action<>(CommentAction.PUSHED_COMMENT, payload);
  }

  public static Action<CommentStore.RemoteCommentResponsePayload> newDeletedCommentAction(CommentStore.RemoteCommentResponsePayload payload) {
    return new Action<>(CommentAction.DELETED_COMMENT, payload);
  }

  public static Action<CommentStore.RemoteCommentResponsePayload> newLikedCommentAction(CommentStore.RemoteCommentResponsePayload payload) {
    return new Action<>(CommentAction.LIKED_COMMENT, payload);
  }

  public static Action<CommentModel> newUpdateCommentAction(CommentModel payload) {
    return new Action<>(CommentAction.UPDATE_COMMENT, payload);
  }

  public static Action<SiteModel> newRemoveCommentsAction(SiteModel payload) {
    return new Action<>(CommentAction.REMOVE_COMMENTS, payload);
  }

  public static Action<CommentModel> newRemoveCommentAction(CommentModel payload) {
    return new Action<>(CommentAction.REMOVE_COMMENT, payload);
  }

  public static Action<Void> newRemoveAllCommentsAction() {
    return generateNoPayloadAction(CommentAction.REMOVE_ALL_COMMENTS);
  }
}
