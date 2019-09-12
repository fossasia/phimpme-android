package org.fossasia.phimpme.leafpic.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.opencamera.Camera.PhotoActivity;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@LargeTest
@RunWith(AndroidJUnit4.class)
public class PhotoActivityTest {

  @Rule
  public ActivityTestRule<PhotoActivity> mActivityTestRule =
      new ActivityTestRule<>(PhotoActivity.class);

  @Test
  public void photoActivityTest() {

    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    ViewInteraction actionMenuItemView = onView(allOf(withId(R.id.menu_share), isDisplayed()));
    actionMenuItemView.check(matches(isDisplayed()));

    ViewInteraction buttonDelete = onView(allOf(withId(R.id.save), isDisplayed()));
    buttonDelete.check(matches(isDisplayed()));

    ViewInteraction buttonSave = onView(allOf(withId(R.id.delete), isDisplayed()));
    buttonSave.check(matches(isDisplayed()));

    ViewInteraction buttonEdit = onView(allOf(withId(R.id.edit), isDisplayed()));
    buttonEdit.check(matches(isDisplayed()));
  }
}
