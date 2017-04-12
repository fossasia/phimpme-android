package com.wellsql.generated;

import com.yarolegovich.wellsql.core.Mapper;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import org.wordpress.android.fluxc.model.CommentModel;

public final class CommentModelMapper implements Mapper<CommentModel> {
  @Override
  public Map<String, Object> toContentValues(CommentModel item) {
    Map<String, Object> cv = new HashMap<String, Object>();
    cv.put("_id", item.getId());
    cv.put("REMOTE_COMMENT_ID", item.getRemoteCommentId());
    cv.put("REMOTE_POST_ID", item.getRemotePostId());
    cv.put("REMOTE_PARENT_COMMENT_ID", item.getRemoteParentCommentId());
    cv.put("LOCAL_SITE_ID", item.getLocalSiteId());
    cv.put("REMOTE_SITE_ID", item.getRemoteSiteId());
    cv.put("AUTHOR_URL", item.getAuthorUrl());
    cv.put("AUTHOR_NAME", item.getAuthorName());
    cv.put("AUTHOR_EMAIL", item.getAuthorEmail());
    cv.put("AUTHOR_PROFILE_IMAGE_URL", item.getAuthorProfileImageUrl());
    cv.put("POST_TITLE", item.getPostTitle());
    cv.put("STATUS", item.getStatus());
    cv.put("DATE_PUBLISHED", item.getDatePublished());
    cv.put("CONTENT", item.getContent());
    cv.put("I_LIKE", item.getILike());
    return cv;
  }

  @Override
  public CommentModel convert(Map<String, Object> cv) {
    CommentModel item = new CommentModel();
    if (cv.get("_id") != null) {
      item.setId(((Long) cv.get("_id")).intValue());
    }
    if (cv.get("REMOTE_COMMENT_ID") != null) {
      item.setRemoteCommentId(((Long) cv.get("REMOTE_COMMENT_ID")).longValue());
    }
    if (cv.get("REMOTE_POST_ID") != null) {
      item.setRemotePostId(((Long) cv.get("REMOTE_POST_ID")).longValue());
    }
    if (cv.get("REMOTE_PARENT_COMMENT_ID") != null) {
      item.setRemoteParentCommentId(((Long) cv.get("REMOTE_PARENT_COMMENT_ID")).longValue());
    }
    if (cv.get("LOCAL_SITE_ID") != null) {
      item.setLocalSiteId(((Long) cv.get("LOCAL_SITE_ID")).intValue());
    }
    if (cv.get("REMOTE_SITE_ID") != null) {
      item.setRemoteSiteId(((Long) cv.get("REMOTE_SITE_ID")).longValue());
    }
    if (cv.get("AUTHOR_URL") != null) {
      item.setAuthorUrl((String) cv.get("AUTHOR_URL"));
    }
    if (cv.get("AUTHOR_NAME") != null) {
      item.setAuthorName((String) cv.get("AUTHOR_NAME"));
    }
    if (cv.get("AUTHOR_EMAIL") != null) {
      item.setAuthorEmail((String) cv.get("AUTHOR_EMAIL"));
    }
    if (cv.get("AUTHOR_PROFILE_IMAGE_URL") != null) {
      item.setAuthorProfileImageUrl((String) cv.get("AUTHOR_PROFILE_IMAGE_URL"));
    }
    if (cv.get("POST_TITLE") != null) {
      item.setPostTitle((String) cv.get("POST_TITLE"));
    }
    if (cv.get("STATUS") != null) {
      item.setStatus((String) cv.get("STATUS"));
    }
    if (cv.get("DATE_PUBLISHED") != null) {
      item.setDatePublished((String) cv.get("DATE_PUBLISHED"));
    }
    if (cv.get("CONTENT") != null) {
      item.setContent((String) cv.get("CONTENT"));
    }
    if (cv.get("I_LIKE") != null) {
      item.setILike(((Long) cv.get("I_LIKE")) == 1);
    }
    return item;
  }
}
