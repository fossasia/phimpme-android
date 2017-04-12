package com.wellsql.generated;

import com.yarolegovich.wellsql.core.TableClass;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import org.wordpress.android.fluxc.model.PostModel;

public final class PostModelTable implements TableClass {
  public static final String ID = "_id";

  public static final String LOCAL_SITE_ID = "LOCAL_SITE_ID";

  public static final String REMOTE_SITE_ID = "REMOTE_SITE_ID";

  public static final String REMOTE_POST_ID = "REMOTE_POST_ID";

  public static final String TITLE = "TITLE";

  public static final String CONTENT = "CONTENT";

  public static final String DATE_CREATED = "DATE_CREATED";

  public static final String CATEGORY_IDS = "CATEGORY_IDS";

  public static final String CUSTOM_FIELDS = "CUSTOM_FIELDS";

  public static final String LINK = "LINK";

  public static final String EXCERPT = "EXCERPT";

  public static final String TAG_NAMES = "TAG_NAMES";

  public static final String STATUS = "STATUS";

  public static final String PASSWORD = "PASSWORD";

  public static final String FEATURED_IMAGE_ID = "FEATURED_IMAGE_ID";

  public static final String POST_FORMAT = "POST_FORMAT";

  public static final String SLUG = "SLUG";

  public static final String LATITUDE = "LATITUDE";

  public static final String LONGITUDE = "LONGITUDE";

  public static final String IS_PAGE = "IS_PAGE";

  public static final String PARENT_ID = "PARENT_ID";

  public static final String PARENT_TITLE = "PARENT_TITLE";

  public static final String IS_LOCAL_DRAFT = "IS_LOCAL_DRAFT";

  public static final String IS_LOCALLY_CHANGED = "IS_LOCALLY_CHANGED";

  public static final String DATE_LOCALLY_CHANGED = "DATE_LOCALLY_CHANGED";

  public static final String LAST_KNOWN_REMOTE_FEATURED_IMAGE_ID = "LAST_KNOWN_REMOTE_FEATURED_IMAGE_ID";

  public static final String HAS_CAPABILITY_PUBLISH_POST = "HAS_CAPABILITY_PUBLISH_POST";

  public static final String HAS_CAPABILITY_EDIT_POST = "HAS_CAPABILITY_EDIT_POST";

  public static final String HAS_CAPABILITY_DELETE_POST = "HAS_CAPABILITY_DELETE_POST";

  @Override
  public String createStatement() {
    return "CREATE TABLE PostModel (_id INTEGER PRIMARY KEY AUTOINCREMENT,LOCAL_SITE_ID INTEGER,REMOTE_SITE_ID INTEGER,REMOTE_POST_ID INTEGER,TITLE TEXT,CONTENT TEXT,DATE_CREATED TEXT,CATEGORY_IDS TEXT,CUSTOM_FIELDS TEXT,LINK TEXT,EXCERPT TEXT,TAG_NAMES TEXT,STATUS TEXT,PASSWORD TEXT,FEATURED_IMAGE_ID INTEGER,POST_FORMAT TEXT,SLUG TEXT,LATITUDE REAL,LONGITUDE REAL,IS_PAGE INTEGER,PARENT_ID INTEGER,PARENT_TITLE TEXT,IS_LOCAL_DRAFT INTEGER,IS_LOCALLY_CHANGED INTEGER,DATE_LOCALLY_CHANGED TEXT,LAST_KNOWN_REMOTE_FEATURED_IMAGE_ID INTEGER,HAS_CAPABILITY_PUBLISH_POST INTEGER,HAS_CAPABILITY_EDIT_POST INTEGER,HAS_CAPABILITY_DELETE_POST INTEGER)";
  }

  @Override
  public String getTableName() {
    return "PostModel";
  }

  @Override
  public Class<?> getModelClass() {
    return PostModel.class;
  }

  @Override
  public boolean shouldAutoincrementId() {
    return true;
  }
}
