package vn.mbm.phimp.me;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<PhimpMe> mActivityTestRule = new ActivityTestRule<>(PhimpMe.class);

    @Before
    public void resetTimeout() {
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(26, TimeUnit.SECONDS);
    }

    @Test
    public void waitFor5Seconds() {
        MainActivityTest(DateUtils.SECOND_IN_MILLIS * 5);
    }

    public void MainActivityTest(long waitingTime) {

        IdlingPolicies.setMasterPolicyTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);

        IdlingResource idlingResource = new ElapsedTimeIdlingResource(waitingTime);
        Espresso.registerIdlingResources(idlingResource);

       /* ViewInteraction imageView = onView(
                allOf(withId(R.id.btnLoadMoreLocalPhotos),
                        childAtPosition(
                                allOf(withId(R.id.titlebarLocalPhotos),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.titleLocalPhotos), withText("Local Photos"),
                        childAtPosition(
                                allOf(withId(R.id.titlebarLocalPhotos),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Local Photos")));*/


        ViewInteraction cameraIcon = onView(
                allOf(withId(R.id.tab_camera), isDisplayed()));
        cameraIcon.check(matches(isDisplayed()));

        ViewInteraction galleryIcon = onView(
                allOf(withId(R.id.tab_gallery), isDisplayed()));
        galleryIcon.check(matches(isDisplayed()));

        ViewInteraction settingsIcon = onView(
                allOf(withId(R.id.tab_settings), isDisplayed()));
        settingsIcon.check(matches(isDisplayed()));

        ViewInteraction uploadIcon = onView(
                allOf(withId(R.id.tab_upload), isDisplayed()));
        uploadIcon.check(matches(isDisplayed()));

        Espresso.unregisterIdlingResources(idlingResource);

    }

   /* private static Matcher<View> childAtPosition(
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
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }*/
}

