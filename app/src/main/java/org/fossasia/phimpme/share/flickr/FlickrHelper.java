package org.fossasia.phimpme.share.flickr;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.share.flickr.tasks.UploadPhotoTask;
import org.fossasia.phimpme.utilities.Constants;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.FLICKR;

public final class FlickrHelper {

    private static FlickrHelper instance = null;
    private InputStream photoStream;
    private String filename;
    private String description;

    private FlickrHelper() {

    }

    public static FlickrHelper getInstance() {
        if (instance == null) {
            instance = new FlickrHelper();
        }

        return instance;
    }

    public static OAuth getOAuthToken() {
        Realm realm = Realm.getDefaultInstance();
        AccountDatabase account;
        RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);
        query.equalTo("name", FLICKR.toString());
        RealmResults<AccountDatabase> result = query.findAll();
        if (result.size() != 0) {
            account = result.get(0);
            String oauthTokenString = account.getToken();
            String tokenSecret = account.getTokenSecret();

            if (oauthTokenString == null && tokenSecret == null) {
                return null;
            }
            OAuth oauth = new OAuth();
            String userName = account.getUsername();
            String userId = account.getUserId();
            if (userId != null) {
                User user = new User();
                user.setUsername(userName);
                user.setId(userId);
                oauth.setUser(user);
            }
            OAuthToken oauthToken = new OAuthToken();
            oauth.setToken(oauthToken);
            oauthToken.setOauthToken(oauthTokenString);
            oauthToken.setOauthTokenSecret(tokenSecret);
            return oauth;
        } else
            return null;
    }

    public Flickr getFlickr() {
        try {
            Flickr f = new Flickr(Constants.FLICKR_API_KEY, Constants.FLICKR_TOKEN_SECRET, new REST());
            return f;
        } catch (ParserConfigurationException e) {
            return null;
        }
    }

    public Flickr getFlickrAuthed(String token, String secret) {
        Flickr f = getFlickr();
        RequestContext requestContext = RequestContext.getRequestContext();
        OAuth auth = new OAuth();
        auth.setToken(new OAuthToken(token, secret));
        requestContext.setOAuth(auth);
        return f;
    }

    public void setFilename(String file) {
        this.filename = file;
    }

    public InputStream getInputStream() {
        return photoStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.photoStream = inputStream;
    }

    public String getFileName() {
        return filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void uploadImage() {
        UploadPhotoTask taskUpload = new UploadPhotoTask();
        taskUpload.setOnUploadDone(new UploadPhotoTask.onUploadDone() {
            @Override
            public void onComplete() {
                // callback if uploaded successfully
            }
        });
        taskUpload.execute(getOAuthToken());
    }
}
