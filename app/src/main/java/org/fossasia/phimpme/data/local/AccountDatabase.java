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

    public enum AccountName {
        FACEBOOK, TWITTER, DRUPAL, NEXTCLOUD, WORDPRESS, PINTEREST, FLICKR, IMGUR, DROPBOX, OWNCLOUD
        , GOOGLEPLUS, BOX
    }

    @PrimaryKey
    String name;
    String username;
    String token;
    String secret;
    String password;
    String serverUrl;
    public String accountname;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
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
