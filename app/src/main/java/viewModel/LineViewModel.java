package viewModel;

import android.arch.lifecycle.ViewModel;
import android.graphics.Color;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;

public class LineViewModel extends ViewModel {
    //调试使用的日志标签
    private static final String TAG = "LineViewModel";

    //按照先“真实”后“预测”的顺序：
    //所有线
    private List<Line> lines = new ArrayList<>();
    //所有坐标轴
    private List<Axis[]> axesList = new ArrayList<>();


    /**
     * 初始化预测的图表
     * 包括点、线、轴；
     * 数据采用默认的预测参数来计算。
     *
     * @author lym
     */
    public void initForecastChart() {
        Log.i(TAG, "initForecastChart 进入函数");
        WebConnect.initForecast();
        //线
        for (int i = 0; i < WebConnect.getNumOfForecastLines(); i++) {
            //绑定数据
            List<PointValue> tempArrayList = new ArrayList<>();//一条线的数据
            for (int p = 0; p < WebConnect.getNumOfForecastPoints(); p++) {
                int x = p + WebConnect.getNumOfRealPoints();//预测点接在真实后面
                float y = WebConnect.getLineDataList().get(i + WebConnect.getNumOfRealLines())[p];
                tempArrayList.add(new PointValue(x, y));//在真实线后面的是预测线
            }
            //设置样式
            Line line = new Line(tempArrayList);//根据值来创建一条线
            line.setColor(Color.rgb(126, 185, 236));//线的颜色 蓝色
//            line.setPointColor(Color.rgb(255, 255, 255));//点的颜色 白色
            line.setPointColor(Color.rgb(126, 185, 236));//点的颜色 蓝色
            line.setPointRadius(2);//点的大小
            line.setHasLabelsOnlyForSelected(true);//点的标签在点击的时候显示
            line.setFilled(true);//下方填充
            line.setCubic(false);//不要曲线
            if (lines.size() >= WebConnect.getNumOfRealLines() + WebConnect.getNumOfForecastLines()) {
                //如果已经有这条线了
                lines.set(i + WebConnect.getNumOfRealLines(), line);
            } else {
                lines.add(line);
            }
        }
        //轴
        WebConnect.initForecastAxis();
        //绑定标签和轴
        for (int i = 0; i < WebConnect.getNumOfForecastLines(); i++) {
            //每条线
            Axis axisX = new Axis();//新建一个x轴
            List<AxisValue> valueListX = new ArrayList<>();//新建一个x轴的值列表
            //每个点，包括前面真实和后面预测
            for (int p = 0; p < WebConnect.getNumOfRealPoints() + WebConnect.getNumOfForecastPoints(); p++) {
                AxisValue valueX = new AxisValue(p);//这里的数字是坐标的数值，比如第一个坐标就是0
                //将坐标的数值和对应的文字标签绑定起来
                //分为前（真实）后（预测）
                if (p < WebConnect.getNumOfRealPoints())
                    //前，是真实线0的标签
                    valueX.setLabel(WebConnect.getAxisLableList().get(0).get(p));
                else
                //后，是预测线i的标签，此时的点值应该剪掉前面的真实点总数
                {
                    int thisLineIndex = i + WebConnect.getNumOfRealLines();
                    int thisP = p - WebConnect.getNumOfRealPoints();
                    valueX.setLabel(WebConnect.getAxisLableList().get(thisLineIndex).get(thisP));
                }
                valueListX.add(valueX);//添加一个值
            }
            axisX.setValues(valueListX);//将列表设置到x轴上面
            Axis axisY = new Axis();//Y轴没有任何设定，就初始化
            Axis[] axisXY = {axisX, axisY};//把XY放到一起
            axesList.add(axisXY);//加入总的坐标轴列表
        }
    }

    /**
     * 初始化真实的图表
     * 包括点、线、轴；
     * 数据先用随机数。
     *
     * @author lym
     */
    public void initRealChart() {
        Log.i(TAG, "initRealChart 进入函数");

        //调用初始化函数
        WebConnect.initReal();

        //获取线条和点数量
        int numOflines = WebConnect.getNumOfRealLines();
        int numOfPoints = WebConnect.getNumOfRealPoints();

        //线
        for (int i = 0; i < numOflines; i++) {

            //获取仓库数据
            List<PointValue> tempArrayList = new ArrayList<>();//一条线的数据
            for (int j = 0; j < numOfPoints; j++) {
//                Log.d(TAG, "initRealChart：拿到仓库的linedata：" + WebConnect.getLineDataList().get(i)[j]);
                tempArrayList.add(new PointValue(j, WebConnect.getLineDataList().get(i)[j]));
            }

            //创建一条线
            Line line = new Line(tempArrayList);

            //调整线样式
            //颜色
            if (i == 0) {
                line.setColor(Color.rgb(255, 0, 0));//第一条线为红色
            } else if (i == 1) {
                line.setColor(Color.rgb(255, 140, 0));//第二条线橙色
            } else if (i == 2) {
                line.setColor(Color.rgb(50, 205, 50));//第三条线绿色
            } else {
                line.setColor(Color.rgb(126, 185, 236));//第四条线蓝色
            }
            //line.setPointColor(Color.rgb(255,255,255));//点的颜色 这个是白色
            line.setPointRadius(1);//点的大小
            line.setHasLabelsOnlyForSelected(true);//点的标签在点击的时候显示
            line.setFilled(true);//下方填充
            line.setCubic(false);//不要曲线

            //放到线组
            //刷新和初始化
            if (lines.size() < numOflines) {
                //如果是空的就初始化
                lines.add(line);
            } else {
                //如果不是空的就应该更新
                lines.set(i, line);
            }
        }

        //---------------- 轴 ---------------

        //调用初始化轴
        WebConnect.initRealAxis();

        //绑定标签和轴
        for (int i = 0; i < numOflines; i++) {
            //新建一个x轴的值列表
            List<AxisValue> valueListX = new ArrayList<>();
            //赋值日期数据
            //每个点
            for (int j = 0; j < numOfPoints; j++) {
                //一个点的轴的坐标显示值
                AxisValue valueX = new AxisValue(j);//这里的数字j是坐标的数值，比如第一个坐标就是0
                //将坐标的数值和对应的文字标签绑定起来
                valueX.setLabel(WebConnect.getAxisLableList().get(i).get(j));
                //添加一个值
                valueListX.add(valueX);
            }

            //做轴
            //x轴
            Axis axisX = new Axis();
            //将标签列表设置到x轴上面
            axisX.setValues(valueListX);

            //TODO step of y ? 是时候考虑y轴的步长问题了
            //y轴
            Axis axisY = new Axis();//Y轴没有任何设定，就初始化


            //把XY放到一起
            Axis[] axisXY = {axisX, axisY};
            //加入总的坐标轴列表
            axesList.add(axisXY);
        }
    }

    //getter & setter
    public int getNumOfRealLines() {
        return WebConnect.getNumOfRealLines();
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Axis[]> getAxesList() {
        return axesList;
    }

    public static boolean isHasControl() {
        return WebConnect.isHasControl();
    }

    public static void setHasControl(boolean hasControl) {
        WebConnect.setHasControl(hasControl);
    }

    public int getNumOfRealPoint() {
        return WebConnect.getNumOfRealPoints() + WebConnect.getNumOfForecastPoints() - 1;
    }

}
