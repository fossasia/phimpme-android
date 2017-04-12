package com.wellsql.generated;

import com.yarolegovich.wellsql.core.Mapper;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import org.wordpress.android.fluxc.model.TaxonomyModel;

public final class TaxonomyModelMapper implements Mapper<TaxonomyModel> {
  @Override
  public Map<String, Object> toContentValues(TaxonomyModel item) {
    Map<String, Object> cv = new HashMap<String, Object>();
    cv.put("_id", item.getId());
    cv.put("LOCAL_SITE_ID", item.getLocalSiteId());
    cv.put("NAME", item.getName());
    cv.put("LABEL", item.getLabel());
    cv.put("DESCRIPTION", item.getDescription());
    cv.put("IS_HIERARCHICAL", item.isHierarchical());
    cv.put("IS_PUBLIC", item.isPublic());
    return cv;
  }

  @Override
  public TaxonomyModel convert(Map<String, Object> cv) {
    TaxonomyModel item = new TaxonomyModel();
    if (cv.get("_id") != null) {
      item.setId(((Long) cv.get("_id")).intValue());
    }
    if (cv.get("LOCAL_SITE_ID") != null) {
      item.setLocalSiteId(((Long) cv.get("LOCAL_SITE_ID")).intValue());
    }
    if (cv.get("NAME") != null) {
      item.setName((String) cv.get("NAME"));
    }
    if (cv.get("LABEL") != null) {
      item.setLabel((String) cv.get("LABEL"));
    }
    if (cv.get("DESCRIPTION") != null) {
      item.setDescription((String) cv.get("DESCRIPTION"));
    }
    if (cv.get("IS_HIERARCHICAL") != null) {
      item.setIsHierarchical(((Long) cv.get("IS_HIERARCHICAL")) == 1);
    }
    if (cv.get("IS_PUBLIC") != null) {
      item.setIsPublic(((Long) cv.get("IS_PUBLIC")) == 1);
    }
    return item;
  }
}
