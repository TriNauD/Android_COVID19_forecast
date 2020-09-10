package com.bupt.covid19_forecast_frontend;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class AlgorithmTest {
    String[] mounth={"8","9","8","8","9","9","9","9","8","8","9","5"};
    String[] day={"20","1","19","20","11","15","10","10","30","10","3","23"};
    String[] raiseLastTime={"0","0","0","0","10","10","60","15","50","30","8","10"};
    int[] controlLevel={1,2,3};
    int i=0;

    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void algorithmTest(){

        onView(withId(R.id.forecast_switch)).perform(click());

        onView(withId(R.id.model_type_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());

        for (i=0;i<3;i++){
            //输入日期
            onView(withId(R.id.control_start_date_month_input))
                    .perform(typeText(mounth[i]), closeSoftKeyboard());
            onView(withId(R.id.control_start_date_day_input))
                    .perform(typeText(day[i]), closeSoftKeyboard());
            //提交
            onView(withId(R.id.submit_button)).perform(click());
            //修改控制等级
            onView(withId(R.id.control_level_spinner)).perform(click());
            onData(anything()).atPosition(controlLevel[i]).perform(click());
            //重置
            onView(withId(R.id.reset_button)).perform(click());
        }

        //群体免疫
        onView(withId(R.id.model_type_spinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());

        onView(withId(R.id.submit_button)).perform(click());

        onView(withId(R.id.model_type_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());

        //自定义控制天数
        for(i=4;i<12;i++){
            onView(withId(R.id.control_start_date_month_input))
                    .perform(typeText(mounth[i]), closeSoftKeyboard());
            onView(withId(R.id.control_start_date_day_input))
                    .perform(typeText(day[i]), closeSoftKeyboard());
            onView((withId(R.id.control_duration_input)))
                    .perform(typeText(raiseLastTime[i]),closeSoftKeyboard());

            onView(withId(R.id.submit_button)).perform(click());
            onView(withId(R.id.reset_button)).perform(click());

        }


    }
}


