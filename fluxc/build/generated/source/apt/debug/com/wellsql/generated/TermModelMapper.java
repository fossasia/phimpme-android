package com.wellsql.generated;

import com.yarolegovich.wellsql.core.Mapper;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import org.wordpress.android.fluxc.model.TermModel;

public final class TermModelMapper implements Mapper<TermModel> {
  @Override
  public Map<String, Object> toContentValues(TermModel item) {
    Map<String, Object> cv = new HashMap<String, Object>();
    cv.put("_id", item.getId());
    cv.put("LOCAL_SITE_ID", item.getLocalSiteId());
    cv.put("REMOTE_TERM_ID", item.getRemoteTermId());
    cv.put("TAXONOMY", item.getTaxonomy());
    cv.put("NAME", item.getName());
    cv.put("SLUG", item.getSlug());
    cv.put("DESCRIPTION", item.getDescription());
    cv.put("PARENT_REMOTE_ID", item.getParentRemoteId());
    return cv;
  }

  @Override
  public TermModel convert(Map<String, Object> cv) {
    TermModel item = new TermModel();
    if (cv.get("_id") != null) {
      item.setId(((Long) cv.get("_id")).intValue());
    }
    if (cv.get("LOCAL_SITE_ID") != null) {
      item.setLocalSiteId(((Long) cv.get("LOCAL_SITE_ID")).intValue());
    }
    if (cv.get("REMOTE_TERM_ID") != null) {
      item.setRemoteTermId(((Long) cv.get("REMOTE_TERM_ID")).longValue());
    }
    if (cv.get("TAXONOMY") != null) {
      item.setTaxonomy((String) cv.get("TAXONOMY"));
    }
    if (cv.get("NAME") != null) {
      item.setName((String) cv.get("NAME"));
    }
    if (cv.get("SLUG") != null) {
      item.setSlug((String) cv.get("SLUG"));
    }
    if (cv.get("DESCRIPTION") != null) {
      item.setDescription((String) cv.get("DESCRIPTION"));
    }
    if (cv.get("PARENT_REMOTE_ID") != null) {
      item.setParentRemoteId(((Long) cv.get("PARENT_REMOTE_ID")).longValue());
    }
    return item;
  }
}
