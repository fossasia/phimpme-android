package com.wellsql.generated;

import com.yarolegovich.wellsql.core.Mapper;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import org.wordpress.android.fluxc.model.PostFormatModel;

public final class PostFormatModelMapper implements Mapper<PostFormatModel> {
  @Override
  public Map<String, Object> toContentValues(PostFormatModel item) {
    Map<String, Object> cv = new HashMap<String, Object>();
    cv.put("_id", item.getId());
    cv.put("SITE_ID", item.getSiteId());
    cv.put("SLUG", item.getSlug());
    cv.put("DISPLAY_NAME", item.getDisplayName());
    return cv;
  }

  @Override
  public PostFormatModel convert(Map<String, Object> cv) {
    PostFormatModel item = new PostFormatModel();
    if (cv.get("_id") != null) {
      item.setId(((Long) cv.get("_id")).intValue());
    }
    if (cv.get("SITE_ID") != null) {
      item.setSiteId(((Long) cv.get("SITE_ID")).intValue());
    }
    if (cv.get("SLUG") != null) {
      item.setSlug((String) cv.get("SLUG"));
    }
    if (cv.get("DISPLAY_NAME") != null) {
      item.setDisplayName((String) cv.get("DISPLAY_NAME"));
    }
    return item;
  }
}
