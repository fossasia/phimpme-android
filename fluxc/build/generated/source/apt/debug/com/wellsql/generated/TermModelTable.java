package com.wellsql.generated;

import com.yarolegovich.wellsql.core.TableClass;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import org.wordpress.android.fluxc.model.TermModel;

public final class TermModelTable implements TableClass {
  public static final String ID = "_id";

  public static final String LOCAL_SITE_ID = "LOCAL_SITE_ID";

  public static final String REMOTE_TERM_ID = "REMOTE_TERM_ID";

  public static final String TAXONOMY = "TAXONOMY";

  public static final String NAME = "NAME";

  public static final String SLUG = "SLUG";

  public static final String DESCRIPTION = "DESCRIPTION";

  public static final String PARENT_REMOTE_ID = "PARENT_REMOTE_ID";

  @Override
  public String createStatement() {
    return "CREATE TABLE TermModel (_id INTEGER PRIMARY KEY AUTOINCREMENT,LOCAL_SITE_ID INTEGER,REMOTE_TERM_ID INTEGER,TAXONOMY TEXT,NAME TEXT,SLUG TEXT,DESCRIPTION TEXT,PARENT_REMOTE_ID INTEGER)";
  }

  @Override
  public String getTableName() {
    return "TermModel";
  }

  @Override
  public Class<?> getModelClass() {
    return TermModel.class;
  }

  @Override
  public boolean shouldAutoincrementId() {
    return true;
  }
}
