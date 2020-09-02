package org.fossasia.phimpme.leafpic.activities;

import static android.content.Context.KEYGUARD_SERVICE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import android.app.Activity;
import android.app.KeyguardManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.gallery.activities.LFMainActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@LargeTest
@RunWith(AndroidJUnit4.class)
public class HomeScreenTest {

  @Rule
  public ActivityTestRule<LFMainActivity> mActivityTestRule =
      new ActivityTestRule<>(LFMainActivity.class);

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
  public void homeScreenTest() {
    // Added a sleep statement to match the app's execution delay.
    // The recommended way to handle such scenarios is to use Espresso idling resources:
    // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    ViewInteraction frameLayout =
        onView(
            allOf(
                withId(R.id.navigation_accounts),
                withContentDescription("Accounts"),
                childAtPosition(childAtPosition(withId(R.id.bottombar), 0), 2),
                isDisplayed()));
    frameLayout.check(matches(isDisplayed()));

    ViewInteraction frameLayout3 =
        onView(
            allOf(
                withId(R.id.navigation_camera),
                withContentDescription("Camera"),
                childAtPosition(childAtPosition(withId(R.id.bottombar), 0), 0),
                isDisplayed()));
    frameLayout3.check(matches(isDisplayed()));

    ViewInteraction frameLayout4 =
        onView(
            allOf(
                withId(R.id.navigation_home),
                childAtPosition(childAtPosition(withId(R.id.bottombar), 0), 1),
                isDisplayed()));
    frameLayout4.check(matches(isDisplayed()));

    // Added a sleep statement to match the app's execution delay.
    // The recommended way to handle such scenarios is to use Espresso idling resources:
    // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup
            && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }
}
