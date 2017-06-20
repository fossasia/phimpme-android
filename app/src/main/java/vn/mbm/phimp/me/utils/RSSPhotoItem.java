package vn.mbm.phimp.me.utils;

public class RSSPhotoItem 
{
	public String id;
	public String title;
	public String width;
	public String height;
	public String url;
	public String thumb;
	public String latitude;
	public String longitude;
	public String link;
	public String service;
	public String description;
	
	public String getID() { return id;	}
	public void setID(String id) { this.id = id; }
	
	public String getTitle() { return this.title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getWidth() { return this.width; }
	public void setWidth(String width) { this.width = width; }
	
	public String getHeight() { return this.height; }
	public void setHeight(String height) { this.height = height; }
	
	public String getURL() { return this.url; }
	public void setURL(String url) { this.url = url; }
	
	public String getThumb() { return this.thumb; }
	public void setThumb(String thumb) { this.thumb = thumb; }
	
	public String getLatitude() { return this.latitude; }
	public void setLatitude(String latitude) { this.latitude = latitude; }
	
	public String getLongitude() { return this.longitude; }
	public void setLongitude(String longitude) { this.longitude = longitude; }
	
	public String getLink() { return this.link; }
	public void setLink(String link) { this.link = link; }
	
	public String getService() { return this.service; }
	public void setService(String service) { this.service = service; }
	
	public String getDescription() { return this.description; }
	public void setDescription(String description) { this.description = description; }
}
