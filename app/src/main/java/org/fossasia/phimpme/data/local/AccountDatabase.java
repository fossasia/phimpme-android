package org.fossasia.phimpme.data.local;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pa1pal on 08/06/17.
 *
 * Model class for accounts. Where we store relevant details related to accounts, which we required
 * to access their SDKs and APIs
 */

public class AccountDatabase extends RealmObject{

    /*Add hidden account option in last and increase the count option in hideInAccount variable*/


    public enum AccountName {
        FACEBOOK, TWITTER, NEXTCLOUD
        //, DRUPAL, WORDPRESS
        , PINTEREST, FLICKR, IMGUR, DROPBOX, OWNCLOUD, BOX, TUMBLR
        , INSTAGRAM, WHATSAPP, MESSENGER ,GOOGLEPLUS, OTHERS
    }
    public static int HIDEINACCOUNTS = 5;

    @PrimaryKey
    String name;
    String username;
    String userId;
    String token;
    String tokenSecret;
    String password;
    String serverUrl;

    public String accountname;
    public String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AccountName getAccountname() {
        return AccountName.valueOf(accountname);
    }

    public void setAccountname(AccountName accountname) {
        this.accountname = accountname.toString();
    }
}
