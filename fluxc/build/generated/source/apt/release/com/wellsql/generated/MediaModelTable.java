package com.wellsql.generated;

import com.yarolegovich.wellsql.core.TableClass;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import org.wordpress.android.fluxc.model.MediaModel;

public final class MediaModelTable implements TableClass {
  public static final String ID = "_id";

  public static final String LOCAL_SITE_ID = "LOCAL_SITE_ID";

  public static final String MEDIA_ID = "MEDIA_ID";

  public static final String POST_ID = "POST_ID";

  public static final String AUTHOR_ID = "AUTHOR_ID";

  public static final String GUID = "GUID";

  public static final String UPLOAD_DATE = "UPLOAD_DATE";

  public static final String URL = "URL";

  public static final String THUMBNAIL_URL = "THUMBNAIL_URL";

  public static final String FILE_NAME = "FILE_NAME";

  public static final String FILE_PATH = "FILE_PATH";

  public static final String FILE_EXTENSION = "FILE_EXTENSION";

  public static final String MIME_TYPE = "MIME_TYPE";

  public static final String TITLE = "TITLE";

  public static final String CAPTION = "CAPTION";

  public static final String DESCRIPTION = "DESCRIPTION";

  public static final String ALT = "ALT";

  public static final String WIDTH = "WIDTH";

  public static final String HEIGHT = "HEIGHT";

  public static final String LENGTH = "LENGTH";

  public static final String VIDEO_PRESS_GUID = "VIDEO_PRESS_GUID";

  public static final String VIDEO_PRESS_PROCESSING_DONE = "VIDEO_PRESS_PROCESSING_DONE";

  public static final String UPLOAD_STATE = "UPLOAD_STATE";

  public static final String HORIZONTAL_ALIGNMENT = "HORIZONTAL_ALIGNMENT";

  public static final String VERTICAL_ALIGNMENT = "VERTICAL_ALIGNMENT";

  public static final String FEATURED = "FEATURED";

  public static final String FEATURED_IN_POST = "FEATURED_IN_POST";

  @Override
  public String createStatement() {
    return "CREATE TABLE MediaModel (_id INTEGER PRIMARY KEY AUTOINCREMENT,LOCAL_SITE_ID INTEGER,MEDIA_ID INTEGER,POST_ID INTEGER,AUTHOR_ID INTEGER,GUID TEXT,UPLOAD_DATE TEXT,URL TEXT,THUMBNAIL_URL TEXT,FILE_NAME TEXT,FILE_PATH TEXT,FILE_EXTENSION TEXT,MIME_TYPE TEXT,TITLE TEXT,CAPTION TEXT,DESCRIPTION TEXT,ALT TEXT,WIDTH INTEGER,HEIGHT INTEGER,LENGTH INTEGER,VIDEO_PRESS_GUID TEXT,VIDEO_PRESS_PROCESSING_DONE INTEGER,UPLOAD_STATE TEXT,HORIZONTAL_ALIGNMENT INTEGER,VERTICAL_ALIGNMENT INTEGER,FEATURED INTEGER,FEATURED_IN_POST INTEGER)";
  }

  @Override
  public String getTableName() {
    return "MediaModel";
  }

  @Override
  public Class<?> getModelClass() {
    return MediaModel.class;
  }

  @Override
  public boolean shouldAutoincrementId() {
    return true;
  }
}
