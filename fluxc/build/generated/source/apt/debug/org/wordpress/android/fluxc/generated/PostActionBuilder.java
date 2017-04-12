package org.wordpress.android.fluxc.generated;

import java.lang.Void;
import org.wordpress.android.fluxc.action.PostAction;
import org.wordpress.android.fluxc.annotations.action.Action;
import org.wordpress.android.fluxc.annotations.action.ActionBuilder;
import org.wordpress.android.fluxc.model.PostModel;
import org.wordpress.android.fluxc.store.PostStore;

public final class PostActionBuilder extends ActionBuilder {
  public static Action<PostStore.FetchPostsPayload> newFetchPostsAction(PostStore.FetchPostsPayload payload) {
    return new Action<>(PostAction.FETCH_POSTS, payload);
  }

  public static Action<PostStore.FetchPostsPayload> newFetchPagesAction(PostStore.FetchPostsPayload payload) {
    return new Action<>(PostAction.FETCH_PAGES, payload);
  }

  public static Action<PostStore.RemotePostPayload> newFetchPostAction(PostStore.RemotePostPayload payload) {
    return new Action<>(PostAction.FETCH_POST, payload);
  }

  public static Action<PostStore.RemotePostPayload> newPushPostAction(PostStore.RemotePostPayload payload) {
    return new Action<>(PostAction.PUSH_POST, payload);
  }

  public static Action<PostStore.RemotePostPayload> newDeletePostAction(PostStore.RemotePostPayload payload) {
    return new Action<>(PostAction.DELETE_POST, payload);
  }

  public static Action<PostStore.FetchPostsResponsePayload> newFetchedPostsAction(PostStore.FetchPostsResponsePayload payload) {
    return new Action<>(PostAction.FETCHED_POSTS, payload);
  }

  public static Action<PostStore.FetchPostResponsePayload> newFetchedPostAction(PostStore.FetchPostResponsePayload payload) {
    return new Action<>(PostAction.FETCHED_POST, payload);
  }

  public static Action<PostStore.RemotePostPayload> newPushedPostAction(PostStore.RemotePostPayload payload) {
    return new Action<>(PostAction.PUSHED_POST, payload);
  }

  public static Action<PostStore.RemotePostPayload> newDeletedPostAction(PostStore.RemotePostPayload payload) {
    return new Action<>(PostAction.DELETED_POST, payload);
  }

  public static Action<PostModel> newUpdatePostAction(PostModel payload) {
    return new Action<>(PostAction.UPDATE_POST, payload);
  }

  public static Action<PostModel> newRemovePostAction(PostModel payload) {
    return new Action<>(PostAction.REMOVE_POST, payload);
  }

  public static Action<Void> newRemoveAllPostsAction() {
    return generateNoPayloadAction(PostAction.REMOVE_ALL_POSTS);
  }
}
