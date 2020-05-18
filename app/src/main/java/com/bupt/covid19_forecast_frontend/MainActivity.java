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

    /**
     * 活动生命周期：“创建”
     *
     * @param savedInstanceState ？？？系统使用参数
     * @author lym
     * @version 1.2
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化数据
        initData();
        //刷新图表
        draw();
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
    private int numOfForecastPoints = 12;//“预测线”的节点数
    private float[][] realLineData = new float[numOfRealLines][numOfRealPoints];//“真实线”的数据存放到二维数组中
    private float[][] forecastLineData = new float[numOfForecastLines][numOfRealPoints];//“预测线”的数据存放到二维数组中
    //另外一个参考方案：也可以放到一个数组里：private int[] numOfLines = {4, 1};//“真实线”与“预测线”的线数量
    private List<Line> lines = new ArrayList<>(); //所有线，里面是按照先“真实”后“预测”的顺序
    private LineChartData myLineData = new LineChartData(lines); //当前显示数据
    private LineChartView myLineChartView; //折线图的view
    private int curLineIndex = 0;//当前显示的线是几号
    private boolean isForecast = false;//是否处于预测状态

    /**
     * 初始化所有数据，包括“数”和“线”
     *
     * @Description 初始化数据，目前就先用随机数；初始化线
     * @author lym
     * @version 2.1
     */
    private void initData() {
        Log.i(TAG, "initData 进入函数");

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

        myLineChartView = findViewById(R.id.chart); //绑定视图

        //给坐标轴赋值
        //“真实”
        for (int j = 0; j < numOfRealLines; j++) {
            //增加“日期”系列x轴
            Axis axisX = new Axis();//新建一个x轴
            List<AxisValue> valueListX = new ArrayList<>();//新建一个x轴的值列表
            int day = 0;
            for (int i = 0; i < numOfRealPoints; i++) {
                AxisValue valueX = new AxisValue(i);//这里的数字是float，作为坐标的数值
                day++;
                if (day > 30) {
                    day %= 30;
                }
                valueX.setLabel("5" + "/" + day);//将数值和文字标签绑定起来
                valueListX.add(valueX);//添加一个值
            }
            axisX.setValues(valueListX);//将列表设置到x轴上面
            Axis axisY = new Axis();//Y轴没有任何设定，就初始化
            Axis[] axisXY = {axisX, axisY};//把XY放到一起
            axesList.add(axisXY);//加入总的坐标轴列表
        }
        //“预测”
        for (int j = 0; j < numOfForecastLines; j++) {
            //增加“日期”系列x轴
            Axis axisX = new Axis();//新建一个x轴
            List<AxisValue> valueListX = new ArrayList<>();//新建一个x轴的值列表
            int day = 0;
            for (int i = 0; i < numOfForecastPoints; i++) {
                AxisValue valueX = new AxisValue(i);//这里的数字是float，作为坐标的数值
                day++;
                if (day > 30) {
                    day %= 30;
                }
                valueX.setLabel("6" + "/" + day);//将数值和文字标签绑定起来
                valueListX.add(valueX);//添加一个值
            }
            axisX.setValues(valueListX);//将列表设置到x轴上面
            Axis axisY = new Axis();//Y轴没有任何设定，就初始化
            Axis[] axisXY = {axisX, axisY};//把XY放到一起
            axesList.add(axisXY);//加入总的坐标轴列表
        }

    }

    private List<Axis[]> axesList = new ArrayList<>(); //所有坐标轴，里面是按照先“真实”后“预测”的顺序

    /**
     * 刷新图像
     *
     * @Description 刷新图像，包括绑定视图、坐标轴、显示位置、显示区域范围
     * @author lym
     * @version 2.0
     */
    private void draw() {
        Log.i(TAG, "draw 进入函数");

        List<Line> curLines = lines.subList(curLineIndex, curLineIndex + 1);//去除不需要的条数
        myLineData = new LineChartData(curLines);//设置为显示的条数
        myLineChartView.setLineChartData(myLineData);//设置图表控件
        myLineData.setAxisXBottom(axesList.get(curLineIndex)[0]); //设置X轴位置 下方
        myLineData.setAxisYLeft(axesList.get(curLineIndex)[1]); //设置Y轴位置 左边

        final Viewport MAX = new Viewport(myLineChartView.getMaximumViewport());//创建一个图表视图 大小为控件的最大大小
        final Viewport CUR = new Viewport(myLineChartView.getCurrentViewport());
        final Viewport fullViewport = new Viewport(MAX);
        final Viewport halfViewport = new Viewport(CUR);

        //TODO “调参师”
        if(isForecast){
            fullViewport.top = 300;
            fullViewport.bottom = -20;//最下面显示的y轴坐标值
            fullViewport.left = -1;//最左边显示的x轴坐标值
            fullViewport.right = numOfForecastPoints;
            halfViewport.top = fullViewport.top;
            halfViewport.bottom = fullViewport.bottom;//最下面显示的y轴坐标值
            halfViewport.left = fullViewport.left;//最左边显示的x轴坐标值
            halfViewport.right = 15;
        }
        else{
            fullViewport.top = 300;
            fullViewport.bottom = -20;//最下面显示的y轴坐标值
            fullViewport.left = -1;//最左边显示的x轴坐标值
            fullViewport.right = numOfRealPoints;
            halfViewport.top = fullViewport.top;
            halfViewport.bottom = fullViewport.bottom;//最下面显示的y轴坐标值
            halfViewport.left = fullViewport.left;//最左边显示的x轴坐标值
            halfViewport.right = 15;
        }

        myLineChartView.setMaximumViewport(fullViewport);   //给最大的视图设置 相当于原图
        myLineChartView.setCurrentViewport(halfViewport);   //给当前的视图设置 相当于当前展示的图
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
        draw();
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
            draw();
        } else {
            Log.i(TAG, "onCheckedChanged 开关状态：关闭");
            curLineIndex = 0;//因为只有第一个曲线是要预测的，关闭时就应该返回到第一个线的真实线
            draw();
        }
    }
}
