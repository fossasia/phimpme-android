package org.fossasia.phimpme.data.local;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by pa1pal on 10/6/17.
 */

public class DatabaseHelper {

    private Realm realm;

    public DatabaseHelper(Realm realm) {
        this.realm = realm;
    }

    public RealmResults<AccountDatabase> fetchAccountDetails(){
        return realm.where(AccountDatabase.class).findAll();
    }

    public void deleteSignedOutAccount(String accountName){
        final RealmResults<AccountDatabase> deletionQueryResult =  realm.where(AccountDatabase.class)
                .equalTo("name", accountName).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                deletionQueryResult.deleteAllFromRealm();
            }
        });
    }

    /**
     * Store the image description
     * @param item Image desc model object
     */
    public void addImageDesc(ImageDescModel item) {
        realm.beginTransaction();
        ImageDescModel u = realm.createObject(ImageDescModel.class, item.getId());
        u.setTitle(item.getTitle());
        realm.commitTransaction();
    }

    /**
     * Description is getting through the match of path of the image
     * @param path Path passes as a parameter
     * @return model object
     */
    public ImageDescModel getImageDesc(String path) {
        ImageDescModel result = realm.where(ImageDescModel.class)
                .equalTo("path", path).findFirst();
        return result;

    }

    public void update(ImageDescModel item) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }
}
