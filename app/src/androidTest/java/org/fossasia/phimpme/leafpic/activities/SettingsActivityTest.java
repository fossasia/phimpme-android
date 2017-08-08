package org.fossasia.phimpme.leafpic.activities;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.fossasia.phimpme.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {

    @Rule
    public ActivityTestRule<SettingsActivity> mActivityTestRule = new ActivityTestRule<>(SettingsActivity.class);

    @Test
    public void settingsActivityTest() {

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction linearLayout1 = onView(
                withId(R.id.ll_camera));
        linearLayout1.perform(scrollTo());
        linearLayout1.check(matches(isDisplayed()));

        ViewInteraction linearLayout2 = onView(
                withId(R.id.ll_security));
        linearLayout2.perform(scrollTo());
        linearLayout2.check(matches(isDisplayed()));

        ViewInteraction linearLayout3 = onView(
                withId(R.id.ll_n_columns));
        linearLayout3.perform(scrollTo());
        linearLayout3.check(matches(isDisplayed()));

        ViewInteraction linearLayout4 = onView(
                withId(R.id.ll_excluded_album));
        linearLayout4.perform(scrollTo());
        linearLayout4.check(matches(isDisplayed()));

        ViewInteraction linearLayout5 = onView(
                withId(R.id.ll_basic_theme));
        linearLayout5.perform(scrollTo());
        linearLayout5.check(matches(isDisplayed()));

        ViewInteraction linearLayout6 = onView(
                withId(R.id.ll_primaryColor));
        linearLayout6.perform(scrollTo());
        linearLayout6.check(matches(isDisplayed()));

        ViewInteraction linearLayout7 = onView(
                withId(R.id.ll_accentColor));
        linearLayout7.perform(scrollTo());
        linearLayout7.check(matches(isDisplayed()));

        ViewInteraction linearLayout8 = onView(
                withId(R.id.ll_custom_thirdAct));
        linearLayout8.perform(scrollTo());
        linearLayout8.check(matches(isDisplayed()));

        ViewInteraction linearLayout9 = onView(
                withId(R.id.ll_map_provider));
        linearLayout9.perform(scrollTo());
        linearLayout9.check(matches(isDisplayed()));

        ViewInteraction linearLayout10 = onView(
                withId(R.id.ll_switch_TraslucentStatusBar));
        linearLayout10.perform(scrollTo());
        linearLayout10.check(matches(isDisplayed()));

        ViewInteraction linearLayout11 = onView(
                withId(R.id.ll_switch_ColoredNavBar));
        linearLayout11.perform(scrollTo());
        linearLayout11.check(matches(isDisplayed()));

        ViewInteraction linearLayout12 = onView(
                withId(R.id.ll_switch_max_luminosity));
        linearLayout12.perform(scrollTo());
        linearLayout12.check(matches(isDisplayed()));

    }

}
