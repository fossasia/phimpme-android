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

        ViewInteraction uploadBtnPanel = onView(
                allOf(withId(R.id.uploadButtonPanel),
                        withParent(withId(R.id.relativeLayout2)),
                        isDisplayed()));
        uploadBtnPanel.check(matches(isDisplayed()));

        ViewInteraction noPhotosLabel = onView(
                allOf(withId(R.id.nophotos),
                        isDisplayed()));
        noPhotosLabel.check(matches(isDisplayed()));

        Espresso.unregisterIdlingResources(idlingResource);

    }
}
