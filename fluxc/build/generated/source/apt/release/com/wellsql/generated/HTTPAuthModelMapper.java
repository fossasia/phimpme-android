package com.wellsql.generated;

import com.yarolegovich.wellsql.core.Mapper;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import org.wordpress.android.fluxc.network.HTTPAuthModel;

public final class HTTPAuthModelMapper implements Mapper<HTTPAuthModel> {
  @Override
  public Map<String, Object> toContentValues(HTTPAuthModel item) {
    Map<String, Object> cv = new HashMap<String, Object>();
    cv.put("_id", item.getId());
    cv.put("ROOT_URL", item.getRootUrl());
    cv.put("REALM", item.getRealm());
    cv.put("USERNAME", item.getUsername());
    cv.put("PASSWORD", item.getPassword());
    return cv;
  }

  @Override
  public HTTPAuthModel convert(Map<String, Object> cv) {
    HTTPAuthModel item = new HTTPAuthModel();
    if (cv.get("_id") != null) {
      item.setId(((Long) cv.get("_id")).intValue());
    }
    if (cv.get("ROOT_URL") != null) {
      item.setRootUrl((String) cv.get("ROOT_URL"));
    }
    if (cv.get("REALM") != null) {
      item.setRealm((String) cv.get("REALM"));
    }
    if (cv.get("USERNAME") != null) {
      item.setUsername((String) cv.get("USERNAME"));
    }
    if (cv.get("PASSWORD") != null) {
      item.setPassword((String) cv.get("PASSWORD"));
    }
    return item;
  }
}
