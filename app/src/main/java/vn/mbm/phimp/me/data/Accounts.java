package vn.mbm.phimp.me.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pa1pal on 4/6/17.
 */

public class Accounts extends RealmObject {
    @PrimaryKey
    private String name;
    private String username;
    private String email;
    private String accessToken;
    private String secretKey;
}
