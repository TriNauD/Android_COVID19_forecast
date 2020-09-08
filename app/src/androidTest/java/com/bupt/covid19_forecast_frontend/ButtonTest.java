package com.bupt.covid19_forecast_frontend;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.onData;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static java.util.EnumSet.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class ButtonTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testProvinceChange(){
        //测试切换省份
        onView(withId(R.id.change_province_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.change_province_spinner)).check(matches(withSpinnerText(containsString("北京"))));
        //切换国内/海外
        onView(withId(R.id.home_or_abroad_toggle_btn)).perform(click());
        onView(withId(R.id.home_or_abroad_toggle_btn)).perform(click());
    }

    @Test
    public void testNationChange(){
        //测试切换国内/海外
        onView(withId(R.id.home_or_abroad_toggle_btn)).perform(click());
        //测试切换国家
        onView(withId(R.id.change_nation_spinner)).perform(click());
        onData(anything()).atPosition(2).perform(click());
        onView(withId(R.id.change_nation_spinner)).check(matches(withSpinnerText(containsString("英国"))));
    }

    @Test
    public void testButtonClick(){
        //测试切换曲线类型
        onView(withId(R.id.line_type_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.line_type_spinner)).check(matches(withSpinnerText(containsString("累计确诊"))));

        onView(withId(R.id.line_type_spinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.line_type_spinner)).check(matches(withSpinnerText(containsString("现存确诊"))));

        //测试切换控制等级
        onView(withId(R.id.control_level_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.control_level_spinner)).check(matches(withSpinnerText(containsString("二级控制"))));

        MainActivity activity = activityRule.getActivity();

        // Type text and then press the button.
        onView(withId(R.id.control_start_date_day_input))
                .perform(typeText("20"), closeSoftKeyboard());
        onView(withId(R.id.control_start_date_month_input))
                .perform(typeText("8"), closeSoftKeyboard());
        onView(withId(R.id.submit_button)).perform(click());
        // Check the toast if its text is failure.
        onView(withText(R.string.alert_msg_get_data_failure)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));

        //测试切换控制模型
        onView(withId(R.id.model_type_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.model_type_spinner)).check(matches(withSpinnerText(containsString("群体免疫"))));

        //测试切换预测状态
        onView(withId(R.id.forecast_switch)).perform(click());
        onView(withId(R.id.forecast_switch)).perform(click());

        onView(withId(R.id.model_type_spinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.model_type_spinner)).check(matches(withSpinnerText(containsString("控制"))));

    }
}
