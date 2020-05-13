package com.bupt.covid19_forecast_frontend;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //日志TAG，调试用，默认使用类名
    private static final String TAG = "MainActivity";

    /**
     * 重载AppCompatActivity的函数，在活动创建时调用
     *
     * @param savedInstanceState ？？？系统使用参数
     * @author lym
     * @version 1.0
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initLines();
        initChart();
        initAxis();
        showPartOfChart();
        //spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
    }

    /*————————————绘图相关————————————*/

    private int numberOfPoints = 120; //节点数
    private int maxNumberOfLines = 4; //图上折线/曲线的最多条数
    private int curLineIndex = 0;//当前显示的线是几号
    private LineChartView myLineChartView; //折线图的view
    private float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints]; //将线上的点放在一个数组中
    private List<Line> lines = new ArrayList<>(); //所有线
    private LineChartData myLineData = new LineChartData(lines); //当前显示数据


    /**
     * 自制-初始化数据
     *
     * @Description 初始化randomNumbersTab数据，目前就先用随机数
     * @author lym
     * @version 1.0
     */
    private void initData() {
        Log.i(TAG, "initData 进入函数");
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                Random random = new Random();
                randomNumbersTab[i][j] = random.nextInt(50) + j * 10;
            }
        }
    }

    /**
     * 自制-初始化“线”
     *
     * @Description 初始化line数组
     * @author lym
     * @version 1.0
     */
    private void initLines() {
        Log.i(TAG, "initLines 进入函数");
        //循环将每条线都设置成对应的属性
        for (int i = 0; i < maxNumberOfLines; i++) {
            List<PointValue> tempArrayList = new ArrayList<>();//一条线的数据
            for (int j = 0; j < numberOfPoints; j++) {
                tempArrayList.add(new PointValue(j, randomNumbersTab[i][j]));
            }
            Line line = new Line(tempArrayList);//根据值来创建一条线
            line.setColor(Color.rgb(126, 185, 236));//线的颜色
            //line.setPointColor(Color.rgb(255,255,255));//点的颜色 这个是白色
            line.setPointRadius(5);//点的大小
            line.setHasLabelsOnlyForSelected(true);//点的标签在点击的时候显示
            line.setFilled(true);//下方填充
            line.setCubic(false);//不要曲线
            lines.add(line);
        }
    }

    /**
     * 自制-初始化图表
     *
     * @Description 初始化图表信息，包括绑定视图、设置图表控件
     * @author lym
     * @version 1.0
     */
    private void initChart() {
        Log.i(TAG, "initChart 进入函数");
        myLineChartView = findViewById(R.id.chart); //绑定视图
        List<Line> curLines = lines.subList(curLineIndex, curLineIndex + 1);//去除不需要的条数
        myLineData = new LineChartData(curLines);//设置为显示的条数
        myLineChartView.setLineChartData(myLineData);//设置图表控件
    }

    /**
     * 自制-初始化坐标轴
     *
     * @author lym
     * @version 1.0
     */
    private void initAxis() {
        Log.i(TAG, "initAxis 进入函数");
        //坐标轴
        Axis axisX = new Axis();
        Axis axisY = new Axis();

        //增加“日期”系列x轴
        List<AxisValue> valueListX = new ArrayList<>();//新建一个x轴的值列表
        int day = 0;
        for (int i = 0; i < numberOfPoints; i++) {
            AxisValue valueX = new AxisValue(i);//这里的数字是float，作为坐标的数值
            day++;
            if (day > 30) {
                day %= 30;
            }
            valueX.setLabel("5" + "/" + day);//将数值和文字标签绑定起来
            valueListX.add(valueX);//添加一个值
        }
        axisX.setValues(valueListX);//将列表设置到x轴上面

        myLineData.setAxisXBottom(axisX); //设置X轴位置 下方
        myLineData.setAxisYLeft(axisY); //设置Y轴位置 左边
    }

    /**
     * 自制-绘图
     *
     * @Description 调整显示的图表的范围
     * @author lym
     * @version 1.0
     */
    private void showPartOfChart() {
        Log.i(TAG, "showPartOfChart 进入函数");

        final Viewport MAX = new Viewport(myLineChartView.getMaximumViewport());//创建一个图表视图 大小为控件的最大大小
        final Viewport CUR = new Viewport(myLineChartView.getCurrentViewport());

        final Viewport fullViewport = new Viewport(MAX);
        fullViewport.top = 300;
        fullViewport.bottom = -20;//最下面显示的y轴坐标值
        fullViewport.left = -1;//最左边显示的x轴坐标值
        fullViewport.right = numberOfPoints;

        final Viewport halfViewport = new Viewport(CUR);
        halfViewport.top = fullViewport.top;
        halfViewport.bottom = fullViewport.bottom;//最下面显示的y轴坐标值
        halfViewport.left = fullViewport.left;//最左边显示的x轴坐标值
        halfViewport.right = 15;

        myLineChartView.setMaximumViewport(fullViewport);   //给最大的视图设置 相当于原图
        myLineChartView.setCurrentViewport(halfViewport);   //给当前的视图设置 相当于当前展示的图
    }


    /*————————————spinner相关————————————*/

    /**
     * @param pos 选项的位置，0 ~ n-1
     * @Description 重载AdapterView.OnItemSelectedListener的函数，在下拉菜单被选择时调用
     * @author lym
     * @version 1.0
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        //日志调试
        Log.i(TAG, "onItemSelected 进入函数");
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.i(TAG, "onItemSelected 函数中，pos = " + pos);
        curLineIndex = pos;
        initChart();
        initAxis();
        showPartOfChart();
    }

    /**
     * @Description 重载AdapterView.OnItemSelectedListener的函数，在下拉菜单没有任何选择时调用
     * @author lym
     * @version 1.0
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.i(TAG, "onNothingSelected 进入函数");
    }

}
