package com.wellsql.generated;

import com.yarolegovich.wellsql.core.TableClass;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import org.wordpress.android.fluxc.model.AccountModel;

public final class AccountModelTable implements TableClass {
  public static final String ID = "_id";

  public static final String USER_NAME = "USER_NAME";

  public static final String USER_ID = "USER_ID";

  public static final String DISPLAY_NAME = "DISPLAY_NAME";

  public static final String PROFILE_URL = "PROFILE_URL";

  public static final String AVATAR_URL = "AVATAR_URL";

  public static final String PRIMARY_SITE_ID = "PRIMARY_SITE_ID";

  public static final String EMAIL_VERIFIED = "EMAIL_VERIFIED";

  public static final String SITE_COUNT = "SITE_COUNT";

  public static final String VISIBLE_SITE_COUNT = "VISIBLE_SITE_COUNT";

  public static final String EMAIL = "EMAIL";

  public static final String HAS_UNSEEN_NOTES = "HAS_UNSEEN_NOTES";

  public static final String FIRST_NAME = "FIRST_NAME";

  public static final String LAST_NAME = "LAST_NAME";

  public static final String ABOUT_ME = "ABOUT_ME";

  public static final String DATE = "DATE";

  public static final String NEW_EMAIL = "NEW_EMAIL";

  public static final String PENDING_EMAIL_CHANGE = "PENDING_EMAIL_CHANGE";

  public static final String WEB_ADDRESS = "WEB_ADDRESS";

  @Override
  public String createStatement() {
    return "CREATE TABLE AccountModel (_id INTEGER PRIMARY KEY,USER_NAME TEXT,USER_ID INTEGER,DISPLAY_NAME TEXT,PROFILE_URL TEXT,AVATAR_URL TEXT,PRIMARY_SITE_ID INTEGER,EMAIL_VERIFIED INTEGER,SITE_COUNT INTEGER,VISIBLE_SITE_COUNT INTEGER,EMAIL TEXT,HAS_UNSEEN_NOTES INTEGER,FIRST_NAME TEXT,LAST_NAME TEXT,ABOUT_ME TEXT,DATE TEXT,NEW_EMAIL TEXT,PENDING_EMAIL_CHANGE INTEGER,WEB_ADDRESS TEXT)";
  }

  @Override
  public String getTableName() {
    return "AccountModel";
  }

  @Override
  public Class<?> getModelClass() {
    return AccountModel.class;
  }

  @Override
  public boolean shouldAutoincrementId() {
    return false;
  }
}
