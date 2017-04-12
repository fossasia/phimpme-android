package com.wellsql.generated;

import com.yarolegovich.wellsql.core.Mapper;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import org.wordpress.android.fluxc.model.PostModel;

public final class PostModelMapper implements Mapper<PostModel> {
  @Override
  public Map<String, Object> toContentValues(PostModel item) {
    Map<String, Object> cv = new HashMap<String, Object>();
    cv.put("_id", item.getId());
    cv.put("LOCAL_SITE_ID", item.getLocalSiteId());
    cv.put("REMOTE_SITE_ID", item.getRemoteSiteId());
    cv.put("REMOTE_POST_ID", item.getRemotePostId());
    cv.put("TITLE", item.getTitle());
    cv.put("CONTENT", item.getContent());
    cv.put("DATE_CREATED", item.getDateCreated());
    cv.put("CATEGORY_IDS", item.getCategoryIds());
    cv.put("CUSTOM_FIELDS", item.getCustomFields());
    cv.put("LINK", item.getLink());
    cv.put("EXCERPT", item.getExcerpt());
    cv.put("TAG_NAMES", item.getTagNames());
    cv.put("STATUS", item.getStatus());
    cv.put("PASSWORD", item.getPassword());
    cv.put("FEATURED_IMAGE_ID", item.getFeaturedImageId());
    cv.put("POST_FORMAT", item.getPostFormat());
    cv.put("SLUG", item.getSlug());
    cv.put("LATITUDE", item.getLatitude());
    cv.put("LONGITUDE", item.getLongitude());
    cv.put("IS_PAGE", item.isPage());
    cv.put("PARENT_ID", item.getParentId());
    cv.put("PARENT_TITLE", item.getParentTitle());
    cv.put("IS_LOCAL_DRAFT", item.isLocalDraft());
    cv.put("IS_LOCALLY_CHANGED", item.isLocallyChanged());
    cv.put("DATE_LOCALLY_CHANGED", item.getDateLocallyChanged());
    cv.put("LAST_KNOWN_REMOTE_FEATURED_IMAGE_ID", item.getLastKnownRemoteFeaturedImageId());
    cv.put("HAS_CAPABILITY_PUBLISH_POST", item.getHasCapabilityPublishPost());
    cv.put("HAS_CAPABILITY_EDIT_POST", item.getHasCapabilityEditPost());
    cv.put("HAS_CAPABILITY_DELETE_POST", item.getHasCapabilityDeletePost());
    return cv;
  }

  @Override
  public PostModel convert(Map<String, Object> cv) {
    PostModel item = new PostModel();
    if (cv.get("_id") != null) {
      item.setId(((Long) cv.get("_id")).intValue());
    }
    if (cv.get("LOCAL_SITE_ID") != null) {
      item.setLocalSiteId(((Long) cv.get("LOCAL_SITE_ID")).intValue());
    }
    if (cv.get("REMOTE_SITE_ID") != null) {
      item.setRemoteSiteId(((Long) cv.get("REMOTE_SITE_ID")).longValue());
    }
    if (cv.get("REMOTE_POST_ID") != null) {
      item.setRemotePostId(((Long) cv.get("REMOTE_POST_ID")).longValue());
    }
    if (cv.get("TITLE") != null) {
      item.setTitle((String) cv.get("TITLE"));
    }
    if (cv.get("CONTENT") != null) {
      item.setContent((String) cv.get("CONTENT"));
    }
    if (cv.get("DATE_CREATED") != null) {
      item.setDateCreated((String) cv.get("DATE_CREATED"));
    }
    if (cv.get("CATEGORY_IDS") != null) {
      item.setCategoryIds((String) cv.get("CATEGORY_IDS"));
    }
    if (cv.get("CUSTOM_FIELDS") != null) {
      item.setCustomFields((String) cv.get("CUSTOM_FIELDS"));
    }
    if (cv.get("LINK") != null) {
      item.setLink((String) cv.get("LINK"));
    }
    if (cv.get("EXCERPT") != null) {
      item.setExcerpt((String) cv.get("EXCERPT"));
    }
    if (cv.get("TAG_NAMES") != null) {
      item.setTagNames((String) cv.get("TAG_NAMES"));
    }
    if (cv.get("STATUS") != null) {
      item.setStatus((String) cv.get("STATUS"));
    }
    if (cv.get("PASSWORD") != null) {
      item.setPassword((String) cv.get("PASSWORD"));
    }
    if (cv.get("FEATURED_IMAGE_ID") != null) {
      item.setFeaturedImageId(((Long) cv.get("FEATURED_IMAGE_ID")).longValue());
    }
    if (cv.get("POST_FORMAT") != null) {
      item.setPostFormat((String) cv.get("POST_FORMAT"));
    }
    if (cv.get("SLUG") != null) {
      item.setSlug((String) cv.get("SLUG"));
    }
    if (cv.get("LATITUDE") != null) {
      item.setLatitude(((Double) cv.get("LATITUDE")).doubleValue());
    }
    if (cv.get("LONGITUDE") != null) {
      item.setLongitude(((Double) cv.get("LONGITUDE")).doubleValue());
    }
    if (cv.get("IS_PAGE") != null) {
      item.setIsPage(((Long) cv.get("IS_PAGE")) == 1);
    }
    if (cv.get("PARENT_ID") != null) {
      item.setParentId(((Long) cv.get("PARENT_ID")).longValue());
    }
    if (cv.get("PARENT_TITLE") != null) {
      item.setParentTitle((String) cv.get("PARENT_TITLE"));
    }
    if (cv.get("IS_LOCAL_DRAFT") != null) {
      item.setIsLocalDraft(((Long) cv.get("IS_LOCAL_DRAFT")) == 1);
    }
    if (cv.get("IS_LOCALLY_CHANGED") != null) {
      item.setIsLocallyChanged(((Long) cv.get("IS_LOCALLY_CHANGED")) == 1);
    }
    if (cv.get("DATE_LOCALLY_CHANGED") != null) {
      item.setDateLocallyChanged((String) cv.get("DATE_LOCALLY_CHANGED"));
    }
    if (cv.get("LAST_KNOWN_REMOTE_FEATURED_IMAGE_ID") != null) {
      item.setLastKnownRemoteFeaturedImageId(((Long) cv.get("LAST_KNOWN_REMOTE_FEATURED_IMAGE_ID")).longValue());
    }
    if (cv.get("HAS_CAPABILITY_PUBLISH_POST") != null) {
      item.setHasCapabilityPublishPost(((Long) cv.get("HAS_CAPABILITY_PUBLISH_POST")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_EDIT_POST") != null) {
      item.setHasCapabilityEditPost(((Long) cv.get("HAS_CAPABILITY_EDIT_POST")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_DELETE_POST") != null) {
      item.setHasCapabilityDeletePost(((Long) cv.get("HAS_CAPABILITY_DELETE_POST")) == 1);
    }
    return item;
  }
}
