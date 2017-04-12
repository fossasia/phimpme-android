package com.wellsql.generated;

import com.yarolegovich.wellsql.core.Mapper;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import org.wordpress.android.fluxc.model.SiteModel;

public final class SiteModelMapper implements Mapper<SiteModel> {
  @Override
  public Map<String, Object> toContentValues(SiteModel item) {
    Map<String, Object> cv = new HashMap<String, Object>();
    cv.put("_id", item.getId());
    cv.put("SITE_ID", item.getSiteId());
    cv.put("URL", item.getUrl());
    cv.put("ADMIN_URL", item.getAdminUrl());
    cv.put("LOGIN_URL", item.getLoginUrl());
    cv.put("NAME", item.getName());
    cv.put("DESCRIPTION", item.getDescription());
    cv.put("IS_WPCOM", item.isWPCom());
    cv.put("IS_FEATURED_IMAGE_SUPPORTED", item.isFeaturedImageSupported());
    cv.put("DEFAULT_COMMENT_STATUS", item.getDefaultCommentStatus());
    cv.put("TIMEZONE", item.getTimezone());
    cv.put("FRAME_NONCE", item.getFrameNonce());
    cv.put("SELF_HOSTED_SITE_ID", item.getSelfHostedSiteId());
    cv.put("USERNAME", item.getUsername());
    cv.put("PASSWORD", item.getPassword());
    cv.put("XMLRPC_URL", item.getXmlRpcUrl());
    cv.put("SOFTWARE_VERSION", item.getSoftwareVersion());
    cv.put("IS_SELF_HOSTED_ADMIN", item.isSelfHostedAdmin());
    cv.put("IS_JETPACK_INSTALLED", item.isJetpackInstalled());
    cv.put("IS_JETPACK_CONNECTED", item.isJetpackConnected());
    cv.put("IS_AUTOMATED_TRANSFER", item.isAutomatedTransfer());
    cv.put("IS_VISIBLE", item.isVisible());
    cv.put("IS_PRIVATE", item.isPrivate());
    cv.put("IS_VIDEO_PRESS_SUPPORTED", item.isVideoPressSupported());
    cv.put("PLAN_ID", item.getPlanId());
    cv.put("PLAN_SHORT_NAME", item.getPlanShortName());
    cv.put("ICON_URL", item.getIconUrl());
    cv.put("HAS_CAPABILITY_EDIT_PAGES", item.getHasCapabilityEditPages());
    cv.put("HAS_CAPABILITY_EDIT_POSTS", item.getHasCapabilityEditPosts());
    cv.put("HAS_CAPABILITY_EDIT_OTHERS_POSTS", item.getHasCapabilityEditOthersPosts());
    cv.put("HAS_CAPABILITY_EDIT_OTHERS_PAGES", item.getHasCapabilityEditOthersPages());
    cv.put("HAS_CAPABILITY_DELETE_POSTS", item.getHasCapabilityDeletePosts());
    cv.put("HAS_CAPABILITY_DELETE_OTHERS_POSTS", item.getHasCapabilityDeleteOthersPosts());
    cv.put("HAS_CAPABILITY_EDIT_THEME_OPTIONS", item.getHasCapabilityEditThemeOptions());
    cv.put("HAS_CAPABILITY_EDIT_USERS", item.getHasCapabilityEditUsers());
    cv.put("HAS_CAPABILITY_LIST_USERS", item.getHasCapabilityListUsers());
    cv.put("HAS_CAPABILITY_MANAGE_CATEGORIES", item.getHasCapabilityManageCategories());
    cv.put("HAS_CAPABILITY_MANAGE_OPTIONS", item.getHasCapabilityManageOptions());
    cv.put("HAS_CAPABILITY_ACTIVATE_WORDADS", item.getHasCapabilityActivateWordads());
    cv.put("HAS_CAPABILITY_PROMOTE_USERS", item.getHasCapabilityPromoteUsers());
    cv.put("HAS_CAPABILITY_PUBLISH_POSTS", item.getHasCapabilityPublishPosts());
    cv.put("HAS_CAPABILITY_UPLOAD_FILES", item.getHasCapabilityUploadFiles());
    cv.put("HAS_CAPABILITY_DELETE_USER", item.getHasCapabilityDeleteUser());
    cv.put("HAS_CAPABILITY_REMOVE_USERS", item.getHasCapabilityRemoveUsers());
    cv.put("HAS_CAPABILITY_VIEW_STATS", item.getHasCapabilityViewStats());
    return cv;
  }

  @Override
  public SiteModel convert(Map<String, Object> cv) {
    SiteModel item = new SiteModel();
    if (cv.get("_id") != null) {
      item.setId(((Long) cv.get("_id")).intValue());
    }
    if (cv.get("SITE_ID") != null) {
      item.setSiteId(((Long) cv.get("SITE_ID")).longValue());
    }
    if (cv.get("URL") != null) {
      item.setUrl((String) cv.get("URL"));
    }
    if (cv.get("ADMIN_URL") != null) {
      item.setAdminUrl((String) cv.get("ADMIN_URL"));
    }
    if (cv.get("LOGIN_URL") != null) {
      item.setLoginUrl((String) cv.get("LOGIN_URL"));
    }
    if (cv.get("NAME") != null) {
      item.setName((String) cv.get("NAME"));
    }
    if (cv.get("DESCRIPTION") != null) {
      item.setDescription((String) cv.get("DESCRIPTION"));
    }
    if (cv.get("IS_WPCOM") != null) {
      item.setIsWPCom(((Long) cv.get("IS_WPCOM")) == 1);
    }
    if (cv.get("IS_FEATURED_IMAGE_SUPPORTED") != null) {
      item.setIsFeaturedImageSupported(((Long) cv.get("IS_FEATURED_IMAGE_SUPPORTED")) == 1);
    }
    if (cv.get("DEFAULT_COMMENT_STATUS") != null) {
      item.setDefaultCommentStatus((String) cv.get("DEFAULT_COMMENT_STATUS"));
    }
    if (cv.get("TIMEZONE") != null) {
      item.setTimezone((String) cv.get("TIMEZONE"));
    }
    if (cv.get("FRAME_NONCE") != null) {
      item.setFrameNonce((String) cv.get("FRAME_NONCE"));
    }
    if (cv.get("SELF_HOSTED_SITE_ID") != null) {
      item.setSelfHostedSiteId(((Long) cv.get("SELF_HOSTED_SITE_ID")).longValue());
    }
    if (cv.get("USERNAME") != null) {
      item.setUsername((String) cv.get("USERNAME"));
    }
    if (cv.get("PASSWORD") != null) {
      item.setPassword((String) cv.get("PASSWORD"));
    }
    if (cv.get("XMLRPC_URL") != null) {
      item.setXmlRpcUrl((String) cv.get("XMLRPC_URL"));
    }
    if (cv.get("SOFTWARE_VERSION") != null) {
      item.setSoftwareVersion((String) cv.get("SOFTWARE_VERSION"));
    }
    if (cv.get("IS_SELF_HOSTED_ADMIN") != null) {
      item.setIsSelfHostedAdmin(((Long) cv.get("IS_SELF_HOSTED_ADMIN")) == 1);
    }
    if (cv.get("IS_JETPACK_INSTALLED") != null) {
      item.setIsJetpackInstalled(((Long) cv.get("IS_JETPACK_INSTALLED")) == 1);
    }
    if (cv.get("IS_JETPACK_CONNECTED") != null) {
      item.setIsJetpackConnected(((Long) cv.get("IS_JETPACK_CONNECTED")) == 1);
    }
    if (cv.get("IS_AUTOMATED_TRANSFER") != null) {
      item.setIsAutomatedTransfer(((Long) cv.get("IS_AUTOMATED_TRANSFER")) == 1);
    }
    if (cv.get("IS_VISIBLE") != null) {
      item.setIsVisible(((Long) cv.get("IS_VISIBLE")) == 1);
    }
    if (cv.get("IS_PRIVATE") != null) {
      item.setIsPrivate(((Long) cv.get("IS_PRIVATE")) == 1);
    }
    if (cv.get("IS_VIDEO_PRESS_SUPPORTED") != null) {
      item.setIsVideoPressSupported(((Long) cv.get("IS_VIDEO_PRESS_SUPPORTED")) == 1);
    }
    if (cv.get("PLAN_ID") != null) {
      item.setPlanId(((Long) cv.get("PLAN_ID")).longValue());
    }
    if (cv.get("PLAN_SHORT_NAME") != null) {
      item.setPlanShortName((String) cv.get("PLAN_SHORT_NAME"));
    }
    if (cv.get("ICON_URL") != null) {
      item.setIconUrl((String) cv.get("ICON_URL"));
    }
    if (cv.get("HAS_CAPABILITY_EDIT_PAGES") != null) {
      item.setHasCapabilityEditPages(((Long) cv.get("HAS_CAPABILITY_EDIT_PAGES")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_EDIT_POSTS") != null) {
      item.setHasCapabilityEditPosts(((Long) cv.get("HAS_CAPABILITY_EDIT_POSTS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_EDIT_OTHERS_POSTS") != null) {
      item.setHasCapabilityEditOthersPosts(((Long) cv.get("HAS_CAPABILITY_EDIT_OTHERS_POSTS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_EDIT_OTHERS_PAGES") != null) {
      item.setHasCapabilityEditOthersPages(((Long) cv.get("HAS_CAPABILITY_EDIT_OTHERS_PAGES")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_DELETE_POSTS") != null) {
      item.setHasCapabilityDeletePosts(((Long) cv.get("HAS_CAPABILITY_DELETE_POSTS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_DELETE_OTHERS_POSTS") != null) {
      item.setHasCapabilityDeleteOthersPosts(((Long) cv.get("HAS_CAPABILITY_DELETE_OTHERS_POSTS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_EDIT_THEME_OPTIONS") != null) {
      item.setHasCapabilityEditThemeOptions(((Long) cv.get("HAS_CAPABILITY_EDIT_THEME_OPTIONS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_EDIT_USERS") != null) {
      item.setHasCapabilityEditUsers(((Long) cv.get("HAS_CAPABILITY_EDIT_USERS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_LIST_USERS") != null) {
      item.setHasCapabilityListUsers(((Long) cv.get("HAS_CAPABILITY_LIST_USERS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_MANAGE_CATEGORIES") != null) {
      item.setHasCapabilityManageCategories(((Long) cv.get("HAS_CAPABILITY_MANAGE_CATEGORIES")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_MANAGE_OPTIONS") != null) {
      item.setHasCapabilityManageOptions(((Long) cv.get("HAS_CAPABILITY_MANAGE_OPTIONS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_ACTIVATE_WORDADS") != null) {
      item.setHasCapabilityActivateWordads(((Long) cv.get("HAS_CAPABILITY_ACTIVATE_WORDADS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_PROMOTE_USERS") != null) {
      item.setHasCapabilityPromoteUsers(((Long) cv.get("HAS_CAPABILITY_PROMOTE_USERS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_PUBLISH_POSTS") != null) {
      item.setHasCapabilityPublishPosts(((Long) cv.get("HAS_CAPABILITY_PUBLISH_POSTS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_UPLOAD_FILES") != null) {
      item.setHasCapabilityUploadFiles(((Long) cv.get("HAS_CAPABILITY_UPLOAD_FILES")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_DELETE_USER") != null) {
      item.setHasCapabilityDeleteUser(((Long) cv.get("HAS_CAPABILITY_DELETE_USER")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_REMOVE_USERS") != null) {
      item.setHasCapabilityRemoveUsers(((Long) cv.get("HAS_CAPABILITY_REMOVE_USERS")) == 1);
    }
    if (cv.get("HAS_CAPABILITY_VIEW_STATS") != null) {
      item.setHasCapabilityViewStats(((Long) cv.get("HAS_CAPABILITY_VIEW_STATS")) == 1);
    }
    return item;
  }
}
