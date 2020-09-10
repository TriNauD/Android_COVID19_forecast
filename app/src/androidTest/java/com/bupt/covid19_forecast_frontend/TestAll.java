package com.bupt.covid19_forecast_frontend;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.repeatedlyUntil;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class TestAll {
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testAll() throws InterruptedException {
        //测试切换省份
        onView(withId(R.id.change_province_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.change_province_spinner)).check(matches(withSpinnerText(containsString("北京"))));
        Thread.sleep(3000);
        //切换国内/海外
        onView(withId(R.id.home_or_abroad_toggle_btn)).perform(click());
        Thread.sleep(3000);
        //测试切换曲线类型
        for(int i=1;i<4;i++){
            onView(withId(R.id.line_type_spinner)).perform(click());
            onData(anything()).atPosition(i).perform(click());
            Thread.sleep(1000);
        }

        onView(withId(R.id.line_type_spinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.line_type_spinner)).check(matches(withSpinnerText(containsString("现存确诊"))));
        //测试切换预测状态
        onView(withId(R.id.forecast_switch)).perform(click());
        Thread.sleep(1000);
        //测试群体免疫
        onView(withId(R.id.submit_button)).perform(click());
        Thread.sleep(3000);
        //切换国家
        onView(withId(R.id.change_nation_spinner)).perform(click());
        onData(anything()).atPosition(17).perform(click());
        onView(withId(R.id.change_nation_spinner)).check(matches(withSpinnerText(containsString("土耳其"))));
        //测试控制
        onView(withId(R.id.model_type_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.control_start_date_month_input))
                .perform(typeText("9"), closeSoftKeyboard());
        onView(withId(R.id.control_start_date_day_input))
                .perform(typeText("10"), closeSoftKeyboard());

        for(int i=1;i<4;i++){
            onView(withId(R.id.submit_button)).perform(click());
            Thread.sleep(1000);

            onView(withId(R.id.control_level_spinner)).perform(click());
            onData(anything()).atPosition(i).perform(click());
        }

        onView(withId(R.id.control_duration_input))
                .perform(typeText("60"), closeSoftKeyboard());
        onView(withId(R.id.submit_button)).perform(click());

        onView(withId(R.id.till_today_button)).perform(click());
        onView(withId(R.id.submit_button)).perform(click());

        onView(withId(R.id.model_type_spinner)).perform(click());
        onData(anything()).atPosition(2).perform(click());
        onView(withId(R.id.model_type_spinner)).check(matches(withSpinnerText(containsString("SEIR"))));

        String r1 = "10";//感染者接触人数
        String b1 = "0.01";//感染者传染概率
        String r2 = "65";//潜伏者接触人数
        String b2 = "0.01";//潜伏者传染概率
        String a = "0.04";//潜伏者患病概率
        String v = "0.15";//感染者康复概率
        String d = "0.25";//病死率
        String n = "1000000";//总人数

        onView(withId(R.id.r1_input))
                .perform(typeText(r1), closeSoftKeyboard());
        onView(withId(R.id.r2_input))
                .perform(typeText(r2), closeSoftKeyboard());
        onView(withId(R.id.n_input))
                .perform(typeText(n), closeSoftKeyboard());
        onView(withId(R.id.b1_input))
                .perform(typeText(b1), closeSoftKeyboard());
        onView(withId(R.id.b2_input))
                .perform(typeText(b2), closeSoftKeyboard());
        onView(withId(R.id.a_input))
                .perform(typeText(a), closeSoftKeyboard());
        onView(withId(R.id.v_input))
                .perform(typeText(v), closeSoftKeyboard());
        onView(withId(R.id.d_input))
                .perform(typeText(d), closeSoftKeyboard());

        onView(withId(R.id.submit_button)).perform(click());
        onView(withId(R.id.reset_button)).perform(click());

        onView(withId(R.id.r1_input)).perform(repeatedlyUntil(swipeDown(),
                hasDescendant(withId(R.id.chart)),
                50));

        Thread.sleep(1000000000);
    }
}
