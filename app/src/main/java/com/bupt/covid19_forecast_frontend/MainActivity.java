package com.bupt.covid19_forecast_frontend;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.tu.loadingdialog.LoadingDailog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import viewModel.WebConnect;
import viewModel.LineViewModel;

import static android.icu.text.DateTimePatternGenerator.DAY;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    //日志TAG，调试用，默认使用类名
    private static final String TAG = "MainActivity";

    //ui控件
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner controlLevelSpinner;
    private Spinner changeNationSpinner;
    private Spinner changeProvinceSpinner;
    private Spinner lineTypeSpinner;
    private Spinner modelTypeSpinner;
    private Switch forecastSwitch;
    private Button submitButton;
    private Button resetButton;
    private Button tillTodyButton;
    private ToggleButton homeOrAbroadToggleButton;
    private EditText controlDurationInput;
    private EditText controlStartDateMonthInput;
    private EditText controlStartDateDayInput;
    private TextView toolbarTitle;
    private TextView controlLevelLabel;
    private TextView controlStartDateLabel;
    private TextView controlDurationLabel;
    private TextView dayLabel;
    private TextView peopleNumBarCol1;
    private TextView peopleNumBarCol2;
    private TextView peopleNumBarCol3;
    private TextView peopleNumBarCol4;
    private RelativeLayout paramLine1;
    private RelativeLayout paramLine2;
    private LinearLayout userParamLines;
    private RelativeLayout userParamLine1;
    private RelativeLayout buttonLine;
    //提示消息
    Toast toast;

    //获取数据线程
    private GetDataTask getDataTask;
    //获取数据的遮罩的样式设置
    LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
            .setMessage("加载中...")
            .setCancelable(false)
            .setCancelOutside(false);
    //遮罩
    LoadingDailog dialog;

    //当前国家
    private String currentRegionName;

    //折线视图
    private LineChartView myLineChartView;

    //折线的数据类
    private LineViewModel lineViewModel;

    //预测开关状态（默认开启）
    private boolean isForecastSwitchedOn = false;

    //国家下拉框是不是第一次调用
    private boolean isFirstChooseNation = true;

    //当前显示的线是几号
    private int curLineIndex = 0;
    //最大y轴
    private int MaxY = 2200000;

    //点击坐标
    int clickX, clickY;
    //点击的日期标签
    String clickDateString = "";
    //点击的线下标
    private int clickColorLineIndex = 0;
    //在画的线有几条是需要标签颜色的
    private int showColorLineNum = 0;

    /*————————————获取数据相关————————————*/

    /**
     * 获取数据线程类
     * 异步运行获取数据 获取结束后反映在ui组件上（加载中图标消失）
     *
     * @author xjy, lym
     */
    private class GetDataTask extends AsyncTask<String, Integer, String> {
        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "Loading... 当前地区：" + currentRegionName);

            //先设置为没有开始获取&没有获取成功
            WebConnect.setIsGetFinished(false);
            WebConnect.setIsGetSuccess(false);
            Log.i(TAG, "Loading...开始转圈圈 isGetFinished：" + WebConnect.isGetFinished());

            //创建一个遮罩
            dialog = loadBuilder.create();
            dialog.show();
        }

        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        @Override
        protected String doInBackground(String... params) {
            try {
                //先重置数据
                //然后去获取数据
                // 如果成功会将isGetFinished设置为true
                switch (params[0]) {
                    case "World":
                        WebConnect.resetRealData();
                        WebConnect.getWorld(currentRegionName);
                        break;
                    case "Predict":
                        WebConnect.resetPredictData();
                        WebConnect.getPredict(currentRegionName);
                        break;
                    case "Province":
                        WebConnect.resetRealData();
                        WebConnect.getProvince(currentRegionName);
                        break;
                }

                //如果没有得到数据，就一直等待，并提示
                while (!WebConnect.isGetFinished()) {
                    Thread.sleep(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

//        // 方法3：onProgressUpdate（）
//        // 作用：在主线程 显示线程任务执行的进度
//        @Override
//        protected void onProgressUpdate(Integer... progresses) {
//            progressBar.setProgress(progresses[0]);
//        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件
        @Override
        protected void onPostExecute(String result) {
            //成功之后，最后一次再刷新一下图表
            drawChart();
            //遮罩消失
            dialog.hide();
            //根据isGetSuccess结果是否成功 选择提示数据获取失败/成功
            toast.setText(WebConnect.isGetSuccess() ? (R.string.alert_msg_get_data_success) : (R.string.alert_msg_get_data_failure));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
            Log.i(TAG, "Loading " + currentRegionName + " 结束");
        }


//        // 方法5：onCancelled()
//        // 作用：将异步任务设置为：取消状态
//        @Override
//        protected void onCancelled() {
//            progressBar.setProgress(0);
//            progressBar.setVisibility(View.INVISIBLE);
//
//        }

    }

    /*————————————画图相关————————————*/

    /**
     * 生成线并且调整线条格式
     *
     * @author lym
     */
    void getLines() {
        //重新生成线
        lineViewModel.initRealChart();
        lineViewModel.initForecastChart();
    }

    /**
     * 刷新图像。
     * 刷新图像，包括绑定视图、坐标轴、显示位置、显示区域范围
     *
     * @author lym
     */
    private void drawChart() {
        Log.i(TAG, "draw 进入函数");
        Log.i(TAG, "draw 函数：curLineIndex：" + curLineIndex);


        //生成线并且调整线条格式
        getLines();

        //最大y轴，赋值为累计确诊数量
        MaxY = WebConnect.getMaxY();

        //画画和调整视图
        draw();

        //更新上方4个数
        updateFourNum();

    }

    /**
     * 更新上方4个数
     *
     * @author lym
     */
    private void updateFourNum() {
        Integer[] integers = WebConnect.getOneDayFourNum();
        String[] strings = new String[4];

        //int -> String
        for (int i = 0; i < 4; i++) {
            strings[i] = String.valueOf(integers[i]);
            Log.i(TAG, "updateFourNum第" + i + "个字符串：" + strings[i]);
        }

        peopleNumBarCol1.setText(strings[0]);
        peopleNumBarCol2.setText(strings[1]);
        peopleNumBarCol3.setText(strings[2]);
        peopleNumBarCol4.setText(strings[3]);

    }



    /**
     * 小画家
     *
     * @author lym
     */
    private void draw() {
        //更新预测状态，这个值是表示显示的线是不是真的预测线
        int numOfRealLines = lineViewModel.getNumOfRealLines();
        boolean isForecast = (curLineIndex >= numOfRealLines);//如果索引大于“真实线”数目，就表示是在预测

        //------------------------- 线 ---------------------
        //获取到所有的线
        List<Line> allLines = lineViewModel.getLines();

        //当前要画的线
        List<Line> showLines = new ArrayList<>();
        //按照下标来选择一条
        showLines.add(allLines.get(curLineIndex));
        //前半截后半截
        //如果在预测，加上对应的真实线
        if (isForecast) {
            showLines.add(allLines.get(0));
        }
        //在画的“线”有几条
        showColorLineNum = showLines.size();

        //手指点击的竖直线
        //点
        PointValue pointValue1 = new PointValue();
        pointValue1.set(clickX, clickY);
        PointValue pointValue2 = new PointValue();
        pointValue2.set(clickX, 0);
        PointValue pointValue3 = new PointValue();
        pointValue3.set(clickX, MaxY);
        List<PointValue> pointValueList = new ArrayList<>();
        pointValueList.add(pointValue1);
        pointValueList.add(pointValue2);
        pointValueList.add(pointValue3);
        //线
        Line handLine = new Line();
        handLine.setValues(pointValueList);
        //样式
        handLine.setHasPoints(false);//不要点

        //用来显示标签的线
        //点
        PointValue pointValue4 = new PointValue();
        pointValue4.set(clickX, clickY);//只取点到的点
        pointValue4.setLabel(clickDateString);
        List<PointValue> pointValueList1 = new ArrayList<>();
        pointValueList1.add(pointValue4);
        //线
        Line labelLine = new Line();
        labelLine.setValues(pointValueList1);
        //样式
        labelLine.setHasLabels(true);//常驻标签
        labelLine.setPointRadius(3);//点的大小

        //颜色
        handLine.setColor(Color.WHITE);
        //点击的线的颜色
        Line colorLine = showLines.get(clickColorLineIndex);
        labelLine.setColor(colorLine.getColor());

        //加入总的线列表
        showLines.add(handLine);
        showLines.add(labelLine);

        //------------------------- 轴 -----------------------
        //获取所有轴
        List<Axis[]> allAxes = lineViewModel.getAxesList();
        //选择要显示的轴
        Axis[] showAxisXY = allAxes.get(curLineIndex);

        //-------------------- 图 ---------------------------------
        //线组放到线图数据里面
        LineChartData curLineData = new LineChartData(showLines);

        //坐标轴设置到图上
        //设置X轴在下面
        curLineData.setAxisXBottom(showAxisXY[0]);
        //设置Y轴在左边
        curLineData.setAxisYLeft(showAxisXY[1]);

        //把这个设置好的数据放到view里面
        myLineChartView.setLineChartData(curLineData);

        //----------------------- 互动与视图 ------------------------------
        Log.i(TAG, "调参师");
        myLineChartView.setInteractive(true);
        myLineChartView.setZoomEnabled(true);
        myLineChartView.setContainerScrollEnabled(true, ContainerScrollType.VERTICAL);
    }

    /*————————————控件相关————————————*/

    /**
     * 绑定组件。
     * 绑定xml的组件
     *
     * @author xjy
     */
    public void bindingElements() {
        //滑动刷新视图
        swipeRefreshLayout = findViewById(R.id.swipe);
        //chart
        myLineChartView = findViewById(R.id.chart);
        //spinner 页面的4个spinner并绑定listener
        lineTypeSpinner = findViewById(R.id.line_type_spinner);
        modelTypeSpinner = findViewById(R.id.model_type_spinner);
        controlLevelSpinner = findViewById(R.id.control_level_spinner);
        changeNationSpinner = findViewById(R.id.change_nation_spinner);
        changeProvinceSpinner = findViewById(R.id.change_province_spinner);

        //2行参数 + 用户参数行 + 按钮行
        paramLine1 = findViewById(R.id.param_line_1);
        paramLine2 = findViewById(R.id.param_line_2);
        userParamLines = findViewById(R.id.user_param_lines);
        userParamLine1 = findViewById(R.id.user_param_line_1);
        buttonLine = findViewById(R.id.buttonLine);

        //switch
        forecastSwitch = findViewById(R.id.forecast_switch);

        //button
        submitButton = findViewById(R.id.submit_button);
        resetButton = findViewById(R.id.reset_button);
        tillTodyButton = findViewById(R.id.till_today_button);

        //toggle button
        homeOrAbroadToggleButton = findViewById(R.id.home_or_abroad_toggle_btn);

        //edit text
        controlDurationInput = findViewById(R.id.control_duration_input);
        controlStartDateMonthInput = findViewById(R.id.control_start_date_month_input);
        controlStartDateDayInput = findViewById(R.id.control_start_date_day_input);
        toolbarTitle = findViewById(R.id.toolbar_title);

        //people num 4个col对应4个数字 需要改数就setText
        peopleNumBarCol1 = findViewById(R.id.people_num_bar_col_1_num);
        peopleNumBarCol2 = findViewById(R.id.people_num_bar_col_2_num);
        peopleNumBarCol3 = findViewById(R.id.people_num_bar_col_3_num);
        peopleNumBarCol4 = findViewById(R.id.people_num_bar_col_4_num);

        peopleNumBarCol1.setText("现存确诊");
        peopleNumBarCol2.setText("累计确诊");
        peopleNumBarCol3.setText("累计治愈");
        peopleNumBarCol4.setText("累计死亡");

        //static element
        controlLevelLabel = findViewById(R.id.control_level_label);
        controlStartDateLabel = findViewById(R.id.control_start_date_label);
        controlDurationLabel = findViewById(R.id.control_duration_label);
        dayLabel = findViewById(R.id.day_label);

        //提示消息
        toast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
    }

    /**
     * 设置多个监听器
     *
     * @author xjy
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setListener() {
        //下拉布局listener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Swipe: is refreshed");

                //刷新数据
                getDataTask = new GetDataTask();
                if (WebConnect.getIsProvince()) {
                    getDataTask.execute("Province");
                } else {
                    getDataTask.execute("World");
                }

                //重新画图
                drawChart();

                Log.i(TAG, "Swipe: fresh finished");
                //画完图后把刷新状态设为false
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //switch设置listener
        forecastSwitch.setOnCheckedChangeListener(this);
        //spinner设置listener
        lineTypeSpinner.setOnItemSelectedListener(this);
        modelTypeSpinner.setOnItemSelectedListener(this);
        controlLevelSpinner.setOnItemSelectedListener(this);
        changeNationSpinner.setOnItemSelectedListener(this);
        changeProvinceSpinner.setOnItemSelectedListener(this);
        //button设置listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Button: submit button clicked");
                //设置控制或群体免疫
                WebConnect.setHasControl(modelTypeSpinner.getSelectedItemPosition() == 0);
                Log.i(TAG, "Button: hasControl:" + (modelTypeSpinner.getSelectedItemPosition() == 0));
                //设置控制等级
                WebConnect.setControlGrade((controlLevelSpinner.getSelectedItemPosition() + 1) % 4);
                Log.i(TAG, "Button: ControlLevel:" + ((controlLevelSpinner.getSelectedItemPosition() + 1) % 4));
                //设置控制开始日期&持续时间
                //控制
                if (modelTypeSpinner.getSelectedItemPosition() == 0) {
                    //用户输入合法（不为空且日期合法）
                    if (isUserInputParamValid()) {
                        //格式化日期输入
                        String date = formatDateInput();
                        //WebConnect设置开始时间
                        WebConnect.setStartControlDate(date);
                        Log.i(TAG, "Button: ControlStartDate:" + date);
                        //WebConnect设置控制持续时间
                        WebConnect.setControlDuration(Integer.valueOf(controlDurationInput.getText().toString()));
                        Log.i(TAG, "Button: ControlDuration:" + (Integer.valueOf(controlDurationInput.getText().toString())));
                        //发送 获取预测
                        getDataTask = new GetDataTask();
                        getDataTask.execute("Predict");
                    }
                    //用户输入非法 显示提示
                    else {
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                //群体免疫
                else {
                    WebConnect.setStartControlDate("yyyy-mm-dd");
                    //发送 获取预测
                    getDataTask = new GetDataTask();
                    getDataTask.execute("Predict");
                }
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFocusableInputBoxes(controlDurationInput);
                clearFocusableInputBoxes(controlStartDateMonthInput);
                clearFocusableInputBoxes(controlStartDateDayInput);
                Log.i(TAG, "Button: reset button clicked");
            }
        });
        tillTodyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDateInputValid()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        //控制开始时间转换为date
                        Date controlStartDate = simpleDateFormat.parse(formatDateInput());
                        //当前日期
                        Date dateNow = new Date();
                        //得到差距多少天
                        int distance = getTimeDistance(controlStartDate, dateNow);
                        //判断是否
                        if (distance > 0) {
                            //如果持续时间输入框可更改则设为该差距 否则不变
                            controlDurationInput.setText(controlDurationInput.isFocusable() ? String.valueOf(distance) : controlDurationInput.getText());
                            Log.i(TAG, "tillTodayButton: 距离" + distance);
                        } else {
                            //超出今天范围
                            clearFocusableInputBoxes(controlStartDateMonthInput);
                            clearFocusableInputBoxes(controlStartDateDayInput);
                            clearFocusableInputBoxes(controlDurationInput);
                            toast.setText(R.string.alert_msg_input_err_after_today);
                            toast.show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast.show();
                }
            }
        });
        //toggle button设置listener
        homeOrAbroadToggleButton.setOnCheckedChangeListener(this);
        //input设置listener
        controlDurationInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(myLineChartView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        });
        controlStartDateMonthInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(myLineChartView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        });
        controlStartDateDayInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(myLineChartView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        });
        //lineChart设置listener
        myLineChartView.setOnTouchListener(new View.OnTouchListener() {
            /**
             * Called when a touch event is dispatched to a view. This allows listeners to
             * get a chance to respond before the target view.
             *
             * @param v     The view the touch event has been dispatched to.
             * @param event The MotionEvent object containing full information about
             *              the event.
             * @return True if the listener has consumed the event, false otherwise.
             */
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LineChartOnValueSelectListener lineChartOnValueSelectListener = new LineChartOnValueSelectListener() {
                        @Override
                        public void onValueSelected(int lineIndex, int pointIndex, PointValue pointValue) {
                            //获取点击的线
                            Log.i(TAG, "touch线为 lineIndex: " + lineIndex + " , pointIndex: " + pointIndex);
                            //如果是需要颜色的线，就赋值
                            if (lineIndex <= showColorLineNum) {
                                clickColorLineIndex = lineIndex;
                            }

                            //获取点击坐标
                            int x = (int) pointValue.getX();
                            int y = (int) pointValue.getY();
                            Log.i(TAG, "touch线上坐标为" + x + "," + y);
                            clickX = x;
                            clickY = y;

                            //获取点击的x轴的日期标签
                            Axis xAxix = lineViewModel.getAxesList().get(curLineIndex)[0];
                            char[] labelChars = xAxix.getValues().get(clickX).getLabel();
                            clickDateString = String.valueOf(labelChars);
                            clickDateString += " ";
                            clickDateString += clickY;
                            Log.i(TAG, "touch x轴坐标标签: " + clickDateString);

                            //动画移动 跟随点击动画到指定位置
                            myLineChartView.moveToWithAnimation(clickX, clickY);

                            //画画
                            draw();
                        }

                        @Override
                        public void onValueDeselected() {
                        }
                    };
                    myLineChartView.setOnValueTouchListener(lineChartOnValueSelectListener);
                    Log.i(TAG, "touch全局坐标为" + event.getAxisValue(0, 0) + "," + event.getAxisValue(1, 0));
                }
                return false;
            }
        });
    }

    /**
     * 清空可编辑状态的输入框组件内容
     *
     * @author xjy
     */
    public void clearFocusableInputBoxes(EditText editText) {
        editText.setText(editText.isFocusable() ? "" : editText.getText());
    }

    /**
     * 判断用户输入参数是否正确
     * 先判断是否为空 再判断是否合法
     *
     * @author xjy
     */
    public boolean isUserInputParamValid() {
        return (isDateInputValid() && isDurationInputValid());
    }

    /**
     * 判断输入持续时间是否正确
     *
     * @author xjy
     */
    public boolean isDurationInputValid() {
        boolean isDurationInputValid = true;
        String durationStr = controlDurationInput.getText().toString().trim();
        //判断是不是空 如果空设置提示消息为空
        if (durationStr.equals("")) {
            isDurationInputValid = false;
            toast.setText(R.string.alert_msg_input_err_duration_empty);
        }
        return isDurationInputValid;
    }

    /**
     * 判断输入日期是否正确
     *
     * @author xjy
     */
    public boolean isDateInputValid() {
        boolean isDateInputValid = true;
        String monthStr = controlStartDateMonthInput.getText().toString().trim();
        String dayStr = controlStartDateDayInput.getText().toString().trim();
        //首先判断是不是空 如果空设置提示消息为空
        if (monthStr.equals("") || dayStr.equals("")) {
            isDateInputValid = false;
            toast.setText(R.string.alert_msg_input_err_date_empty);
        }
        //再判断日期是否合法
        else {
            int monthInt = Integer.parseInt(monthStr);
            int dayInt = Integer.parseInt(dayStr);
            isDateInputValid = isDateValid(monthInt, dayInt);
        }
        return isDateInputValid;
    }

    /**
     * 判断日期是否正确
     *
     * @param month
     * @param day
     * @author xjy
     */
    public boolean isDateValid(int month, int day) {
        boolean isDateValid = true;
        //月份天数对照
        int monthDays[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        //月份<=0或者月份>12 日<=或者>31 粗略排除明显离谱日期
        if ((month <= 0 || month > 12) || ((day <= 0) || (day > 31))) {
            toast.setText(R.string.alert_msg_input_err_invalid);
            //清除日期输入框
            clearFocusableInputBoxes(controlStartDateDayInput);
            clearFocusableInputBoxes(controlStartDateMonthInput);
            isDateValid = false;
        }
        //按大小月精确分
        else {
            if (day > monthDays[month - 1]) {
                toast.setText(R.string.alert_msg_input_err_invalid);
                //清除日期输入框
                clearFocusableInputBoxes(controlStartDateDayInput);
                clearFocusableInputBoxes(controlStartDateMonthInput);
                isDateValid = false;
            }
        }
        return isDateValid;
    }

    /**
     * 格式化日期
     *
     * @author xjy
     */
    public String formatDateInput() {
        String dateFormatted = "";
        String monthStr = controlStartDateMonthInput.getText().toString().trim();
        String dayStr = controlStartDateDayInput.getText().toString().trim();
        int monthInt = Integer.parseInt(monthStr);
        int dayInt = Integer.parseInt(dayStr);
        String month = (monthInt >= 10) ? (monthStr) : ("0" + monthStr);
        String day = (dayInt >= 10) ? (dayStr) : ("0" + dayStr);
        dateFormatted = "2020" + "-" + month.substring(month.length() - 2, month.length()) + "-" + day.substring(day.length() - 2, day.length());
        return dateFormatted;
    }


    /**
     * 获得两个日期间距多少天
     *
     * @param smdate
     * @param bdate
     * @return
     */
    public static int getTimeDistance(Date smdate, Date bdate) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(smdate);

        long time1 = cal.getTimeInMillis();

        cal.setTime(bdate);

        long time2 = cal.getTimeInMillis();

        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 清空点击显示
     *
     * @author lym
     */
    public void clearClick() {
        //清空点击显示
        clickX = 0;
        clickY = 0;
        clickDateString = "";
        clickColorLineIndex = 0;
        //简单画一下
        draw();
    }

    /**
     * 下拉菜单，选项控制事件。
     * 重载AdapterView.OnItemSelectedListener的函数，在下拉菜单被选择时调用
     *
     * @param pos 选项的位置，0 ~ n-1
     * @author lym
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        //日志调试
        Log.i(TAG, "onItemSelected 进入函数");
        Log.i(TAG, "onItemSelected 函数中，pos = " + pos);

        //清空点击显示
        clearClick();

        int parentID = parent.getId();

        //判断是哪个spinner
        switch (parentID) {
            //第1个spinner 曲线类型
            case R.id.line_type_spinner: {
                //只要不是选择了第一条线，都不应该出现预测按钮；选择了第一条线，就出现按钮
                if (pos != 0) {
                    //如果不是第一条线，也就不应该显示预测
                    //隐藏预测开关，参数意义为：INVISIBLE:4 不可见的，但还占着原来的空间
                    forecastSwitch.setVisibility(View.INVISIBLE);
                    //同时隐藏参数们
                    paramLine2.setVisibility(View.INVISIBLE);
                    buttonLine.setVisibility(View.INVISIBLE);
                    userParamLines.setVisibility(View.INVISIBLE);
                    //线是选择的pos那条
                    curLineIndex = pos;
                } else {
                    //如果是第一条线，也就可以看看在没在预测了
                    //显示预测开关
                    forecastSwitch.setVisibility(View.VISIBLE);
                    //bug老朋友
                    //复现：先开启预测，然后切换到其他线
                    if (isForecastSwitchedOn) {
                        //如果预测按钮开着
                        //因为在我们的线系统中，跟在真实后面的就是预测线了
                        curLineIndex = lineViewModel.getNumOfRealLines();
                        //显示第2行和按钮行
                        paramLine2.setVisibility(View.VISIBLE);
                        buttonLine.setVisibility(View.VISIBLE);
                        //用户参数行要看modelType是否为控制
                        userParamLines.setVisibility(modelTypeSpinner.getSelectedItemPosition() == 0 ? View.VISIBLE : View.INVISIBLE);
                    } else {
                        //如果没在预测就正常0
                        curLineIndex = 0;
                        //同时隐藏参数们
                        paramLine2.setVisibility(View.INVISIBLE);
                        buttonLine.setVisibility(View.INVISIBLE);
                        userParamLines.setVisibility(View.INVISIBLE);

                    }
                }
                //只需要重新绘制即可
                draw();
                break;
            }
            //第2个spinner 模型类型（群体和控制）
            case R.id.model_type_spinner: {
                //选了第1个选项：控制
                if (pos == 0) {
                    Log.i(TAG, "onItemSelected 选了第2个spinner的第1个选项");
                    //用户参数行要看第二行是否出现
                    if (paramLine2.getVisibility() == View.VISIBLE) {
                        userParamLines.setVisibility(View.VISIBLE);
                    }
                }
                //选了第2个选项：群体免疫
                else {
                    Log.i(TAG, "onItemSelected 选了第2个spinner的其他选项");
                    //用户参数行应该隐藏
                    userParamLines.setVisibility(View.INVISIBLE);
                }
                break;
            }
            //第3个spinner 控制等级
            case R.id.control_level_spinner: {
                //选了非最后一项（即1～3级控制）
                if (pos != 3) {
                    Log.i(TAG, "onItemSelected 选了第3个spinner的前3个选项");
                    //天数输入框不可编辑&灰色
                    controlDurationInput.setFocusable(false);
                    controlDurationInput.setFocusableInTouchMode(false);
                    controlDurationInput.setCursorVisible(false);
                    controlDurationInput.setTextColor(Color.GRAY);
                    //到今天按钮设为不可见
                    tillTodyButton.setVisibility(View.INVISIBLE);
                    //根据选项设置默认持续时间
                    switch (pos) {
                        //一级
                        case 0:
                            controlDurationInput.setText(R.string.control_duration_level1);
                            break;
                        //二级
                        case 1:
                            controlDurationInput.setText(R.string.control_duration_level2);
                            break;
                        //三级
                        case 2:
                            controlDurationInput.setText(R.string.control_duration_level3);
                            break;
                    }
                }
                //选了最后一项（即自定义）
                else {
                    Log.i(TAG, "onItemSelected 选了第3个spinner的最后一个选项");
                    //天数输入框可以编辑&正常颜色 清空内容
                    controlDurationInput.setFocusable(true);
                    controlDurationInput.setFocusableInTouchMode(true);
                    controlDurationInput.setCursorVisible(true);
                    controlDurationInput.getText().clear();
                    controlDurationInput.setTextColor(Color.BLACK);
                    //到今天按钮设为可见
                    tillTodyButton.setVisibility(View.VISIBLE);
                }
                break;
            }
            //选择国家spinner
            case R.id.change_nation_spinner: {
                //从spinner选项得到当前选择的国家
                currentRegionName = changeNationSpinner.getSelectedItem().toString();
                Log.i(TAG, "onItemSelected: Nation Spinner  : " + currentRegionName);

                //设置国内外标志位
                WebConnect.setIsProvince(false);//不是省份，是世界

                //设置toolbar标题
                toolbarTitle.setText(getResources().getString(R.string.national_title));

                break;
            }
            //选择省spinner
            case R.id.change_province_spinner: {
                //从spinner选项得到当前选择的省
                currentRegionName = changeProvinceSpinner.getSelectedItem().toString();
                //如果是"全国" isProvince设为false 之后按世界找
                if (currentRegionName.equals("全国")) {
                    WebConnect.setIsProvince(false);
                    Log.i(TAG, "onItemSelected: Province Spinner  : 选了全国 " + currentRegionName);

                } else {
                    WebConnect.setIsProvince(true);//是省份
                    Log.i(TAG, "onItemSelected: Province Spinner  : " + currentRegionName);
                }
                break;
            }

        }

        //网络获取
        if (parentID == R.id.change_nation_spinner) {
            //如果是初次选择国家 就不做什么 更改flag之后直接return离开
            if (isFirstChooseNation) {
                isFirstChooseNation = false;
                return;
            }
            //获取世界
            getDataTask = new GetDataTask();
            Log.i(TAG, "onItemSelected点击切换国家，Web去获取世界: " + currentRegionName);
            getDataTask.execute("World");
        } else if (parentID == R.id.change_province_spinner) {
            //获取省份
            getDataTask = new GetDataTask();
            Log.i(TAG, "点击切换省份，Web去获取省份: " + currentRegionName);
            //如果是"全国" 就改名"中国" 从世界获取 否则就正常获取省份
            if (currentRegionName.equals("全国")) {
                currentRegionName = "中国";
                Log.i(TAG, "点击切换省份，全国->中国" + currentRegionName);
                getDataTask.execute("World");
            } else {
                getDataTask.execute("Province");
            }
        }

    }

    /**
     * 下拉菜单，无选择时默认事件。
     * 重载AdapterView.OnItemSelectedListener的函数，在下拉菜单没有任何选择时调用
     *
     * @author lym
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.i(TAG, "onNothingSelected 进入函数");
    }

    /**
     * 预测开关，监听开关事件。
     * 重载CompoundButton.OnCheckedChangeListener的函数，监听switch按钮有没有被选中
     *
     * @author lym, xjy
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.i(TAG, "onCheckedChanged 进入函数");
        //清空点击显示
        clearClick();
        //判断监听到的是哪个组件被选中
        switch (buttonView.getId()) {
            //如果是预测开关按钮switch
            case R.id.forecast_switch:
                isForecastSwitchedOn = isChecked;
                if (isChecked) {
                    Log.i(TAG, "onCheckedChanged 开关状态：开启，在预测");
                    //因为在我们的线系统中，跟在真实后面的就是预测线了
                    curLineIndex = lineViewModel.getNumOfRealLines();
                    paramLine2.setVisibility(View.VISIBLE);
                    buttonLine.setVisibility(View.VISIBLE);
                    //用户参数要看modelType是否为控制
                    userParamLines.setVisibility(modelTypeSpinner.getSelectedItemPosition() == 0 ? View.VISIBLE : View.INVISIBLE);
                } else {
                    Log.i(TAG, "onCheckedChanged 开关状态：关闭");
                    //因为只有第一个曲线是要预测的，关闭时就应该返回到第一个线的真实线
                    curLineIndex = 0;
                    paramLine2.setVisibility(View.INVISIBLE);
                    buttonLine.setVisibility(View.INVISIBLE);
                    userParamLines.setVisibility(View.INVISIBLE);
                }
                //无论怎样，点击了预测开关就刷新一下线图
                //只画画就可以,不用重新生成线了
                draw();
                break;
            //如果是海外/国内切换按钮toggle button
            case R.id.home_or_abroad_toggle_btn:
                //选中 状态为海外
                if (isChecked) {
                    //UI
                    changeNationSpinner.setVisibility(View.VISIBLE);
                    changeProvinceSpinner.setVisibility(View.INVISIBLE);
                    //isProvince设置为false 海外
                    WebConnect.setIsProvince(false);
                    //设置当前地区为国家spinner的选中项
                    currentRegionName = changeNationSpinner.getSelectedItem().toString();
                    //获取世界
                    getDataTask = new GetDataTask();
                    Log.i(TAG, "onItemSelected点击切换国家，Web去获取世界: " + currentRegionName);
                    getDataTask.execute("World");
                }
                //非选中 为国内
                else {
                    //UI
                    changeNationSpinner.setVisibility(View.INVISIBLE);
                    changeProvinceSpinner.setVisibility(View.VISIBLE);
                    //设置当前地区为省份spinner的选中项
                    currentRegionName = changeProvinceSpinner.getSelectedItem().toString();
                    getDataTask = new GetDataTask();
                    if (currentRegionName.equals("全国")) {
                        //如果是"全国" 则改成"中国"去世界找
                        currentRegionName = "中国";
                        Log.i(TAG, "点击切换省份为全国，Web去获取中国: " + currentRegionName);
                        WebConnect.setIsProvince(false);
                        getDataTask.execute("World");

                    } else {
                        //如果不是"全国" 则正常获取省份
                        Log.i(TAG, "点击切换省份，Web去获取省份: " + currentRegionName);
                        WebConnect.setIsProvince(true);
                        getDataTask.execute("Province");
                    }

                }
                break;
        }
    }

    /**
     * 活动生命周期：“创建”
     *
     * @param savedInstanceState ？？？系统使用参数
     * @author lym, xjy
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定组件
        bindingElements();
        //设置监听
        setListener();
        //折线图数据
        lineViewModel = ViewModelProviders.of(this).get(LineViewModel.class);
        //画折线图
        drawChart();

    }

}
