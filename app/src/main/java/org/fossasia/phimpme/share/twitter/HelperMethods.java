/*
 * Copyright 2013 - learnNcode (learnncode@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.fossasia.phimpme.share.twitter;

import android.content.Context;
import android.util.Log;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.AccountDatabase;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class HelperMethods {
	private static final String TAG = "HelperMethods";

	public static void postToTwitterWithImage(Context context, final String imageUrl, final String message, final TwitterCallback postResponse){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);
        query.equalTo("name", "Twitter");
        RealmResults<AccountDatabase> result = query.findAll();

		if(!LoginActivity.isActive(context)){
			postResponse.onFinsihed(false);
			return;
		}

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(context.getResources().getString(R.string.twitter_CONSUMER_KEY));
		configurationBuilder.setOAuthConsumerSecret(context.getResources().getString(R.string.twitter_CONSUMER_SECRET));
		//configurationBuilder.setOAuthAccessToken(LoginActivity.getAccessToken((context)));
		configurationBuilder.setOAuthAccessToken(result.get(0).getToken());
		//configurationBuilder.setOAuthAccessTokenSecret(LoginActivity.getAccessTokenSecret(context));
		configurationBuilder.setOAuthAccessTokenSecret(result.get(0).getSecret());
		Configuration configuration = configurationBuilder.build();
		final Twitter twitter = new TwitterFactory(configuration).getInstance();

		final File file = new File(imageUrl);
		boolean success = true;
		if (file.exists()) {
			try {
			StatusUpdate status = new StatusUpdate(message);
			status.setMedia(file);
				twitter.updateStatus(status);
			} catch (TwitterException e) {
				e.printStackTrace();
				success = false;
			}
		}else{
			Log.d(TAG, "----- Invalid File ----------");
			success = false;
		}
		postResponse.onFinsihed(success);

	}

	public static abstract class TwitterCallback{
		public abstract void onFinsihed(Boolean success);
	}
}
