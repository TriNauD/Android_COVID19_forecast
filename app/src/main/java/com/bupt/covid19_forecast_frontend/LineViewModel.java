package com.bupt.covid19_forecast_frontend;

import android.arch.lifecycle.ViewModel;
import android.graphics.Color;
import android.util.Log;

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

public class LineViewModel extends ViewModel {
    private static final String TAG = "LineViewModel";



    /*————————————绘图相关————————————*/

    public int getNumOfRealLines() {
        return numOfRealLines;
    }

    public void setNumOfRealLines(int numOfRealLines) {
        this.numOfRealLines = numOfRealLines;
    }

    private int numOfRealLines = 4;//“真实线”的数量
    private List<float[]> lineData = new ArrayList<>();//所有线数据，里面是按照先“真实”后“预测”的顺序

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    private List<Line> lines = new ArrayList<>(); //所有线，里面是按照先“真实”后“预测”的顺序

    public List<Axis[]> getAxesList() {
        return axesList;
    }

    public void setAxesList(List<Axis[]> axesList) {
        this.axesList = axesList;
    }

    private List<Axis[]> axesList = new ArrayList<>(); //所有坐标轴，里面是按照先“真实”后“预测”的顺序
    private List<List<String>> axisLableList = new ArrayList<>(); //所有坐标轴的标签信息，里面是按照先“真实”后“预测”的顺序

    public int getCurLineIndex() {
        return curLineIndex;
    }

    public void setCurLineIndex(int curLineIndex) {
        this.curLineIndex = curLineIndex;
    }

    private int curLineIndex = 0;//当前显示的线是几号

    public boolean isForecast() {
        return isForecast;
    }

    public void setForecast(boolean forecast) {
        isForecast = forecast;
    }

    private boolean isForecast = false;//是否处于预测状态

    /**
     * 初始化图表相关
     *
     * @Description 初始化，包括数据、线、轴；数据先用随机数
     * @author lym
     * @version 2.3
     */
    public void initChart() {
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
        //“真实线”的节点数
        int numOfRealPoints = 120;
        for (int i = 0; i < numOfRealLines; ++i) {
            float[] linePoints = new float[numOfRealPoints];//一条线上面的点
            for (int j = 0; j < numOfRealPoints; ++j) {
                Random random = new Random();
                linePoints[j] = random.nextInt(50) + j * 10;
            }
            lineData.add(linePoints);
        }
        //线
        for (int i = 0; i < numOfRealLines; i++) {
            List<PointValue> tempArrayList = new ArrayList<>();//一条线的数据
            for (int j = 0; j < numOfRealPoints; j++) {
                tempArrayList.add(new PointValue(j, lineData.get(i)[j]));
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
        //“预测线”的数量
        int numOfForecastLines = 1;
        //“预测线”的节点数
        int numOfForecastPoints = 15;
        for (int i = 0; i < numOfForecastLines; ++i) {
            float[] linePoints = new float[numOfForecastPoints];//一条线上面的点
            for (int j = 0; j < numOfForecastPoints; ++j) {
                Random random = new Random();
                linePoints[j] = numOfForecastPoints * numOfForecastPoints - j * j;
            }
            lineData.add(linePoints);
        }
        //线
        for (int i = 0; i < numOfForecastLines; i++) {
            List<PointValue> tempArrayList = new ArrayList<>();//一条线的数据
            for (int j = 0; j < numOfForecastPoints; j++) {
                tempArrayList.add(new PointValue(j, lineData.get(i + numOfRealLines)[j]));//在真实线后面的是预测线
            }
            Line line = new Line(tempArrayList);//根据值来创建一条线
            line.setColor(Color.rgb(255, 0, 0));//线的颜色
            line.setPointColor(Color.rgb(255, 255, 255));//点的颜色 这个是白色
            line.setPointRadius(3);//点的大小
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


}
