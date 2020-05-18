package com.bupt.covid19_forecast_frontend;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    //日志TAG，调试用，默认使用类名
    private static final String TAG = "MainActivity";

    //一些控件
    private Switch myswitch;
    private Spinner spinner;
    private LineChartView myLineChartView;//折线图

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
        initChart();
        drawChart();
        //spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        //switch
        myswitch = (Switch) findViewById(R.id.forecast_switch);
        myswitch.setOnCheckedChangeListener(this);
    }

    /*————————————绘图相关————————————*/

    private int numOfRealLines = 4;//“真实线”的数量
    private int numOfRealPoints = 120;//“真实线”的节点数
    private int numOfForecastLines = 1;//“预测线”的数量
    private int numOfForecastPoints = 15;//“预测线”的节点数
    //TODO 把data也做成先真实后预测得了
    private float[][] realLineData = new float[numOfRealLines][numOfRealPoints];//“真实线”的数据存放到二维数组中
    private float[][] forecastLineData = new float[numOfForecastLines][numOfRealPoints];//“预测线”的数据存放到二维数组中
    //另外一个参考方案：也可以放到一个数组里：private int[] numOfLines = {4, 1};//“真实线”与“预测线”的线数量
    private List<Line> lines = new ArrayList<>(); //所有线，里面是按照先“真实”后“预测”的顺序
    private List<Axis[]> axesList = new ArrayList<>(); //所有坐标轴，里面是按照先“真实”后“预测”的顺序
    private List<List<String>> axisLableList = new ArrayList<>(); //所有坐标轴的标签信息，里面是按照先“真实”后“预测”的顺序
    private int curLineIndex = 0;//当前显示的线是几号
    private boolean isForecast = false;//是否处于预测状态

    /**
     * 初始化
     *
     * @Description 初始化，包括数据、线、轴；数据先用随机数
     * @author lym
     * @version 2.3
     */
    private void initChart() {
        Log.i(TAG, "initChart 进入函数");

        //总体的代码结构：
        /*
        * for (int i = 0; i < numOfXXXLines; i++) {
            for (int j = 0; j < numOfXXXPoints; j++) {
            }
        }
        * */

        //————真实————
        //数
        for (int i = 0; i < numOfRealLines; ++i) {
            for (int j = 0; j < numOfRealPoints; ++j) {
                Random random = new Random();
                realLineData[i][j] = random.nextInt(50) + j * 10;
            }
        }
        //线
        for (int i = 0; i < numOfRealLines; i++) {
            List<PointValue> tempArrayList = new ArrayList<>();//一条线的数据
            for (int j = 0; j < numOfRealPoints; j++) {
                tempArrayList.add(new PointValue(j, realLineData[i][j]));
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
        //轴
        //字符串部分
        for (int i = 0; i < numOfRealLines; i++) {
            List<String> strings = new ArrayList<>(numOfRealPoints);
            int day = 0;
            for (int j = 0; j < numOfRealPoints; j++) {
                day++;
                if (day > 30) {
                    day %= 30;
                }
                String s = (i + 1) + "/" + day;
                strings.add(s);
            }
            axisLableList.add(strings);
        }
        //绑定轴部分
        //每条线
        for (int i = 0; i < numOfRealLines; i++) {
            Axis axisX = new Axis();//新建一个x轴
            List<AxisValue> valueListX = new ArrayList<>();//新建一个x轴的值列表
            //每个点
            for (int j = 0; j < numOfRealPoints; j++) {
                AxisValue valueX = new AxisValue(j);//这里的数字是坐标的数值，比如第一个坐标就是0
                valueX.setLabel(axisLableList.get(i).get(j));//将坐标的数值和对应的文字标签绑定起来
                valueListX.add(valueX);//添加一个值
            }
            axisX.setValues(valueListX);//将列表设置到x轴上面
            Axis axisY = new Axis();//Y轴没有任何设定，就初始化
            Axis[] axisXY = {axisX, axisY};//把XY放到一起
            axesList.add(axisXY);//加入总的坐标轴列表
        }

        //————预测————
        //数
        for (int i = 0; i < numOfForecastLines; ++i) {
            for (int j = 0; j < numOfForecastPoints; ++j) {
                Random random = new Random();
                forecastLineData[i][j] = random.nextInt(50) + j * 10;
            }
        }
        //线
        for (int i = 0; i < numOfForecastLines; i++) {
            List<PointValue> tempArrayList = new ArrayList<>();//一条线的数据
            for (int j = 0; j < numOfForecastPoints; j++) {
                tempArrayList.add(new PointValue(j, forecastLineData[i][j]));
            }
            Line line = new Line(tempArrayList);//根据值来创建一条线
            //line.setColor(Color.rgb(126, 185, 236));//线的颜色
            line.setColor(Color.rgb(255, 0, 0));//线的颜色
            line.setPointColor(Color.rgb(255, 255, 255));//点的颜色 这个是白色
            line.setPointRadius(5);//点的大小
            line.setHasLabelsOnlyForSelected(true);//点的标签在点击的时候显示
            line.setFilled(false);//下方填充就不要了吧
            line.setCubic(false);//不要曲线
            lines.add(line);
        }
        //轴
        Axis axisX = new Axis();//新建一个x轴
        Axis axisY = new Axis();//Y轴没有任何设定，就初始化
        Axis[] axisXY = {axisX, axisY};//把XY放到一起
        axesList.add(axisXY);//加入总的坐标轴列表
    }


    /**
     * 刷新图像
     *
     * @Description 刷新图像，包括绑定视图、坐标轴、显示位置、显示区域范围
     * @author lym
     * @version 3.0
     */
    private void drawChart() {
        Log.i(TAG, "draw 进入函数");
        Log.i(TAG, "draw 函数：curLineIndex：" + curLineIndex);

        //设置数据
        LineChartData myLineData = new LineChartData(lines.subList(curLineIndex, curLineIndex + 1));
        myLineData.setAxisXBottom(axesList.get(curLineIndex)[0]); //设置X轴位置 下方
        myLineData.setAxisYLeft(axesList.get(curLineIndex)[1]); //设置Y轴位置 左边
        myLineChartView.setLineChartData(myLineData);
        setChartShow(300, 25);//为“调参师”专门准备
    }

    /**
     * 设置显示范围
     *
     * @param top   y轴最大坐标值
     * @param right x轴最大坐标值
     * @Description 设置当前图表的显示范围，其中最大坐标指的是显示窗口的那个值，因为可以滑动
     * @author lym
     * @version 2.0
     */
    private void setChartShow(int top, int right) {
        final Viewport halfViewport = new Viewport(myLineChartView.getCurrentViewport());
        halfViewport.bottom = 0;
        //TODO 是时候考虑y轴的步长问题了
        halfViewport.top = top;
        halfViewport.left = 0;
        if (isForecast) {
            //如果在预测
            halfViewport.right = numOfForecastPoints - 1;
        } else {
            halfViewport.right = right;
        }
        myLineChartView.setCurrentViewport(halfViewport);
    }

    /*————————————控件相关————————————*/

    /**
     * 下拉菜单，选项控制事件
     *
     * @param pos 选项的位置，0 ~ n-1
     * @Description 重载AdapterView.OnItemSelectedListener的函数，在下拉菜单被选择时调用
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
            curLineIndex = pos;
        } else {
            myswitch.setVisibility(View.VISIBLE);//显示
            if (isForecast) {
                curLineIndex = numOfRealLines;//因为在我们的线系统中，跟在真实后面的就是预测线了
            } else {
                curLineIndex = 0;//如果没在预测就正常0
            }
        }
        //刷新 线
        drawChart();
    }

    /**
     * 下拉菜单，无选择时默认事件
     *
     * @Description 重载AdapterView.OnItemSelectedListener的函数，在下拉菜单没有任何选择时调用
     * @author lym
     * @version 1.0
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.i(TAG, "onNothingSelected 进入函数");
    }

    /**
     * 预测开关，监听开关事件
     *
     * @Description 重载CompoundButton.OnCheckedChangeListener的函数，监听switch按钮有没有被选中
     * @author lym
     * @version 2.2
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.i(TAG, "onCheckedChanged 进入函数");
        isForecast = isChecked;//改变预测状态
        if (isChecked) {
            Log.i(TAG, "onCheckedChanged 开关状态：开启");
            curLineIndex = numOfRealLines;//因为在我们的线系统中，跟在真实后面的就是预测线了
            drawChart();
        } else {
            Log.i(TAG, "onCheckedChanged 开关状态：关闭");
            curLineIndex = 0;//因为只有第一个曲线是要预测的，关闭时就应该返回到第一个线的真实线
            drawChart();
        }
    }
}
