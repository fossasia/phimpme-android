package org.fossasia.phimpme.data;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by pa1pal on 10/6/17.
 */

public class DatabaseHelper {

    private Realm realm;

    public DatabaseHelper(Realm realm) {
        this.realm = realm;
    }

    public RealmQuery<AccountDatabase> fetchAccountDetails(){
        return realm.where(AccountDatabase.class);
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
}
