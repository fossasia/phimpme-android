package vn.mbm.phimp.me;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UploadActivityTest {

    @Rule
    public ActivityTestRule<PhimpMe> mActivityTestRule = new ActivityTestRule<>(PhimpMe.class);

    @Before
    public void resetTimeout() {
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(26, TimeUnit.SECONDS);
    }

    @Test
    public void waitFor5Seconds() {
        UploadActivityTest(DateUtils.SECOND_IN_MILLIS * 5);
    }

    public void UploadActivityTest(long waitingTime) {

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.tab_upload), isDisplayed()));
        bottomNavigationItemView.perform(click());

        IdlingPolicies.setMasterPolicyTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);

        IdlingResource idlingResource = new ElapsedTimeIdlingResource(waitingTime);
        Espresso.registerIdlingResources(idlingResource);

        /*ViewInteraction textView = onView(
                allOf(withId(R.id.textView1), withText("Accounts"),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout1),
                                        childAtPosition(
                                                withId(R.id.tabUpload),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Accounts")));*/

        /*ViewInteraction textView2 = onView(
                allOf(withId(R.id.textView2), withText("Photos"),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout2),
                                        childAtPosition(
                                                withId(R.id.tabUpload),
                                                2)),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("Photos")));*/

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.btnUploadAccountAdd),
                        withParent(allOf(withId(R.id.relativeLayout1),
                                withParent(withId(R.id.tabUpload)))),
                        isDisplayed()));
        appCompatImageView.check(matches(isDisplayed()));

        ViewInteraction btnUploadPhotoView = onView(
                allOf(withId(R.id.btnUploadPhoto),
                        withParent(withId(R.id.uploadButtonPanel)),
                        isDisplayed()));
        btnUploadPhotoView.check(matches(isDisplayed()));

        ViewInteraction btnSendDirectlyView = onView(
                allOf(withId(R.id.upload_sendDirectly),
                        withParent(withId(R.id.uploadButtonPanel)),
                            isDisplayed()));
        btnSendDirectlyView.check(matches(isDisplayed()));

        ViewInteraction btnUploadAddView = onView(
                allOf(withId(R.id.btnUploadPhotoAdd),
                        withParent(withId(R.id.uploadButtonPanel)),
                        isDisplayed()));
        btnUploadAddView.check(matches(isDisplayed()));

        Espresso.unregisterIdlingResources(idlingResource);

    }

    /*private static Matcher<View> childAtPosition(
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
