package com.wellsql.generated;

import com.yarolegovich.wellsql.core.TableClass;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import org.wordpress.android.fluxc.model.TaxonomyModel;

public final class TaxonomyModelTable implements TableClass {
  public static final String ID = "_id";

  public static final String LOCAL_SITE_ID = "LOCAL_SITE_ID";

  public static final String NAME = "NAME";

  public static final String LABEL = "LABEL";

  public static final String DESCRIPTION = "DESCRIPTION";

  public static final String IS_HIERARCHICAL = "IS_HIERARCHICAL";

  public static final String IS_PUBLIC = "IS_PUBLIC";

  @Override
  public String createStatement() {
    return "CREATE TABLE TaxonomyModel (_id INTEGER PRIMARY KEY AUTOINCREMENT,LOCAL_SITE_ID INTEGER,NAME TEXT,LABEL TEXT,DESCRIPTION TEXT,IS_HIERARCHICAL INTEGER,IS_PUBLIC INTEGER)";
  }

  @Override
  public String getTableName() {
    return "TaxonomyModel";
  }

  @Override
  public Class<?> getModelClass() {
    return TaxonomyModel.class;
  }

  @Override
  public boolean shouldAutoincrementId() {
    return true;
  }
}
