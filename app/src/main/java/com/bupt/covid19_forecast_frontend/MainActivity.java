package com.bupt.covid19_forecast_frontend;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

    //控件
    private Spinner controlLevelSpinner;
    private Button controlStartDateButton;
    private Switch myswitch;
    private EditText controlDurationInput;
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


    //折线视图
    private LineChartView myLineChartView;

    //折线的数据类
    private LineViewModel lineViewModel;

    //当前显示的线是几号
    private int curLineIndex = 0;
    //预测开关状态（默认关闭）
    private boolean isForecastSwitchedOn = false;

    /**
     * 活动生命周期：“创建”
     *
     * @param savedInstanceState ？？？系统使用参数
     * @author lym
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定组件
        bindingElements();

        //折线图数据
        lineViewModel = ViewModelProviders.of(this).get(LineViewModel.class);

        //初始化折线图数据
        //尝试传一个地区名字
        String name = "湖北";
        WebConnect.getProvince(name);//调用网络更新
        lineViewModel.initRealChart();
        lineViewModel.initForecastChart();

        //画折线图
        drawChart();
        myswitch.setOnCheckedChangeListener(this);


    }

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
        Spinner lineTypeSpinner = findViewById(R.id.line_type_spinner);
        Spinner modelTypeSpinner = findViewById(R.id.model_type_spinner);
        controlLevelSpinner = findViewById(R.id.control_level_spinner);
        controlStartDateButton = findViewById(R.id.control_start_date_button);

        lineTypeSpinner.setOnItemSelectedListener(this);
        modelTypeSpinner.setOnItemSelectedListener(this);
        controlLevelSpinner.setOnItemSelectedListener(this);
//        controlStartDateButton.setOnItemSelectedListener(this);

        //3行参数
        paramLine1 = findViewById(R.id.param_line_1);
        paramLine2 = findViewById(R.id.param_line_2);
        paramLine3 = findViewById(R.id.param_line_3);

        //switch
        myswitch = findViewById(R.id.forecast_switch);
        //edit text
        controlDurationInput = findViewById(R.id.control_duration_input);

        //people num 4个col对应4个数字 需要改数就setText
        peopleNumBarCol1 = findViewById(R.id.people_num_bar_col_1_num);
        peopleNumBarCol2 = findViewById(R.id.people_num_bar_col_2_num);
        peopleNumBarCol3 = findViewById(R.id.people_num_bar_col_3_num);
        peopleNumBarCol4 = findViewById(R.id.people_num_bar_col_4_num);
        //peopleNumBarCol1.setText("114514");


        //static element
        controlLevelLabel = findViewById(R.id.control_level_label);
        controlStartDateLabel = findViewById(R.id.control_start_date_label);
        controlDurationLabel = findViewById(R.id.control_duration_label);
        dayLabel = findViewById(R.id.day_label);
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
        //更新预测状态，这个值是表示显示的线是不是真的预测线
        boolean isForecast = (curLineIndex >= lineViewModel.getNumOfRealLines());//如果索引大于“真实线”数目，就表示是在预测
        if (isForecast) {
            //如果在预测，就重新初始化预测数据
            lineViewModel.initForecastChart();
        }
        //线
        List<Line> allLines = lineViewModel.getLines();
        List<Line> showLines = new ArrayList<>();
        //当前的
        showLines.add(allLines.get(curLineIndex));
        if (isForecast) {
            //如果在预测，加上对应的真实线
            showLines.add(allLines.get(0));
        }
        LineChartData curLineData = new LineChartData(showLines);
        //轴
        Axis[] showAxisXY = lineViewModel.getAxesList().get(curLineIndex);
        curLineData.setAxisXBottom(showAxisXY[0]);//设置X轴
        curLineData.setAxisYLeft(showAxisXY[1]);//设置Y轴
        //视图
        myLineChartView.setLineChartData(curLineData);//把这个设置好的数据放到view里面

        Log.i(TAG, "drawChart 调参师");

        //总体的图表范围
        Viewport maxViewPort = new Viewport(myLineChartView.getMaximumViewport());
        maxViewPort.left = 0;
        maxViewPort.bottom = 0;
        //x轴最大坐标值
        maxViewPort.right = 120 + 15 - 1;
        //y轴最大坐标值
        maxViewPort.top = 6000;
        myLineChartView.setMaximumViewport(maxViewPort);

        //显示的小界面，可以滑动
        Viewport halfViewport = new Viewport(myLineChartView.getCurrentViewport());
        halfViewport.top = 1300;
        halfViewport.bottom = 0;
        halfViewport.left = 0;
        if (isForecast) {
            //如果在预测
            Log.i(TAG, "setChartShow 函数：【预测】设置当前范围");
            //真实120预测15
            halfViewport.right = 120 + 15 - 1;
        } else {
            Log.i(TAG, "setChartShow 函数：【真实】设置当前范围");
            halfViewport.right = 120;
        }
        myLineChartView.setCurrentViewport(halfViewport);
    }

    /*————————————控件相关————————————*/

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
        //判断是哪个spinner
        switch (parent.getId()) {
            //第1个spinner 曲线类型
            case R.id.line_type_spinner:
                //只要不是选择了第一条线，都不应该出现预测按钮；选择了第一条线，就出现按钮
                if (pos != 0) {
                    myswitch.setVisibility(View.INVISIBLE);//隐藏，参数意义为：INVISIBLE:4 不可见的，但还占着原来的空间
                    curLineIndex = pos;
                } else {
                    myswitch.setVisibility(View.VISIBLE);//显示
                    if (isForecastSwitchedOn) {
                        //因为在我们的线系统中，跟在真实后面的就是预测线了
                        curLineIndex = lineViewModel.getNumOfRealLines();
                    } else {
                        //如果没在预测就正常0
                        curLineIndex = 0;
                    }
                }
                break;
            //第2个spinner 模型类型（群体和控制）
            case R.id.model_type_spinner:
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
                break;
            //第3个spinner 控制等级
            case R.id.control_level_spinner:
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
                break;
//            //第4个spinner 控制开始日期
//            case R.id.control_start_date_spinner:
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
//                break;
        }
        //刷新 线
        drawChart();
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
}
