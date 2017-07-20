package org.fossasia.phimpme.share.flickr;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

import org.fossasia.phimpme.utilities.Constants;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

public final class FlickrHelper {

	private static FlickrHelper instance = null;
	private static final String API_KEY = Constants.FLICKR_API_KEY;
	private static final String API_SEC = Constants.FLICKR_TOKEN_SECRET;
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

	public Flickr getFlickr() {
		try {
			Flickr f = new Flickr(API_KEY, API_SEC, new REST());
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

	public InterestingnessInterface getInterestingInterface() {
		Flickr f = getFlickr();
		if (f != null) {
			return f.getInterestingnessInterface();
		} else {
			return null;
		}
	}

	public PhotosInterface getPhotosInterface() {
		Flickr f = getFlickr();
		if (f != null) {
			return f.getPhotosInterface();
		} else {
			return null;
		}
	}

	public void setInputStream(InputStream inputStream){
		this.photoStream = inputStream;
	}
	public void setFilename(String file){
		this.filename = file;
	}
	public void setDescription(String description){
		this.description = description;
	}
	public InputStream getInputStream(){
		return photoStream;
	}
	public String getFileName(){
		return filename;
	}
	public String getDescription(){
		return description;
	}


}
