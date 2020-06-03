package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Repository {
    //真实线数
    public static final int numOfRealLines = 4;
    //预测线数
    public static final int numOfForecastLines = 1;
    //“真实线”的节点数
    public static final int numOfRealPoints = 120;
    //“预测线”的节点数
    public static final int numOfForecastPoints = 15;


    //预测参数
    //是否进行控制
    private static boolean hasControl = false;
    //控制开始时间
    private static String startControlDate;
    //控制增长阶段的时间
    private static int raiseLastTime;
    //控制强度
    private static int controlGrade;

    //所有线数据
    private static List<float[]> lineData = new ArrayList<>();

    //网络传进来的数
    float[] x;
    float[] y;
    String[] date;

    public Repository() {
        x = new float[]{0, 1, 2, 3};
        y = new float[]{0, 100, 200, 300};
        date = new String[]{"1/1", "1/2", "1/3", "1/4"};
        initReal();
        initForecast();
    }

    public static void initReal() {
        for (int i = 0; i < numOfRealLines; ++i) {
            float[] linePoints = new float[numOfRealPoints];//一条线上面的点
            for (int j = 0; j < numOfRealPoints; ++j) {
                Random random = new Random();
                linePoints[j] = random.nextInt(50) + j * 10;
            }
            if (lineData.size() < numOfRealLines) {
                //如果是空的就初始化
                lineData.add(linePoints);
            } else {
                //如果不是空的就应该更新
                lineData.set(i, linePoints);
            }
        }
    }

    public static void initForecast() {
        for (int i = 0; i < numOfForecastLines; ++i) {
            float[] linePoints = new float[numOfForecastPoints];//一条线上面的点
            for (int j = 0; j < numOfForecastPoints; ++j) {
                if (hasControl) {
                    //如果进行控制
                    linePoints[j] = 1200 - j * j;
                } else {
                    //群体免疫
                    float x = j * 1000;
                    linePoints[j] = 1200 + (float) Math.sqrt(x);
                }
            }
            //判定是否要刷新
            int size = lineData.size();
            if (size < numOfRealLines + numOfForecastLines) {
                //如果线组里面还没有预测线，就新添加
                lineData.add(linePoints);
            } else {
                //如果已经有预测线，就更新
                lineData.set(i + numOfRealLines, linePoints);
            }
        }
    }


    //getter & setter

    public static int getNumOfRealLines() {
        return numOfRealLines;
    }

    public static int getNumOfForecastLines() {
        return numOfForecastLines;
    }

    public static int getNumOfRealPoints() {
        return numOfRealPoints;
    }

    public static int getNumOfForecastPoints() {
        return numOfForecastPoints;
    }

    public static boolean isHasControl() {
        return hasControl;
    }

    public static void setHasControl(boolean hasControl) {
        Repository.hasControl = hasControl;
    }

    public static String getStartControlDate() {
        return startControlDate;
    }

    public static void setStartControlDate(String startControlDate) {
        Repository.startControlDate = startControlDate;
    }

    public static int getRaiseLastTime() {
        return raiseLastTime;
    }

    public static void setRaiseLastTime(int raiseLastTime) {
        Repository.raiseLastTime = raiseLastTime;
    }

    public static int getControlGrade() {
        return controlGrade;
    }

    public static void setControlGrade(int controlGrade) {
        Repository.controlGrade = controlGrade;
    }

    public static List<float[]> getLineData() {
        return lineData;
    }

    public static void setLineData(List<float[]> lineData) {
        Repository.lineData = lineData;
    }

}
