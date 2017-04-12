package com.wellsql.generated;

import com.yarolegovich.wellsql.core.Mapper;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import org.wordpress.android.fluxc.model.AccountModel;

public final class AccountModelMapper implements Mapper<AccountModel> {
  @Override
  public Map<String, Object> toContentValues(AccountModel item) {
    Map<String, Object> cv = new HashMap<String, Object>();
    cv.put("_id", item.getId());
    cv.put("USER_NAME", item.getUserName());
    cv.put("USER_ID", item.getUserId());
    cv.put("DISPLAY_NAME", item.getDisplayName());
    cv.put("PROFILE_URL", item.getProfileUrl());
    cv.put("AVATAR_URL", item.getAvatarUrl());
    cv.put("PRIMARY_SITE_ID", item.getPrimarySiteId());
    cv.put("EMAIL_VERIFIED", item.getEmailVerified());
    cv.put("SITE_COUNT", item.getSiteCount());
    cv.put("VISIBLE_SITE_COUNT", item.getVisibleSiteCount());
    cv.put("EMAIL", item.getEmail());
    cv.put("HAS_UNSEEN_NOTES", item.getHasUnseenNotes());
    cv.put("FIRST_NAME", item.getFirstName());
    cv.put("LAST_NAME", item.getLastName());
    cv.put("ABOUT_ME", item.getAboutMe());
    cv.put("DATE", item.getDate());
    cv.put("NEW_EMAIL", item.getNewEmail());
    cv.put("PENDING_EMAIL_CHANGE", item.getPendingEmailChange());
    cv.put("WEB_ADDRESS", item.getWebAddress());
    return cv;
  }

  @Override
  public AccountModel convert(Map<String, Object> cv) {
    AccountModel item = new AccountModel();
    if (cv.get("_id") != null) {
      item.setId(((Long) cv.get("_id")).intValue());
    }
    if (cv.get("USER_NAME") != null) {
      item.setUserName((String) cv.get("USER_NAME"));
    }
    if (cv.get("USER_ID") != null) {
      item.setUserId(((Long) cv.get("USER_ID")).longValue());
    }
    if (cv.get("DISPLAY_NAME") != null) {
      item.setDisplayName((String) cv.get("DISPLAY_NAME"));
    }
    if (cv.get("PROFILE_URL") != null) {
      item.setProfileUrl((String) cv.get("PROFILE_URL"));
    }
    if (cv.get("AVATAR_URL") != null) {
      item.setAvatarUrl((String) cv.get("AVATAR_URL"));
    }
    if (cv.get("PRIMARY_SITE_ID") != null) {
      item.setPrimarySiteId(((Long) cv.get("PRIMARY_SITE_ID")).longValue());
    }
    if (cv.get("EMAIL_VERIFIED") != null) {
      item.setEmailVerified(((Long) cv.get("EMAIL_VERIFIED")) == 1);
    }
    if (cv.get("SITE_COUNT") != null) {
      item.setSiteCount(((Long) cv.get("SITE_COUNT")).intValue());
    }
    if (cv.get("VISIBLE_SITE_COUNT") != null) {
      item.setVisibleSiteCount(((Long) cv.get("VISIBLE_SITE_COUNT")).intValue());
    }
    if (cv.get("EMAIL") != null) {
      item.setEmail((String) cv.get("EMAIL"));
    }
    if (cv.get("HAS_UNSEEN_NOTES") != null) {
      item.setHasUnseenNotes(((Long) cv.get("HAS_UNSEEN_NOTES")) == 1);
    }
    if (cv.get("FIRST_NAME") != null) {
      item.setFirstName((String) cv.get("FIRST_NAME"));
    }
    if (cv.get("LAST_NAME") != null) {
      item.setLastName((String) cv.get("LAST_NAME"));
    }
    if (cv.get("ABOUT_ME") != null) {
      item.setAboutMe((String) cv.get("ABOUT_ME"));
    }
    if (cv.get("DATE") != null) {
      item.setDate((String) cv.get("DATE"));
    }
    if (cv.get("NEW_EMAIL") != null) {
      item.setNewEmail((String) cv.get("NEW_EMAIL"));
    }
    if (cv.get("PENDING_EMAIL_CHANGE") != null) {
      item.setPendingEmailChange(((Long) cv.get("PENDING_EMAIL_CHANGE")) == 1);
    }
    if (cv.get("WEB_ADDRESS") != null) {
      item.setWebAddress((String) cv.get("WEB_ADDRESS"));
    }
    return item;
  }
}
