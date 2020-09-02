package viewModel;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import domain.Alltime_province;
import domain.Alltime_world;
import okhttp3.OkHttpClient;
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
    private static int numOfRealPoints = 9999;
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
    private static List<float[]> lineDataList = new ArrayList<>();

    //网络传进来的数
    private static float[][] xyReal = new float[4][9999];


    //后端用的国家名
    private static String name;
    //后端用的“是否为国家”
    private static boolean isNation;

    //拿到的一个地区的列表，里面是所有时间的数据
    private static List<Alltime_province> provinceList = new ArrayList<>();
    //世界的列表
    private static List<Alltime_world> nationList = new ArrayList<>();
    //第一天的现存确诊
    private static Integer a;

    //数据是否获取完毕 用于通知前端 已加载
    public static boolean isDataGotten = false;

    /**
     * 从后端获取省份疫情数据
     *
     * @param name 传给后端的国家名
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
                    //网络拿到的一个地区的列表，里面是所有时间的数据
                    //赋值列表
                    provinceList = province;
                    //一天的所有数据
                    Alltime_province oneDay = provinceList.get(0);
                    //一天的现存确诊
                    a = oneDay.getPresent_confirm();
                    Log.i(TAG, "onResponse: 第一天的的现存确诊： " + a);
                    //真实线的数量，要根据传进来的数量啦
                    numOfRealPoints = provinceList.size();
                    Log.i(TAG, "onResponse: 真实线的节点数量：" + numOfRealPoints);

                    //真实线，一共4条
                    for (int i = 0; i < numOfRealPoints; i++) {
                        //拿到一天的4种数据
                        Alltime_province oneDay1 = provinceList.get(i);
                        //因为后面函数不一样所以没法for循环
                        //现存确诊
                        xyReal[0][i] = oneDay1.getPresent_confirm();
                        //累计确诊
                        xyReal[1][i] = oneDay1.getTotal_confirm();
                        //累计治愈
                        xyReal[2][i] = oneDay1.getTotal_heal();
                        //累计死亡
                        xyReal[3][i] = oneDay1.getTotal_dead();
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Alltime_province>> call, Throwable t) {
                Log.i(TAG, "onFailure..." + t.toString());
            }
        });

    }


    /**
     * 从后端获取国家疫情数据
     *
     * @param name 传给后端的国家名
     * @author qy
     */
    public static void getWorld(String name) {
        Log.i(TAG, "进入getWorld");

        //进行获取
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        //解决超时问题
        OkHttpClient client = new OkHttpClient.Builder().
                connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(60, TimeUnit.SECONDS).
                writeTimeout(60, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://39.96.80.224:8080")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        API api = retrofit.create(API.class);
        Call<List<Alltime_world>> task = api.getWorld(name);
        task.enqueue(new Callback<List<Alltime_world>>() {
            @Override
            public void onResponse(Call<List<Alltime_world>> call, Response<List<Alltime_world>> response) {
                Log.i(TAG, "onResponse --> " + response.code());
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    List<Alltime_world> world = response.body();
                    //网络拿到的一个地区的列表，里面是所有时间的数据
                    //赋值列表
                    nationList = world;
                    //一天的所有数据
                    Alltime_world oneDay = nationList.get(0);
                    //一天的现存确诊
                    a = oneDay.getPresent_confirm();
                    Log.i(TAG, "onResponse: 第一天的的现存确诊： " + a);
                    //真实线的数量，要根据传进来的数量啦
                    numOfRealPoints = nationList.size();
                    Log.i(TAG, "onResponse: 真实线的节点数量：" + numOfRealPoints);

                    //真实线，一共4条
                    for (int i = 0; i < numOfRealPoints; i++) {
                        //拿到一天的4种数据
                        Alltime_world oneDay1 = nationList.get(i);
                        //因为后面函数不一样所以没法for循环
                        //现存确诊
                        xyReal[0][i] = oneDay1.getPresent_confirm();
                        //累计确诊
                        xyReal[1][i] = oneDay1.getTotal_confirm();
                        //累计治愈
                        xyReal[2][i] = oneDay1.getTotal_heal();
                        //累计死亡
                        xyReal[3][i] = oneDay1.getTotal_dead();
                    }
                    //已经获取完毕
                    isDataGotten = true;
                }
            }

            @Override
            public void onFailure(Call<List<Alltime_world>> call, Throwable t) {
                Log.i(TAG, "onFailure..." + t.toString());
            }
        });

    }

    /**
     * 从后端获取预测数据
     *
     * @param name 传给后端的地区名
     * @author qy
     */
    public static void getPredict(String name) {
        Log.i(TAG, "进入getPredict");

        //进行获取
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://39.96.80.224:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        API api = retrofit.create(API.class);
        Call<List<Integer>> task = api.getPredict(name, isNation, hasControl, startControlDate, raiseLastTime, controlGrade);
        task.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                Log.i(TAG, "onResponse --> " + response.code());
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    List<Integer> predict = response.body();
                    float[] linePoints = new float[5];//一条线上面的点
                    for (int j = 0; j < 5; ++j) {
                        linePoints[j] = predict.get(j);
                    }
                    //判定是否要刷新
                    int size = lineDataList.size();
                    if (size < numOfRealLines + numOfForecastLines) {
                        //如果线组里面还没有预测线，就新添加
                        lineDataList.add(linePoints);
                    } else {
                        //如果已经有预测线，就更新
                        lineDataList.set(numOfRealLines, linePoints);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Log.i(TAG, "onFailure..." + t.toString());
            }
        });

    }


    /**
     * 给前端用的 初始化真实线
     * 实际上是用xyReal给lineData赋值
     *
     * @author lym
     */
    public static void initReal() {
        Log.i(TAG, "进入initReal");

        for (int i = 0; i < numOfRealLines; ++i) {
            float[] linePoints = new float[numOfRealPoints];//一条线上面的点
            for (int j = 0; j < numOfRealPoints; ++j) {
                //debug TAG
                Log.d(TAG, "initReal：xyReal[i][j]: " + xyReal[i][j]);
                linePoints[j] = xyReal[i][j];
            }
            if (lineDataList.size() < numOfRealLines) {
                //如果是空的就初始化
                lineDataList.add(linePoints);
            } else {
                //如果不是空的就应该更新
                lineDataList.set(i, linePoints);
            }
        }
    }

    /**
     * 给前端用的 初始化预测线
     *
     * @author lym
     */
    public static void initForecast() {
        for (int i = 0; i < numOfForecastLines; ++i) {
            float[] linePoints = new float[numOfForecastPoints];//一条线上面的点
            /*for (int j = 0; j < numOfForecastPoints; ++j) {
                if (hasControl) {
                    //如果进行控制
                    linePoints[j] = 1150000 - j * j * 1000;
                } else {
                    //群体免疫
                    float x = j * 1000;
                    linePoints[j] = 1150000 + (float) Math.sqrt(x) * 1000;
                }
            }*/
            //判定是否要刷新
//            int size = lineData.size();
            if (lineDataList.size() < numOfRealLines + numOfForecastLines) {
                //如果线组里面还没有预测线，就新添加
                lineDataList.add(linePoints);
            } else {
                //如果已经有预测线，就更新
                lineDataList.set(i + numOfRealLines, linePoints);
            }
        }
    }


    //所有坐标轴的标签信息
    private static List<List<String>> axisLableList = new ArrayList<>();

    /**
     * 给前端用的 初始化真实坐标轴
     *
     * @author lym
     */
    public static void initRealAxis() {
        //真实
        //建立标签
        for (int i = 0; i < numOfRealLines; i++) {
            List<String> strings = new ArrayList<>();
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
    }

    /**
     * 给前端用的 初始化预测坐标轴
     *
     * @author lym
     */
    public static void initForecastAxis() {
        //预测
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
    }

    //----------------------getter & setter---------------------------------------------------------

    public static List<List<String>> getAxisLableList() {
        return axisLableList;
    }

    public static void setAxisLableList(List<List<String>> axisLableList) {
        WebConnect.axisLableList = axisLableList;
    }

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

    public static List<float[]> getLineDataList() {
        return lineDataList;
    }

    public static void setLineDataList(List<float[]> lineDataList) {
        WebConnect.lineDataList = lineDataList;
    }

}
