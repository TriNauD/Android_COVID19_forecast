package com.bupt.covid19_forecast_frontend;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import viewModel.WebConnect;
import viewModel.LineViewModel;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    //日志TAG，调试用，默认使用类名
    private static final String TAG = "MainActivity";

    //ui控件
    private Spinner controlLevelSpinner;
    private Spinner changeNationSpinner;
    private Spinner lineTypeSpinner;
    private Spinner modelTypeSpinner;
    private Switch forecastSwitch;
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
    private RelativeLayout paramLine3;
    private ProgressBar progressBar;

    //获取数据线程
    private GetDataTask getDataTask;
    private GetPredictDataTask getPredictDataTask;
    //当前国家
    private String currentNation = "中国";

    //折线视图
    private LineChartView myLineChartView;
    //折线的数据类
    private LineViewModel lineViewModel;
    //当前显示的线是几号
    private int curLineIndex = 0;
    //预测开关状态（默认开启）
    private boolean isForecastSwitchedOn = true;


    /**
     * 获取数据线程类
     * 异步运行获取数据 获取结束后反映在ui组件上（加载中图标消失）
     *
     * @author xjy
     */
    private class GetDataTask extends AsyncTask<String, Integer, String> {
        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作
        @Override
        protected void onPreExecute() {
        }

        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        @Override
        protected String doInBackground(String... params) {
            try {
                Log.i(TAG, "Loading...真实当前国家：" + currentNation);

                //先设置为没有开始获取
                WebConnect.isDataGotten = false;
                progressBar.setVisibility(View.VISIBLE);
                Log.i(TAG, "Loading...isDataGotten开始转圈圈所以设为F：" + WebConnect.isDataGotten);


                //去获取数据，如果成功会将isDataGotten设置为true
                WebConnect.getWorld(currentNation);

                //如果没有得到数据，就一直刷新图表
                while (!WebConnect.isDataGotten) {
                    Thread.sleep(1);
                }

                //成功之后，最后一次再刷新一下图表
                drawChart();

                Log.i(TAG, "Loading" + currentNation + "结束真实线");

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
            // 执行完毕后，则更新UI
            progressBar.setVisibility(View.INVISIBLE);
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

    private class GetPredictDataTask extends AsyncTask<String, Integer, String> {
        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作
        @Override
        protected void onPreExecute() {
        }

        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        @Override
        protected String doInBackground(String... params) {
            try {
                Log.i(TAG, "Loading...预测当前国家：" + currentNation);

                //先设置为没有开始获取
                WebConnect.isDataGotten = false;
                progressBar.setVisibility(View.VISIBLE);
                Log.i(TAG, "Loading...isDataGotten开始转圈圈所以设为F：" + WebConnect.isDataGotten);


                //去获取数据，如果成功会将isDataGotten设置为true
                WebConnect.getPredict(currentNation);

                //如果没有得到数据，就一直刷新图表
                while (!WebConnect.isDataGotten) {
                    Thread.sleep(1);
                }

                //成功之后，最后一次再刷新一下图表
                drawChart();

                Log.i(TAG, "Loading" + currentNation + "结束预测线");

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
            // 执行完毕后，则更新UI
            progressBar.setVisibility(View.INVISIBLE);
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


        //----------------------- 视图 ------------------------------
        //把这个设置好的数据放到view里面
        myLineChartView.setLineChartData(curLineData);

        Log.i(TAG, "drawChart 调参师");

        //获取后端的节点数目
        int numOfPoint = lineViewModel.getNumOfPoint();

        //总体的图表范围
        Viewport maxViewPort = new Viewport(myLineChartView.getMaximumViewport());
        maxViewPort.left = 0;
        maxViewPort.bottom = 0;
        //x轴最大坐标值
        maxViewPort.right = numOfPoint;
        //y轴最大坐标值
        maxViewPort.top = 2200000;
        myLineChartView.setMaximumViewport(maxViewPort);

        //显示的小界面，可以滑动
        Viewport halfViewport = new Viewport(myLineChartView.getCurrentViewport());
        halfViewport.top = 2200000;
        halfViewport.bottom = 0;
        halfViewport.left = 0;
        if (isForecast) {
            //如果在预测
            Log.i(TAG, "setChartShow 函数：【预测】设置当前范围");
            //真实120预测15
            halfViewport.right = numOfPoint;
        } else {
            Log.i(TAG, "setChartShow 函数：【真实】设置当前范围");
            halfViewport.right = numOfPoint;
        }
        myLineChartView.setCurrentViewport(halfViewport);
    }

    /*————————————控件相关————————————*/

    /**
     * 绑定组件。
     * 绑定xml的组件
     *
     * @author xjy
     */
    public void bindingElements() {
        //chart
        myLineChartView = findViewById(R.id.chart);
        //spinner 页面的4个spinner并绑定listener
        lineTypeSpinner = findViewById(R.id.line_type_spinner);
        modelTypeSpinner = findViewById(R.id.model_type_spinner);
        controlLevelSpinner = findViewById(R.id.control_level_spinner);
        changeNationSpinner = findViewById(R.id.change_nation_spinner);

        //3行参数
        paramLine1 = findViewById(R.id.param_line_1);
        paramLine2 = findViewById(R.id.param_line_2);
        paramLine3 = findViewById(R.id.param_line_3);

        //switch
        forecastSwitch = findViewById(R.id.forecast_switch);

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

        //progress bar
        progressBar = findViewById(R.id.progress_bar);

        peopleNumBarCol1.setText("现存确诊");
        peopleNumBarCol2.setText("累计确诊");
        peopleNumBarCol3.setText("累计治愈");
        peopleNumBarCol4.setText("累计死亡");


        //static element
        controlLevelLabel = findViewById(R.id.control_level_label);
        controlStartDateLabel = findViewById(R.id.control_start_date_label);
        controlDurationLabel = findViewById(R.id.control_duration_label);
        dayLabel = findViewById(R.id.day_label);
    }

    /**
     * 设置多个监听器
     *
     * @author xjy
     */
    public void setListener() {
        //switch设置listener
        forecastSwitch.setOnCheckedChangeListener(this);
        //spinner设置listener
        lineTypeSpinner.setOnItemSelectedListener(this);
        modelTypeSpinner.setOnItemSelectedListener(this);
        controlLevelSpinner.setOnItemSelectedListener(this);
        changeNationSpinner.setOnItemSelectedListener(this);
        //input设置listener
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
                    paramLine3.setVisibility(View.INVISIBLE);
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
                        //同时显示参数们
                        paramLine2.setVisibility(View.VISIBLE);
                        paramLine3.setVisibility(View.VISIBLE);
                    } else {
                        //如果没在预测就正常0
                        curLineIndex = 0;
                        //同时隐藏参数们
                        paramLine2.setVisibility(View.INVISIBLE);
                        paramLine3.setVisibility(View.INVISIBLE);
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
                    LineViewModel.setHasControl(true);//控制：是
                    //控制等级spinner应该保持出现
                    controlLevelSpinner.setVisibility(View.VISIBLE);
                    controlLevelLabel.setVisibility(View.VISIBLE);
                    //第三行要看第二行是否出现
                    if (paramLine2.getVisibility() == View.VISIBLE) {
                        paramLine3.setVisibility(View.VISIBLE);
                    }
                }
                //选了第2个选项：群体免疫
                else {
                    Log.i(TAG, "onItemSelected 选了第2个spinner的其他选项");
                    LineViewModel.setHasControl(false);//控制：否
                    //第三行和控制等级spinner应该隐藏
                    paramLine3.setVisibility(View.INVISIBLE);
                    controlLevelSpinner.setVisibility(View.INVISIBLE);
                    controlLevelLabel.setVisibility(View.INVISIBLE);
                }
                //画图
                drawChart();
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

                }
                drawChart();
                break;
            }
            //选择国家
            case R.id.change_nation_spinner: {
                //从spinner选项得到当前选择的国家
                currentNation = changeNationSpinner.getSelectedItem().toString();
                //设置toolbar标题
                toolbarTitle.setText(currentNation + getResources().getString(R.string.national_title));
                Log.i(TAG, "onItemSelected:国家名 " + currentNation);
                drawChart();
                break;
            }
            //第4个spinner 控制开始日期
//            case R.id.control_start_date_spinner: {
//                //选了非最后一项
//                if (pos != 3) {
//                    Log.i(TAG, "onItemSelected 选了第4个spinner的前3个选项");
//                    //天数输入框不可编辑&灰色
//                }
//                //选了最后一项
//                else {
//                    Log.i(TAG, "onItemSelected 选了第4个spinner的最后一个选项");
//                    //天数输入框可以编辑&正常颜色
//                }
//                drawChart();
//                break;
//            }
        }

        //网络获取
        if (parentID == R.id.change_nation_spinner) {
            //获取世界
            getDataTask = new GetDataTask();
            getDataTask.execute();
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
     * @author lym
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.i(TAG, "onCheckedChanged 进入函数");
        isForecastSwitchedOn = isChecked;
        if (isChecked) {
            Log.i(TAG, "onCheckedChanged 开关状态：开启，在预测");
            //因为在我们的线系统中，跟在真实后面的就是预测线了
            curLineIndex = lineViewModel.getNumOfRealLines();
            paramLine2.setVisibility(View.VISIBLE);
            paramLine3.setVisibility(View.VISIBLE);

            //获取预测
            getPredictDataTask = new GetPredictDataTask();
            getPredictDataTask.execute();

        } else {
            Log.i(TAG, "onCheckedChanged 开关状态：关闭");
            //因为只有第一个曲线是要预测的，关闭时就应该返回到第一个线的真实线
            curLineIndex = 0;
            paramLine2.setVisibility(View.INVISIBLE);
            paramLine3.setVisibility(View.INVISIBLE);
        }
        //无论怎样，点击了预测开关就刷新一下线图
        drawChart();
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
