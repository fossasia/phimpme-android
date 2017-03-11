package vn.mbm.phimp.me;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CameraActivityTest {

    @Rule
    public ActivityTestRule<PhimpMe> mActivityTestRule = new ActivityTestRule<>(PhimpMe.class);

    @Test
    public void cameraActivityTest() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.tab_camera), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.takephoto), isDisplayed()));
        appCompatImageButton2.check(matches(isDisplayed()));

    }
}

