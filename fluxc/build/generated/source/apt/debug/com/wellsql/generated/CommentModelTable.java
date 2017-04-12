package com.wellsql.generated;

import com.yarolegovich.wellsql.core.TableClass;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import org.wordpress.android.fluxc.model.CommentModel;

public final class CommentModelTable implements TableClass {
  public static final String ID = "_id";

  public static final String REMOTE_COMMENT_ID = "REMOTE_COMMENT_ID";

  public static final String REMOTE_POST_ID = "REMOTE_POST_ID";

  public static final String REMOTE_PARENT_COMMENT_ID = "REMOTE_PARENT_COMMENT_ID";

  public static final String LOCAL_SITE_ID = "LOCAL_SITE_ID";

  public static final String REMOTE_SITE_ID = "REMOTE_SITE_ID";

  public static final String AUTHOR_URL = "AUTHOR_URL";

  public static final String AUTHOR_NAME = "AUTHOR_NAME";

  public static final String AUTHOR_EMAIL = "AUTHOR_EMAIL";

  public static final String AUTHOR_PROFILE_IMAGE_URL = "AUTHOR_PROFILE_IMAGE_URL";

  public static final String POST_TITLE = "POST_TITLE";

  public static final String STATUS = "STATUS";

  public static final String DATE_PUBLISHED = "DATE_PUBLISHED";

  public static final String CONTENT = "CONTENT";

  public static final String I_LIKE = "I_LIKE";

  @Override
  public String createStatement() {
    return "CREATE TABLE CommentModel (_id INTEGER PRIMARY KEY AUTOINCREMENT,REMOTE_COMMENT_ID INTEGER,REMOTE_POST_ID INTEGER,REMOTE_PARENT_COMMENT_ID INTEGER,LOCAL_SITE_ID INTEGER,REMOTE_SITE_ID INTEGER,AUTHOR_URL TEXT,AUTHOR_NAME TEXT,AUTHOR_EMAIL TEXT,AUTHOR_PROFILE_IMAGE_URL TEXT,POST_TITLE TEXT,STATUS TEXT,DATE_PUBLISHED TEXT,CONTENT TEXT,I_LIKE INTEGER)";
  }

  @Override
  public String getTableName() {
    return "CommentModel";
  }

  @Override
  public Class<?> getModelClass() {
    return CommentModel.class;
  }

  @Override
  public boolean shouldAutoincrementId() {
    return true;
  }
}
