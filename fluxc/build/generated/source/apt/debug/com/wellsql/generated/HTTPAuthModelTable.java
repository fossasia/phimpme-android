package com.wellsql.generated;

import com.yarolegovich.wellsql.core.TableClass;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import org.wordpress.android.fluxc.network.HTTPAuthModel;

public final class HTTPAuthModelTable implements TableClass {
  public static final String ID = "_id";

  public static final String ROOT_URL = "ROOT_URL";

  public static final String REALM = "REALM";

  public static final String USERNAME = "USERNAME";

  public static final String PASSWORD = "PASSWORD";

  @Override
  public String createStatement() {
    return "CREATE TABLE HTTPAuthModel (_id INTEGER PRIMARY KEY AUTOINCREMENT,ROOT_URL TEXT,REALM TEXT,USERNAME TEXT,PASSWORD TEXT,UNIQUE (ROOT_URL))";
  }

  @Override
  public String getTableName() {
    return "HTTPAuthModel";
  }

  @Override
  public Class<?> getModelClass() {
    return HTTPAuthModel.class;
  }

  @Override
  public boolean shouldAutoincrementId() {
    return true;
  }
}
