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
    public boolean hasControl = false;
    //控制开始时间
    public String startControlDate;
    //控制增长阶段的时间
    public int raiseLastTime;
    //控制强度
    public int controlGrade;

    //所有线数据
    public List<float[]> lineData = new ArrayList<>();

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

    public void initReal() {
        for (int i = 0; i < numOfRealLines; ++i) {
            float[] linePoints = new float[numOfRealPoints];//一条线上面的点
            for (int j = 0; j < numOfRealPoints; ++j) {
                Random random = new Random();
                linePoints[j] = random.nextInt(50) + j * 10;
            }
            lineData.add(linePoints);
        }
    }

    public void initForecast(){
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
            lineData.add(linePoints);
        }
    }
}
