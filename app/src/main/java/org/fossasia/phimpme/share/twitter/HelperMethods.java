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

import static org.fossasia.phimpme.utilities.Constants.TWITTER_CONSUMER_KEY;
import static org.fossasia.phimpme.utilities.Constants.TWITTER_CONSUMER_SECRET;

import android.content.Context;
import android.util.Log;
import java.io.File;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class HelperMethods {
  private static final String TAG = "HelperMethods";

  public static void postToTwitterWithImage(
      Context context,
      final String imageUrl,
      final String message,
      final String token,
      final String secret,
      final TwitterCallback postResponse) {
    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
    configurationBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
    configurationBuilder.setOAuthAccessToken(token);
    configurationBuilder.setOAuthAccessTokenSecret(secret);
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
    } else {
      Log.d(TAG, "----- Invalid File ----------");
      success = false;
    }
    postResponse.onFinsihed(success);
  }

  public abstract static class TwitterCallback {
    public abstract void onFinsihed(Boolean success);
  }
}
