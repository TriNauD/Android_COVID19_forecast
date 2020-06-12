package viewModel;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import domain.Alltime_province;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebConnect {
    //调试使用的日志标签
    private static final String TAG = "WebConnect";

    //真实线数
    private static final int numOfRealLines = 4;
    //预测线数
    private static final int numOfForecastLines = 1;
    //“真实线”的节点数
    private static int numOfRealPoints = 120;
    //“预测线”的节点数
    private static final int numOfForecastPoints = 15;


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


    //后端用的国家名
    private static String name;

    //返回值
    private static List<Alltime_province> provinceList = new ArrayList<>();

    //第一天的现存确诊
    private static Integer a;

    /**
     * 从后端获取省份疫情数据
     *
     * @param name 传给后端的国家名
     * @return 一个List，里面的数据格式为Alltime_provice
     * @author qy
     */
    public static void getProvince(String name) {
        Log.i(TAG, "进入getProvince");

        //进行获取
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://39.96.80.224:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        API api = retrofit.create(API.class);
        Call<List<Alltime_province>> province_task = api.getProvince(name);
        province_task.enqueue(new Callback<List<Alltime_province>>() {
            @Override
            public void onResponse(Call<List<Alltime_province>> call, Response<List<Alltime_province>> response) {
                Log.i(TAG, "onResponse --> " + response.code());
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    List<Alltime_province> province = response.body();
                    //赋值列表
                    provinceList = province;
                    //一天的所有数据
                    Alltime_province oneDay = provinceList.get(0);
                    //一天的现存确诊
                    a = oneDay.getPresent_confirm();
                    Log.i(TAG, "第一天的的现存确诊： " + a);
                }
            }

            @Override
            public void onFailure(Call<List<Alltime_province>> call, Throwable t) {
                Log.i(TAG, "onFailure..." + t.toString());
            }
        });

    }


    /**
     * 网络
     */
    public static void web() {
        //尝试传一个地区名字
        name = "湖北";

        //获取数据
        getProvince(name);

        //log一下
        Log.i(TAG, "湖北的现存确诊1： " + a);

        //假装 拿到的一个地区的列表，里面是所有时间的数据
        List<Alltime_province> provinceList = new ArrayList<>();

        //假装 网络 传进来 地区列表
        //todo 其实还是在随机数造假
        //4种数据都赋值
        //假装有120个点
        int tempNumOfPoints = 120;
        for (int i = 0; i < tempNumOfPoints; i++) {
            //一天的数据
            Alltime_province oneDay = new Alltime_province();

            //随机数
            int tempInt;
            //4个不一样的，要重新随机
            tempInt = new Random().nextInt(50) + i * 10;
            oneDay.setPresent_confirm(tempInt);
            tempInt = new Random().nextInt(50) + i * 10;
            oneDay.setTotal_confirm(tempInt);
            tempInt = new Random().nextInt(50) + i * 10;
            oneDay.setTotal_heal(tempInt);
            tempInt = new Random().nextInt(50) + i * 10;
            oneDay.setTotal_dead(tempInt);

            //加入到总的列表中去
            provinceList.add(oneDay);
        }

        //真实线的数量，要根据传进来的数量啦
        numOfRealPoints = provinceList.size();

        //真实线，一共4条
        for (int i = 0; i < provinceList.size(); i++) {
            //拿到一天的4种数据
            Alltime_province oneDay = provinceList.get(i);
            //因为后面函数不一样所以没法for循环
            //现存确诊
            xyReal[0][i] = oneDay.getPresent_confirm();
            //累计确诊
            xyReal[1][i] = oneDay.getTotal_confirm();
            //累计治愈
            xyReal[2][i] = oneDay.getTotal_heal();
            //累计死亡
            xyReal[3][i] = oneDay.getTotal_dead();
        }

        //todo 预测线

        //todo 传进来坐标轴标签
        String[] date = new String[]{"1/1", "1/2", "1/3", "1/4"};

        //todo 传出去预测参数
        //这里的局部变量可以用于网络传输
        //是否进行控制
        boolean hasControl = WebConnect.hasControl;
        //控制开始时间
        String startControlDate = WebConnect.startControlDate;
        //控制增长阶段的时间
        int raiseLastTime = WebConnect.raiseLastTime;
        //控制强度
        int controlGrade = WebConnect.controlGrade;
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

    public static List<List<String>> getAxisLableList() {
        return axisLableList;
    }

    public static void setAxisLableList(List<List<String>> axisLableList) {
        WebConnect.axisLableList = axisLableList;
    }

    //所有坐标轴的标签信息
    private static List<List<String>> axisLableList = new ArrayList<>();

    /**
     * 初始化真实坐标轴
     *
     * @author lym
     */
    public static void initRealAxis() {
        //真实
        //建立标签
        for (int i = 0; i < WebConnect.getNumOfRealLines(); i++) {
            List<String> strings = new ArrayList<>(WebConnect.getNumOfRealPoints());
            int day = 0;
            for (int j = 0; j < WebConnect.getNumOfRealPoints(); j++) {
                day++;
                if (day > 30) {
                    day %= 30;
                }
                String s = (i + 1) + "/" + day;
                strings.add(s);
            }
            axisLableList.add(strings);
        }
    }

    /**
     * 初始化预测坐标轴
     *
     * @author lym
     */
    public static void initForecastAxis() {
        //预测
        //todo 预测的坐标轴应该是真实轴的延申
        //建立预测标签
        for (int i = 0; i < WebConnect.getNumOfForecastLines(); i++) {
            //对于每一条预测线
            List<String> strings = new ArrayList<>();
            int day = 0;
            for (int j = 0; j < WebConnect.getNumOfForecastPoints(); j++) {
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
        WebConnect.hasControl = hasControl;
    }

    public static String getStartControlDate() {
        return startControlDate;
    }

    public static void setStartControlDate(String startControlDate) {
        WebConnect.startControlDate = startControlDate;
    }

    public static int getRaiseLastTime() {
        return raiseLastTime;
    }

    public static void setRaiseLastTime(int raiseLastTime) {
        WebConnect.raiseLastTime = raiseLastTime;
    }

    public static int getControlGrade() {
        return controlGrade;
    }

    public static void setControlGrade(int controlGrade) {
        WebConnect.controlGrade = controlGrade;
    }

    public static List<float[]> getLineData() {
        return lineData;
    }

    public static void setLineData(List<float[]> lineData) {
        WebConnect.lineData = lineData;
    }

}
