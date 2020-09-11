package com.bupt.covid19_forecast_frontend;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    //Simulate user's inputting and pressing submit button, then check if the toast msg is showed as intended.
    @Test
    public void checkToastShowAfterSubmitInput() {

        MainActivity activity = activityRule.getActivity();

        // Type text and then press the button.
        onView(withId(R.id.control_start_date_day_input))
                .perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.control_start_date_month_input))
                .perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.submit_button)).perform(click());
        // Check the toast if its text is failure.
        onView(withText(R.string.alert_msg_get_data_failure)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));

    }

}
