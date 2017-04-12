package com.wellsql.generated;

import com.yarolegovich.wellsql.core.TableClass;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import org.wordpress.android.fluxc.model.PostFormatModel;

public final class PostFormatModelTable implements TableClass {
  public static final String ID = "_id";

  public static final String SITE_ID = "SITE_ID";

  public static final String SLUG = "SLUG";

  public static final String DISPLAY_NAME = "DISPLAY_NAME";

  @Override
  public String createStatement() {
    return "CREATE TABLE PostFormatModel (_id INTEGER PRIMARY KEY AUTOINCREMENT,SITE_ID INTEGER,SLUG TEXT,DISPLAY_NAME TEXT)";
  }

  @Override
  public String getTableName() {
    return "PostFormatModel";
  }

  @Override
  public Class<?> getModelClass() {
    return PostFormatModel.class;
  }

  @Override
  public boolean shouldAutoincrementId() {
    return true;
  }
}
