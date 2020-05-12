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
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //日志TAG，调试用，默认使用类名
    private static final String TAG = "MainActivity";

    /**
     * 重载AppCompatActivity的函数，在活动创建时调用
     * @param savedInstanceState ？？？系统使用参数
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLines();
        initChart();
        initAxis();
        showChart();
        //spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
    }

    /*————————————绘图相关————————————*/

    private static int numberOfPoints = 8; //节点数
    private static int numberOfLines = 1; //图上折线/曲线的显示条数
    private static int maxNumberOfLines = 4; //图上折线/曲线的最多条数
    private LineChartView myLineChartView; //折线图的view
    private float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints]; //将线上的点放在一个数组中
    private List<Line> lines = new ArrayList<>(); //所有线
    private LineChartData myLineData = new LineChartData(lines); //数据

    /**
     * 自制-初始化“线”
     * 功能：初始化line数组
     * */
    private void initLines(){
        Log.i(TAG, "initLines 进入函数");

        //循环将每条线都设置成对应的属性
        for (int i = 0; i < numberOfLines; ++i) {
            //节点的值
            List<PointValue> values = new ArrayList<>();
            for (int j = 0; j < numberOfPoints; ++j) {
                Random random = new Random();
                randomNumbersTab[i][j] = random.nextInt(100);
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }

            Line line = new Line(values);               //根据值来创建一条线

            line.setColor(Color.rgb(126,185,236));  //线的颜色
            line.setCubic(true);        //曲线

            lines.add(line);
        }
    }

    /**
     * 自制-初始化图表
     * 功能：初始化图表信息，包括绑定视图、设置图表控件
     */
    private void initChart() {
        Log.i(TAG, "initChart 进入函数");
        myLineChartView = findViewById(R.id.chart); //绑定视图
        myLineChartView.setLineChartData(myLineData);    //设置图表控件
   }

   /**
    * 自制-初始化坐标轴
    * */
   private void initAxis(){
       Log.i(TAG, "initAxis 进入函数");
       //坐标轴
       Axis axisX = new Axis();
       Axis axisY = new Axis();
       myLineData.setAxisXBottom(axisX); //设置X轴位置 下方
       myLineData.setAxisYLeft(axisY); //设置Y轴位置 左边
   }

    /**
     * 自制-绘图
     * 功能：调用后会绘图，达到刷新图像的目的
     * */
    private void showChart(){
        Log.i(TAG, "showChart 进入函数");
        final Viewport v = new Viewport(myLineChartView.getMaximumViewport());//创建一个图表视图 大小为控件的最大大小
        v.left = 0;                             //坐标原点在左下
        v.bottom = 0;
        v.top = 100;                            //最高点为100
        v.right = numberOfPoints - 1;           //右边为点 坐标从0开始 点号从1 需要 -1
        myLineChartView.setMaximumViewport(v);   //给最大的视图设置 相当于原图
        myLineChartView.setCurrentViewport(v);   //给当前的视图设置 相当于当前展示的图
    }


    /*————————————spinner相关————————————*/

    /**
     * 重载AdapterView.OnItemSelectedListener的函数，在下拉菜单被选择时调用
     * @param pos 选项的位置，0 ~ n-1
     * */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        //日志调试
        Log.i(TAG, "onItemSelected 进入函数");
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.i(TAG, "onItemSelected 函数中，pos = " + pos);
        //TODO:应该按照pos决定图表的线，现在的效果是多显示了一个线（？
        initLines();//重新初始化线
        initChart();//重新初始化图表
        initAxis();//坐标轴
        showChart();//绘图
    }

    /**
     * 重载AdapterView.OnItemSelectedListener的函数，在下拉菜单没有任何选择时调用
     * */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.i(TAG, "onNothingSelected 进入函数");
    }

}
