package org.fossasia.phimpme.leafpic.data;

import android.app.Application;

import org.fossasia.phimpme.leafpic.data.providers.Item;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by nirvan on 20/6/17.
 */

public class RealmController {
    private Realm realm;

    public RealmController() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
    }


    public void addItem(Item item) {

        realm.beginTransaction();
        Item u = realm.createObject(Item.class,item.getId());
        //u.setId(item.getId());
        u.setTitle(item.getTitle());
        realm.commitTransaction();
    }


    public Item getItem(String path) {

        Item result = realm.where(Item.class).equalTo("path", path).findFirst();
        return result;

    }

    public void update(Item item) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

}
