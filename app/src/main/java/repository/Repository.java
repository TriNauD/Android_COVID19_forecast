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
    private static float[][] xyReal = new float[4][120];

    /**
     * 网络
     */
    public static void web() {
        //真实线，一共4条，每条120个点
        for (int line = 0; line < 4; line++) {
            for (int i = 0; i < 120; i++) {
                //todo 传进来历史数据的x和y数组
                int x = i;//这里网络传进x值。也可以直接用序号，因为x默认就是 0, 1, 2...
                float y = new Random().nextInt(50) + i * 10;//这里网络传进y值
                xyReal[line][x] = y;
            }
        }
        //todo 预测线

        //todo 传进来坐标轴标签
        String[] date = new String[]{"1/1", "1/2", "1/3", "1/4"};

        //todo 传出去预测参数
        //这里的局部变量可以用于网络传输
        //是否进行控制
        boolean hasControl = Repository.hasControl;
        //控制开始时间
        String startControlDate = Repository.startControlDate;
        //控制增长阶段的时间
        int raiseLastTime = Repository.raiseLastTime;
        //控制强度
        int controlGrade = Repository.controlGrade;
    }

    public static void initReal() {
        for (int i = 0; i < numOfRealLines; ++i) {
            float[] linePoints = new float[numOfRealPoints];//一条线上面的点
            for (int j = 0; j < numOfRealPoints; ++j) {
                linePoints[j] = xyReal[i][j];
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
