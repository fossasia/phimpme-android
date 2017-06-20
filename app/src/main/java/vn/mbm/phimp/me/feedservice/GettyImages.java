package vn.mbm.phimp.me.feedservice;

import android.content.SharedPreferences;

public class GettyImages 
{
	static final String TAG = "gettyimages";
	
	static boolean[] channels_status = {
		false,
		false,
		false,
		false,
		false,
		false,
		false,
		false,
		false,
		false,
		false,
		false
	};
	
	static final String[] channels_key = {
		"feeds_gettyimages_recent_photos",
		"feeds_gettyimages_recent_editorial_entertainment",
		"feeds_gettyimages_recent_editorial_features",
		"feeds_gettyimages_recent_editorial_news",
		"feeds_gettyimages_recent_editorial_sports",
		"feeds_gettyimages_entertainment_photos",
		"feeds_gettyimages_news_photos",
		"feeds_gettyimages_sports_photos",
		"feeds_gettyimages_creative_rf_photos",
		"feeds_gettyimages_creative_rm_photos",
		"feeds_gettyimages_most_mailed",
		"feeds_gettyimages_highest_rated"
	};
	
	static final String[] feeds = {
		"Recent Photos",
		"Editorial Entertainment Events",
		"Editorial Features Events",
		"Editorial News Events",
		"Editorial Sports Events",
		"Editorial Entertainment Photos",
		"Editorial News Photos",
		"Editorial Sports Photos",
		"Creative Royalty Free Photos",
		"Creative Rights Managed Photos",
		"Consumers Most Emailed Photos",
		"Consumers Highest Rated Photos"
	};
	
	static final String[] channels = {
		"http://feeds.gettyimages.com/channels/RecentPhotos.rss",
		"http://feeds.gettyimages.com/channels/RecentEditorialEntertainment.rss", 
		"http://feeds.gettyimages.com/channels/RecentEditorialFeatures.rss",
		"http://feeds.gettyimages.com/channels/RecentEditorialNews.rss",
		"http://feeds.gettyimages.com/channels/RecentEditorialSports.rss",
		"http://feeds.gettyimages.com/channels/EntertainmentPhotos.rss",
		"http://feeds.gettyimages.com/channels/NewsPhotos.rss",
		"http://feeds.gettyimages.com/channels/SportsPhotos.rss",
		"http://feeds.gettyimages.com/channels/RecentCreativeRFPhotos.rss",
		"http://feeds.gettyimages.com/channels/RecentCreativeRMPhotos.rss",
		"http://feeds.gettyimages.com/channels/dynamic/mostemailed.rss",
		"http://feeds.gettyimages.com/channels/dynamic/highestrated.rss"
	};
	
	public static void getPref(SharedPreferences sharedprefs)
	{
		for (int i = 0; i < channels_key.length; i++)
		{
			channels_status[i] = sharedprefs.getBoolean(channels_key[i], false);
		}
	}
	
	public static void savePref(SharedPreferences sharedprefs)
	{
		SharedPreferences.Editor editor = sharedprefs.edit();
		for (int i = 0; i < channels_key.length; i++)
		{
			editor.putBoolean(channels_key[i], channels_status[i]);
		}
		editor.commit();
	}
}
