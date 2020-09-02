package org.fossasia.phimpme.leafpic.activities;

import static android.content.Context.KEYGUARD_SERVICE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.app.KeyguardManager;
import android.view.WindowManager;
import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.gallery.activities.SettingsActivity;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {

  @Rule
  public ActivityTestRule<SettingsActivity> mActivityTestRule =
      new ActivityTestRule<>(SettingsActivity.class);

  @UiThreadTest
  @Before
  public void setUp() throws Exception {
    final Activity activity = mActivityTestRule.getActivity();
    try {
      mActivityTestRule.runOnUiThread(
          new Runnable() {
            @Override
            public void run() {
              KeyguardManager mKG = (KeyguardManager) activity.getSystemService(KEYGUARD_SERVICE);
              KeyguardManager.KeyguardLock mLock = mKG.newKeyguardLock(KEYGUARD_SERVICE);
              mLock.disableKeyguard();

              // turn the screen on
              activity
                  .getWindow()
                  .addFlags(
                      WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                          | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                          | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                          | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                          | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            }
          });
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  @Test
  public void settingsActivityTest() {

    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    ViewInteraction linearLayout1 = onView(withId(R.id.ll_camera));
    linearLayout1.perform(scrollTo());
    linearLayout1.check(matches(isDisplayed()));

    ViewInteraction linearLayout3 = onView(withId(R.id.ll_n_columns));
    linearLayout3.perform(scrollTo());
    linearLayout3.check(matches(isDisplayed()));

    ViewInteraction linearLayout4 = onView(withId(R.id.ll_excluded_album));
    linearLayout4.perform(scrollTo());
    linearLayout4.check(matches(isDisplayed()));

    ViewInteraction linearLayout5 = onView(withId(R.id.ll_basic_theme));
    linearLayout5.perform(scrollTo());
    linearLayout5.check(matches(isDisplayed()));

    ViewInteraction linearLayout6 = onView(withId(R.id.ll_primaryColor));
    linearLayout6.perform(scrollTo());
    linearLayout6.check(matches(isDisplayed()));

    ViewInteraction linearLayout7 = onView(withId(R.id.ll_accentColor));
    linearLayout7.perform(scrollTo());
    linearLayout7.check(matches(isDisplayed()));

    ViewInteraction linearLayout8 = onView(withId(R.id.ll_custom_thirdAct));
    linearLayout8.perform(scrollTo());
    linearLayout8.check(matches(isDisplayed()));

    ViewInteraction linearLayout9 = onView(withId(R.id.ll_map_provider));
    linearLayout9.perform(scrollTo());
    linearLayout9.check(matches(isDisplayed()));

    ViewInteraction linearLayout10 = onView(withId(R.id.ll_switch_TraslucentStatusBar));
    linearLayout10.perform(scrollTo());
    linearLayout10.check(matches(isDisplayed()));

    ViewInteraction linearLayout11 = onView(withId(R.id.ll_switch_ColoredNavBar));
    linearLayout11.perform(scrollTo());
    linearLayout11.check(matches(isDisplayed()));

    ViewInteraction linearLayout12 = onView(withId(R.id.ll_switch_max_luminosity));
    linearLayout12.perform(scrollTo());
    linearLayout12.check(matches(isDisplayed()));
  }
}
