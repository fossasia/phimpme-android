package com.wellsql.generated;

import com.yarolegovich.wellsql.core.Mapper;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import org.wordpress.android.fluxc.model.MediaModel;

public final class MediaModelMapper implements Mapper<MediaModel> {
  @Override
  public Map<String, Object> toContentValues(MediaModel item) {
    Map<String, Object> cv = new HashMap<String, Object>();
    cv.put("_id", item.getId());
    cv.put("LOCAL_SITE_ID", item.getLocalSiteId());
    cv.put("MEDIA_ID", item.getMediaId());
    cv.put("POST_ID", item.getPostId());
    cv.put("AUTHOR_ID", item.getAuthorId());
    cv.put("GUID", item.getGuid());
    cv.put("UPLOAD_DATE", item.getUploadDate());
    cv.put("URL", item.getUrl());
    cv.put("THUMBNAIL_URL", item.getThumbnailUrl());
    cv.put("FILE_NAME", item.getFileName());
    cv.put("FILE_PATH", item.getFilePath());
    cv.put("FILE_EXTENSION", item.getFileExtension());
    cv.put("MIME_TYPE", item.getMimeType());
    cv.put("TITLE", item.getTitle());
    cv.put("CAPTION", item.getCaption());
    cv.put("DESCRIPTION", item.getDescription());
    cv.put("ALT", item.getAlt());
    cv.put("WIDTH", item.getWidth());
    cv.put("HEIGHT", item.getHeight());
    cv.put("LENGTH", item.getLength());
    cv.put("VIDEO_PRESS_GUID", item.getVideoPressGuid());
    cv.put("VIDEO_PRESS_PROCESSING_DONE", item.getVideoPressProcessingDone());
    cv.put("UPLOAD_STATE", item.getUploadState());
    cv.put("HORIZONTAL_ALIGNMENT", item.getHorizontalAlignment());
    cv.put("VERTICAL_ALIGNMENT", item.getVerticalAlignment());
    cv.put("FEATURED", item.getFeatured());
    cv.put("FEATURED_IN_POST", item.getFeaturedInPost());
    return cv;
  }

  @Override
  public MediaModel convert(Map<String, Object> cv) {
    MediaModel item = new MediaModel();
    if (cv.get("_id") != null) {
      item.setId(((Long) cv.get("_id")).intValue());
    }
    if (cv.get("LOCAL_SITE_ID") != null) {
      item.setLocalSiteId(((Long) cv.get("LOCAL_SITE_ID")).intValue());
    }
    if (cv.get("MEDIA_ID") != null) {
      item.setMediaId(((Long) cv.get("MEDIA_ID")).longValue());
    }
    if (cv.get("POST_ID") != null) {
      item.setPostId(((Long) cv.get("POST_ID")).longValue());
    }
    if (cv.get("AUTHOR_ID") != null) {
      item.setAuthorId(((Long) cv.get("AUTHOR_ID")).longValue());
    }
    if (cv.get("GUID") != null) {
      item.setGuid((String) cv.get("GUID"));
    }
    if (cv.get("UPLOAD_DATE") != null) {
      item.setUploadDate((String) cv.get("UPLOAD_DATE"));
    }
    if (cv.get("URL") != null) {
      item.setUrl((String) cv.get("URL"));
    }
    if (cv.get("THUMBNAIL_URL") != null) {
      item.setThumbnailUrl((String) cv.get("THUMBNAIL_URL"));
    }
    if (cv.get("FILE_NAME") != null) {
      item.setFileName((String) cv.get("FILE_NAME"));
    }
    if (cv.get("FILE_PATH") != null) {
      item.setFilePath((String) cv.get("FILE_PATH"));
    }
    if (cv.get("FILE_EXTENSION") != null) {
      item.setFileExtension((String) cv.get("FILE_EXTENSION"));
    }
    if (cv.get("MIME_TYPE") != null) {
      item.setMimeType((String) cv.get("MIME_TYPE"));
    }
    if (cv.get("TITLE") != null) {
      item.setTitle((String) cv.get("TITLE"));
    }
    if (cv.get("CAPTION") != null) {
      item.setCaption((String) cv.get("CAPTION"));
    }
    if (cv.get("DESCRIPTION") != null) {
      item.setDescription((String) cv.get("DESCRIPTION"));
    }
    if (cv.get("ALT") != null) {
      item.setAlt((String) cv.get("ALT"));
    }
    if (cv.get("WIDTH") != null) {
      item.setWidth(((Long) cv.get("WIDTH")).intValue());
    }
    if (cv.get("HEIGHT") != null) {
      item.setHeight(((Long) cv.get("HEIGHT")).intValue());
    }
    if (cv.get("LENGTH") != null) {
      item.setLength(((Long) cv.get("LENGTH")).intValue());
    }
    if (cv.get("VIDEO_PRESS_GUID") != null) {
      item.setVideoPressGuid((String) cv.get("VIDEO_PRESS_GUID"));
    }
    if (cv.get("VIDEO_PRESS_PROCESSING_DONE") != null) {
      item.setVideoPressProcessingDone(((Long) cv.get("VIDEO_PRESS_PROCESSING_DONE")) == 1);
    }
    if (cv.get("UPLOAD_STATE") != null) {
      item.setUploadState((String) cv.get("UPLOAD_STATE"));
    }
    if (cv.get("HORIZONTAL_ALIGNMENT") != null) {
      item.setHorizontalAlignment(((Long) cv.get("HORIZONTAL_ALIGNMENT")).intValue());
    }
    if (cv.get("VERTICAL_ALIGNMENT") != null) {
      item.setVerticalAlignment(((Long) cv.get("VERTICAL_ALIGNMENT")) == 1);
    }
    if (cv.get("FEATURED") != null) {
      item.setFeatured(((Long) cv.get("FEATURED")) == 1);
    }
    if (cv.get("FEATURED_IN_POST") != null) {
      item.setFeaturedInPost(((Long) cv.get("FEATURED_IN_POST")) == 1);
    }
    return item;
  }
}
