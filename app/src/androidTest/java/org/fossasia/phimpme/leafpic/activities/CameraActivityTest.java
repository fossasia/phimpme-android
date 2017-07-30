package org.fossasia.phimpme.leafpic.activities;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.text.format.DateUtils;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.opencamera.Camera.CameraActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CameraActivityTest {

    @Rule
    public ActivityTestRule<CameraActivity> mActivityTestRule = new ActivityTestRule<>(CameraActivity.class);

    @Before
    public void resetTimeout() {
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(26, TimeUnit.SECONDS);
    }

    @Test
    public void waitFor5Seconds() {
        cameraActivityTest(DateUtils.SECOND_IN_MILLIS * 5);
    }


    public void cameraActivityTest(long waitingTime) {

        IdlingPolicies.setMasterPolicyTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);

        IdlingResource idlingResource = new ElapsedTimeIdlingResource(waitingTime);
        Espresso.registerIdlingResources(idlingResource);

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_camera), withContentDescription("Camera"), isDisplayed()));
        bottomNavigationItemView.perform(click());


        onView(
                allOf(withId(R.id.toggle_button), isDisplayed()));

        onView(
                allOf(withId(R.id.popup), withContentDescription("Popup settings"), isDisplayed()));

        onView(
                allOf(withId(R.id.exposure), withContentDescription("Exposure"), isDisplayed()));


        ViewInteraction appCompatImageButton5 = onView(
                allOf(withId(R.id.take_photo), withContentDescription("Take Photo"), isDisplayed()));
        appCompatImageButton5.perform(click());

        Espresso.unregisterIdlingResources(idlingResource);

    }

}
