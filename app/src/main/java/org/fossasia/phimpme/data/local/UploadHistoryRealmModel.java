package org.fossasia.phimpme.data.local;

import io.realm.RealmObject;

/**
 * Created by pa1pal on 16/08/17.
 */

public class UploadHistoryRealmModel extends RealmObject{

    String name;
    String pathname;
    String datetime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
