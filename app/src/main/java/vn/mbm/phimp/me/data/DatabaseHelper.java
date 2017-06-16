package vn.mbm.phimp.me.data;

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
}
