package com.bupt.covid19_forecast_frontend;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

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
    public void testProvinceChange() {
        //测试切换省份
        onView(withId(R.id.change_province_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.change_province_spinner)).check(matches(withSpinnerText(containsString("北京"))));
        //切换国内/海外
        onView(withId(R.id.home_or_abroad_toggle_btn)).perform(click());
        onView(withId(R.id.home_or_abroad_toggle_btn)).perform(click());
    }

    @Test
    public void testNationChange() {
        //测试切换国内/海外
        onView(withId(R.id.home_or_abroad_toggle_btn)).perform(click());
        //测试切换国家
        onView(withId(R.id.change_nation_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.change_nation_spinner)).check(matches(withSpinnerText(containsString("中国"))));

        //onView(withId(R.id.change_nation_spinner)).perform(click());
        //onView(withText("圣文森特和格林纳丁斯")).perform(click());
    }

    @Test
    public void testButtonClick() {
        //开启预测
        onView(withId(R.id.forecast_switch)).perform(click());

        onView(withId(R.id.model_type_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.model_type_spinner)).check(matches(withSpinnerText(containsString("控制"))));

        //测试切换控制等级
        onView(withId(R.id.control_level_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.control_level_spinner)).check(matches(withSpinnerText(containsString("二级控制"))));

        // Type text and then press the button
        onView(withId(R.id.control_start_date_month_input))
                .perform(typeText("8"), closeSoftKeyboard());
        onView(withId(R.id.control_start_date_day_input))
                .perform(typeText("20"), closeSoftKeyboard());

        onView(withId(R.id.submit_button)).perform(click());

        //测试切换控制模型
        onView(withId(R.id.model_type_spinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.model_type_spinner)).check(matches(withSpinnerText(containsString("群体免疫"))));
        onView(withId(R.id.submit_button)).perform(click());

        //测试切换预测状态
        onView(withId(R.id.forecast_switch)).perform(click());
        onView(withId(R.id.forecast_switch)).perform(click());

        onView(withId(R.id.model_type_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.model_type_spinner)).check(matches(withSpinnerText(containsString("控制"))));

        //测试切换曲线类型
        onView(withId(R.id.line_type_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.line_type_spinner)).check(matches(withSpinnerText(containsString("累计确诊"))));

        onView(withId(R.id.line_type_spinner)).perform(click());
        onData(anything()).atPosition(2).perform(click());
        onView(withId(R.id.line_type_spinner)).check(matches(withSpinnerText(containsString("累计治愈"))));

        onView(withId(R.id.line_type_spinner)).perform(click());
        onData(anything()).atPosition(3).perform(click());
        onView(withId(R.id.line_type_spinner)).check(matches(withSpinnerText(containsString("累计死亡"))));

        onView(withId(R.id.line_type_spinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.line_type_spinner)).check(matches(withSpinnerText(containsString("现存确诊"))));

        //重置
        onView(withId(R.id.reset_button)).perform(click());

    }


    /**
     * @Test public void testScreenRotation(){
     * //onView(withId(R.id.home_or_abroad_toggle_btn)).perform(click());
     * onView(withId(R.id.forecast_switch)).perform(click());
     * //测试屏幕旋转
     * activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
     * activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
     * }
     */

    //用户输入参数测试
    @Test
    public void testUserParamInput() {
        //开启预测
        onView(withId(R.id.forecast_switch)).perform(click());

        // input 09 0
        onView(withId(R.id.control_start_date_day_input))
                .perform(typeText("09"), closeSoftKeyboard());
        onView(withId(R.id.control_start_date_month_input))
                .perform(typeText("0"), closeSoftKeyboard());
        onView(withId(R.id.submit_button)).perform(click());
    }

    @Test
    public void testSEIR() throws InterruptedException {
        onView(withId(R.id.forecast_switch)).perform(click());

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
        onView(withId(R.id.b1_input))
                .perform(typeText(b1), closeSoftKeyboard());
        onView(withId(R.id.r2_input))
                .perform(typeText(r2), closeSoftKeyboard());
        onView(withId(R.id.b2_input))
                .perform(typeText(b2), closeSoftKeyboard());
        onView(withId(R.id.a_input))
                .perform(typeText(a), closeSoftKeyboard());
        onView(withId(R.id.v_input))
                .perform(typeText(v), closeSoftKeyboard());
        onView(withId(R.id.d_input))
                .perform(typeText(d), closeSoftKeyboard());
        onView(withId(R.id.n_input))
                .perform(typeText(n), closeSoftKeyboard());

        onView(withId(R.id.submit_button)).perform(click());
        Thread.sleep(1000000000);

    }

}
