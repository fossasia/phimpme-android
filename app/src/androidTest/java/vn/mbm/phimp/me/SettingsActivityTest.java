package vn.mbm.phimp.me;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {

    @Rule
    public ActivityTestRule<PhimpMe> mActivityTestRule = new ActivityTestRule<>(PhimpMe.class);

    @Test
    public void settingsActivityTest() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.tab_settings), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.textView1), withText("Accounts"),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout1),
                                        childAtPosition(
                                                withId(R.id.linearLayout1),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Accounts")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textView2), withText("Photos Settings"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withId(R.id.scrollView1),
                                                0)),
                                2),
                        isDisplayed()));
        textView2.check(matches(withText("Photos Settings")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.TextView02), withText("Local Photos"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withId(R.id.scrollView1),
                                                0)),
                                4),
                        isDisplayed()));
        textView3.check(matches(withText("Local Photos")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.imgbtnSettingsMaxFilesize),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.deletebtn),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        2),
                                1),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction checkBox = onView(
                allOf(withId(R.id.checkbox_gallery),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        checkBox.check(matches(isDisplayed()));

        ViewInteraction checkBox2 = onView(
                allOf(withId(R.id.checkbox_gallery),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        checkBox2.check(matches(isDisplayed()));

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
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}

