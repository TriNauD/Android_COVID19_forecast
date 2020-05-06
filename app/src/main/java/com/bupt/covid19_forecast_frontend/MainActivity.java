package com.bupt.covid19_forecast_frontend;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

@TargetApi(21)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        /*toolbar.setTitle("全国疫情趋势");
        setSupportActionBar(toolbar);*/
        showChart();
    }

    /**
     * 显示图表
     */
    public void showChart() {
        int numberOfPoints = 8;                     //节点数
        int maxNumberOfLines = 4;                   //图上折线/曲线的最多条数
        int numberOfLines = 1;                      //图上折线/曲线的显示条数
        float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints]; //将线上的点放在一个数组中
        List<Line> lines = new ArrayList<>();       //所有线

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

        LineChartView myLineChartView = findViewById(R.id.chart); //绑定视图
        LineChartData myLineData = new LineChartData(lines); //数据
        myLineChartView.setLineChartData(myLineData);    //设置图表控件

        //坐标轴
        Axis axisX = new Axis();
        Axis axisY = new Axis();
        axisX.setName("Axis X");
        axisY.setName("Axis Y");
        myLineData.setAxisXBottom(axisX);            //设置X轴位置 下方
        myLineData.setAxisYLeft(axisY);              //设置Y轴位置 左边

        //计算并绘图
        final Viewport v = new Viewport(myLineChartView.getMaximumViewport());//创建一个图标视图 大小为控件的最大大小
        v.left = 0;                             //坐标原点在左下
        v.bottom = 0;
        v.top = 100;                            //最高点为100
        v.right = numberOfPoints - 1;           //右边为点 坐标从0开始 点号从1 需要 -1
        myLineChartView.setMaximumViewport(v);   //给最大的视图设置 相当于原图
        myLineChartView.setCurrentViewport(v);   //给当前的视图设置 相当于当前展示的图

    }
}
