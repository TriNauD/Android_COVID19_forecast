package viewModel;

import android.arch.lifecycle.ViewModel;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;

public class LineViewModel extends ViewModel {
    //调试使用的日志标签
    private static final String TAG = "LineViewModel";

    //“真实线”的数量
    private int numOfRealLines = 4;

    //按照先“真实”后“预测”的顺序：
    //所有线数据
    private List<float[]> lineData = new ArrayList<>();
    //所有线
    private List<Line> lines = new ArrayList<>();
    //所有坐标轴
    private List<Axis[]> axesList = new ArrayList<>();
    //所有坐标轴的标签信息
    private List<List<String>> axisLableList = new ArrayList<>();

    //预测参数
    //是否进行控制
    public boolean hasControl = false;
    //控制开始时间
    public String startControlDate;
    //控制增长阶段的时间
    public int raiseLastTime;
    //控制强度
    int controlGrade;

    /**
     * 初始化图表相关
     * 初始化，包括数据、线、轴；数据先用随机数
     *
     * @author lym
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
            line.setPointRadius(1);//点的大小
            line.setHasLabelsOnlyForSelected(true);//点的标签在点击的时候显示
            line.setFilled(true);//下方填充
            line.setCubic(false);//不要曲线
            lines.add(line);
        }
        //轴
        //建立标签
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
        //绑定标签和轴
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
            //TODO step of y ? 是时候考虑y轴的步长问题了
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
                //Random random = new Random();
                linePoints[j] = 1200 - j * j;
            }
            lineData.add(linePoints);
        }
        //线
        for (int i = 0; i < numOfForecastLines; i++) {
            //绑定数据
            List<PointValue> tempArrayList = new ArrayList<>();//一条线的数据
            for (int p = 0; p < numOfForecastPoints; p++) {
                int x = p + numOfRealPoints;//预测点接在真实后面
                float y = lineData.get(i + numOfRealLines)[p];
                tempArrayList.add(new PointValue(x, y));//在真实线后面的是预测线
            }
            //设置样式
            Line line = new Line(tempArrayList);//根据值来创建一条线
            line.setColor(Color.rgb(255, 0, 0));//线的颜色
//            line.setPointColor(Color.rgb(255, 255, 255));//点的颜色 白色
            line.setPointColor(Color.rgb(255, 0, 0));//点的颜色 红色
            line.setPointRadius(2);//点的大小
            line.setHasLabelsOnlyForSelected(true);//点的标签在点击的时候显示
            line.setFilled(true);//下方填充
            line.setCubic(false);//不要曲线
            lines.add(line);
        }
        //轴
        //todo 预测的坐标轴应该是真实轴的延申
        //建立预测标签
        for (int i = 0; i < numOfForecastLines; i++) {
            //对于每一条预测线
            List<String> strings = new ArrayList<>();
            int day = 0;
            for (int j = 0; j < numOfForecastPoints; j++) {
                day++;
                if (day > 30) {
                    day %= 30;
                }
                //todo 需要接着真实线来做日期标签
                String s = (i + 6) + "/" + day;
                strings.add(s);
            }
            axisLableList.add(strings);
        }
        //绑定标签和轴
        for (int i = 0; i < numOfForecastLines; i++) {
            //每条线
            Axis axisX = new Axis();//新建一个x轴
            List<AxisValue> valueListX = new ArrayList<>();//新建一个x轴的值列表
            //每个点，包括前面真实和后面预测
            for (int p = 0; p < numOfRealPoints + numOfForecastPoints; p++) {
                AxisValue valueX = new AxisValue(p);//这里的数字是坐标的数值，比如第一个坐标就是0
                //将坐标的数值和对应的文字标签绑定起来
                //分为前（真实）后（预测）
                if (p < numOfRealPoints)
                    //前，是真实线0的标签
                    valueX.setLabel(axisLableList.get(0).get(p));
                else
                //后，是预测线i的标签，此时的点值应该剪掉前面的真实点总数
                {
                    int thisLineIndex = i + numOfRealLines;
                    int thisP = p - numOfRealPoints;
                    valueX.setLabel(axisLableList.get(thisLineIndex).get(thisP));
                }
                valueListX.add(valueX);//添加一个值
            }
            axisX.setValues(valueListX);//将列表设置到x轴上面
            Axis axisY = new Axis();//Y轴没有任何设定，就初始化
            Axis[] axisXY = {axisX, axisY};//把XY放到一起
            axesList.add(axisXY);//加入总的坐标轴列表
        }
    }

    //getter & setter
    public int getNumOfRealLines() {
        return numOfRealLines;
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Axis[]> getAxesList() {
        return axesList;
    }

}
