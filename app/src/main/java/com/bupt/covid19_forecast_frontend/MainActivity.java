package com.bupt.covid19_forecast_frontend;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import viewModel.LineViewModel;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    //日志TAG，调试用，默认使用类名
    private static final String TAG = "MainActivity";

    //ui控件
    //因为切换预测的按钮要根据状态不同显示和隐藏，所以放在外面供全局调用
    private Switch myswitch;

    //折线视图
    private LineChartView myLineChartView;

    //折线的数据类
    LineViewModel lineViewModel;

    /**
     * 活动生命周期：“创建”
     *
     * @param savedInstanceState ？？？系统使用参数
     * @author lym
     * @version 1.3
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //chart
        myLineChartView = findViewById(R.id.chart);

        //折线图数据
        lineViewModel = ViewModelProviders.of(this).get(LineViewModel.class);

        lineViewModel.initChart();

        drawChart();

        //spinner
        Spinner spinner = findViewById(R.id.line_type_spinner);
        spinner.setOnItemSelectedListener(this);
        //switch
        myswitch = findViewById(R.id.forecast_switch);
        myswitch.setOnCheckedChangeListener(this);
    }


    /**
     * 刷新图像。
     * 刷新图像，包括绑定视图、坐标轴、显示位置、显示区域范围
     *
     * @author lym
     * @version 3.1
     */
    private void drawChart() {
        Log.i(TAG, "draw 进入函数");
        Log.i(TAG, "draw 函数：curLineIndex：" + lineViewModel.getCurLineIndex());

        LineChartData myLineData = new LineChartData(lineViewModel.getLines().subList(lineViewModel.getCurLineIndex(),
                lineViewModel.getCurLineIndex() + 1));//把没用的线去掉
        myLineData.setAxisXBottom(lineViewModel.getAxesList().get(lineViewModel.getCurLineIndex())[0]);//设置X轴
        myLineData.setAxisYLeft(lineViewModel.getAxesList().get(lineViewModel.getCurLineIndex())[1]);//设置Y轴
        myLineChartView.setLineChartData(myLineData);//把这个设置好的数据放到view里面
        boolean isOnForecast = (lineViewModel.getCurLineIndex() > lineViewModel.getNumOfRealLines());//如果索引大于“真实线”数目，就表示是在预测
        setChartShow(isOnForecast);//设置显示图表的范围，为“调参师”专门准备
    }

    /**
     * 设置显示范围
     * 设置当前图表的显示范围，其中最大坐标指的是显示窗口的那个值，因为可以滑动
     *
     * @param isForecast 是不是真的在显示预测图表
     * @author lym
     * @version 2.1
     */
    private void setChartShow(boolean isForecast) {
        final Viewport halfViewport = new Viewport(myLineChartView.getCurrentViewport());
        halfViewport.bottom = 0;
        //TODO step of y ? 是时候考虑y轴的步长问题了
        halfViewport.top = 300;//y轴最大坐标值
        halfViewport.left = 0;
        if (isForecast) {
            //如果在预测
            int numOfForecastPoints = 15;
            halfViewport.right = numOfForecastPoints - 1;
        } else {
            halfViewport.right = 25;//x轴最大坐标值
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
     * @version 2.4
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        //日志调试
        Log.i(TAG, "onItemSelected 进入函数");
        Log.i(TAG, "onItemSelected 函数中，pos = " + pos);
        //只要不是选择了第一条线，都不应该出现预测按钮；选择了第一条线，就出现按钮
        if (pos != 0) {
            myswitch.setVisibility(View.INVISIBLE);//隐藏，参数意义为：INVISIBLE:4 不可见的，但还占着原来的空间
            lineViewModel.setCurLineIndex(pos);
        } else {
            myswitch.setVisibility(View.VISIBLE);//显示
            if (lineViewModel.isForecast()) {
                //因为在我们的线系统中，跟在真实后面的就是预测线了
                lineViewModel.setCurLineIndex(lineViewModel.getNumOfRealLines());
            } else {
                //如果没在预测就正常0
                lineViewModel.setCurLineIndex(0);
            }
        }
        //刷新 线
        drawChart();
    }

    /**
     * 下拉菜单，无选择时默认事件。
     * 重载AdapterView.OnItemSelectedListener的函数，在下拉菜单没有任何选择时调用
     *
     * @author lym
     * @version 1.0
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
     * @version 2.2
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.i(TAG, "onCheckedChanged 进入函数");
        lineViewModel.setForecast(isChecked);
        if (isChecked) {
            Log.i(TAG, "onCheckedChanged 开关状态：开启");
            //因为在我们的线系统中，跟在真实后面的就是预测线了
            lineViewModel.setCurLineIndex(lineViewModel.getNumOfRealLines());
            drawChart();
        } else {
            Log.i(TAG, "onCheckedChanged 开关状态：关闭");
            //因为只有第一个曲线是要预测的，关闭时就应该返回到第一个线的真实线
            lineViewModel.setCurLineIndex(0);
            drawChart();
        }
    }
}
