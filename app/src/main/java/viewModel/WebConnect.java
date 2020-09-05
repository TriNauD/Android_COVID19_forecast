package viewModel;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    private static int numOfForecastPoints = 333;


    //预测参数
    //是否在国内，true表示省份，false表示国家
    private static Boolean isNation = false;
    //是否进行控制
    private static Boolean hasControl = true;
    //控制开始时间
    private static String startControlDate = "2020-01-01";
    //控制增长阶段的时间
    private static int raiseLastTime = 7;
    //控制强度
    private static int controlGrade = 1;

    //所有线数据
    private static List<float[]> lineDataList = new ArrayList<>();

    //网络传进来的
    //真实数据
    private static float[][] xyReal = new float[numOfRealLines][numOfRealPoints];
    //预测数据
    private static float[] xyPredict = new float[numOfForecastPoints];
    //真实日期标签
    private static String[] xRealLabel = new String[numOfRealPoints];
    //预测日期标签
    private static String[] xPredictLabel = new String[numOfForecastPoints];

    //拿到的一个地区的列表，里面是所有时间的数据
    private static List<Alltime_province> provinceList = new ArrayList<>();
    //世界的列表
    private static List<Alltime_world> nationList = new ArrayList<>();

    //某一天的4个数
    private static Integer[] oneDayFourNum = new Integer[4];

    //数据是否获取完毕 用于通知前端 已加载
    public static boolean isGetFinished = false;
    public static boolean isGetSuccess = false;

    /**
     * 从后端获取省份疫情数据
     *
     * @param name 传给后端的国家名
     * @author qy, lym
     */
    public static void getProvince(String name) {
        Log.i(TAG, "获取省份：" + name);

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
        Call<List<Alltime_province>> task = api.getProvince(name);
        task.enqueue(new Callback<List<Alltime_province>>() {
            @Override
            public void onResponse(Call<List<Alltime_province>> call, Response<List<Alltime_province>> response) {
                Log.i(TAG, "省份onResponse --> " + response.code());
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    List<Alltime_province> province = response.body();
                    //网络拿到的一个地区的列表，里面是所有时间的数据
                    //赋值列表
                    provinceList = province;

                    if (provinceList.size() <= 0) {
                        //已经获取完毕
                        isGetFinished = true;
                        isGetSuccess = false;
                        Log.i(TAG, "isGetFinished省份失败" + isGetFinished);
                        Log.i(TAG, "isGetSuccess省份失败" + isGetSuccess);
                        return;
                    }
                    //最后一天的所有数据
                    Alltime_province lastDay = provinceList.get(provinceList.size() - 1);

                    //日期
                    Date lastDate = lastDay.getDate();
                    //log一下
                    String dateStr = lastDate.toString();
                    Log.i(TAG, "onResponse: 最后一天日期是： " +
                            dateStr.substring(0, 4) + " 年 " +
                            dateStr.substring(5, 7).replaceFirst("0", "") + " 月 " +
                            dateStr.substring(8, 10).replaceFirst("0", "") + " 日 ");
                    //分离月/日
                    String month = dateStr.substring(5, 7).replaceFirst("0", "");
                    String day = dateStr.substring(8, 10).replaceFirst("0", "");
                    //x轴用的标签
                    String xDateString = month + "/" + day;
                    Log.i(TAG, "onResponse: x轴显示日期： " + xDateString);

                    Date tempDate = lastDate;
                    for (int i = 0; i < numOfForecastPoints; i++) {
                        //加一天
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(tempDate);
                        calendar.add(Calendar.DATE, 1); //把日期往后增加一天,正数往后推,负数往前移动
                        tempDate = calendar.getTime();//滚雪球++
                        //log一下
                        // 获得年份
                        int predictYear = calendar.get(Calendar.YEAR);
                        // 获得月份
                        int predictMonth = calendar.get(Calendar.MONTH) + 1;
                        // 获得日期
                        int predictDate = calendar.get(Calendar.DATE);
//                        Log.i(TAG, "onResponse: 预测的第" + i + "天日期是： " + predictYear + " 年 " + predictMonth + " 月 " + predictDate + " 日 ");
                        String xPredictDateString = predictMonth + "/" + predictDate;
//                        Log.i(TAG, "onResponse: x轴显示预测日期： " + xPredictDateString);
                        xPredictLabel[i] = xPredictDateString;
                    }

                    //最后一天的四个数
                    Integer oneDayPresent = lastDay.getPresent_confirm();
                    Log.i(TAG, "onResponse: 最后一天的的现存确诊： " + oneDayPresent);
                    oneDayFourNum[0] = oneDayPresent;
                    oneDayFourNum[1] = lastDay.getTotal_confirm();
                    oneDayFourNum[2] = lastDay.getTotal_heal();
                    oneDayFourNum[3] = lastDay.getTotal_dead();
                    Log.i(TAG, "onResponse: 最后一天的的累计死亡： " + oneDayFourNum[3]);

                    //真实线的节点数量，要根据传进来的数量啦
                    numOfRealPoints = provinceList.size();
                    Log.i(TAG, "onResponse: 省份真实线的节点数量：" + numOfRealPoints);

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
                        //x轴标签
                        String dateStr1 = oneDay1.getDate().toString();
                        //分离月/日
                        String month1 = dateStr1.substring(5, 7).replaceFirst("0", "");
                        String day1 = dateStr1.substring(8, 10).replaceFirst("0", "");
                        //x轴用的标签
                        String xDateString1 = month1 + "/" + day1;
                        xRealLabel[i] = xDateString1;
                    }

                }

                //已经获取完毕
                isGetFinished = true;
                isGetSuccess = true;
                Log.i(TAG, "isGetFinished省份成功" + isGetFinished);
                Log.i(TAG, "isGetSuccess省份成功" + isGetSuccess);
            }

            @Override
            public void onFailure(Call<List<Alltime_province>> call, Throwable t) {
                Log.i(TAG, "省份onFailure..." + t.toString());
                //已经获取完毕
                isGetFinished = true;
                isGetSuccess = false;
                Log.i(TAG, "isGetFinished省份失败" + isGetFinished);
                Log.i(TAG, "isGetSuccess省份失败" + isGetSuccess);

            }
        });

    }

    /**
     * 从后端获取国家疫情数据
     *
     * @param name 传给后端的国家名
     * @author qy, lym
     */
    public static void getWorld(String name) {
        Log.i(TAG, "获取世界：" + name);

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
                Log.i(TAG, "世界onResponse --> " + response.code());
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    List<Alltime_world> world = response.body();
                    //网络拿到的一个地区的列表，里面是所有时间的数据
                    //赋值列表
                    nationList = world;

                    if (nationList.size() <= 0) {
                        //已经获取完毕
                        isGetFinished = true;
                        isGetSuccess = false;
                        Log.i(TAG, "isGetFinished世界失败" + isGetFinished);
                        Log.i(TAG, "isGetSuccess世界失败" + isGetSuccess);
                        return;
                    }
                    //最后一天的所有数据
                    Alltime_world lastDay = nationList.get(nationList.size() - 1);

                    //日期
                    Date lastDate = lastDay.getDate();
                    //log一下
                    String dateStr = lastDate.toString();
                    Log.i(TAG, "onResponse: 最后一天日期是： " +
                            dateStr.substring(0, 4) + " 年 " +
                            dateStr.substring(5, 7).replaceFirst("0", "") + " 月 " +
                            dateStr.substring(8, 10).replaceFirst("0", "") + " 日 ");
                    //分离月/日
                    String month = dateStr.substring(5, 7).replaceFirst("0", "");
                    String day = dateStr.substring(8, 10).replaceFirst("0", "");
                    //x轴用的标签
                    String xDateString = month + "/" + day;
                    Log.i(TAG, "onResponse: x轴显示日期： " + xDateString);

                    Date tempDate = lastDate;
                    for (int i = 0; i < numOfForecastPoints; i++) {
                        //加一天
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(tempDate);
                        calendar.add(Calendar.DATE, 1); //把日期往后增加一天,正数往后推,负数往前移动
                        tempDate = calendar.getTime();//滚雪球++
                        //log一下
                        // 获得年份
                        int predictYear = calendar.get(Calendar.YEAR);
                        // 获得月份
                        int predictMonth = calendar.get(Calendar.MONTH) + 1;
                        // 获得日期
                        int predictDate = calendar.get(Calendar.DATE);
//                        Log.i(TAG, "onResponse: 预测的第" + i + "天日期是： " + predictYear + " 年 " + predictMonth + " 月 " + predictDate + " 日 ");
                        String xPredictDateString = predictMonth + "/" + predictDate;
//                        Log.i(TAG, "onResponse: x轴显示预测日期： " + xPredictDateString);
                        xPredictLabel[i] = xPredictDateString;
                    }

                    //最后一天的四个数
                    Integer oneDayPresent = lastDay.getPresent_confirm();
                    Log.i(TAG, "onResponse: 最后一天的的现存确诊： " + oneDayPresent);
                    oneDayFourNum[0] = oneDayPresent;
                    oneDayFourNum[1] = lastDay.getTotal_confirm();
                    oneDayFourNum[2] = lastDay.getTotal_heal();
                    oneDayFourNum[3] = lastDay.getTotal_dead();
                    Log.i(TAG, "onResponse: 最后一天的的累计死亡： " + oneDayFourNum[3]);

                    //真实线的节点数量，要根据传进来的数量啦
                    numOfRealPoints = nationList.size();
                    Log.i(TAG, "onResponse: 世界真实线的节点数量：" + numOfRealPoints);

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
                        //x轴标签
                        String dateStr1 = oneDay1.getDate().toString();
                        //分离月/日
                        String month1 = dateStr1.substring(5, 7).replaceFirst("0", "");
                        String day1 = dateStr1.substring(8, 10).replaceFirst("0", "");
                        //x轴用的标签
                        String xDateString1 = month1 + "/" + day1;
                        xRealLabel[i] = xDateString1;
                    }

                }
                //已经获取完毕
                isGetFinished = true;
                isGetSuccess = true;
                Log.i(TAG, "isGetFinished世界成功" + isGetFinished);
                Log.i(TAG, "isGetSuccess世界成功" + isGetSuccess);
            }

            @Override
            public void onFailure(Call<List<Alltime_world>> call, Throwable t) {
                Log.i(TAG, "世界onFailure..." + t.toString());

                //已经获取完毕
                isGetFinished = true;
                isGetSuccess = false;
                Log.i(TAG, "isGetFinished世界失败" + isGetFinished);
                Log.i(TAG, "isGetSuccess世界失败" + isGetSuccess);
            }
        });

    }

    /**
     * 从后端获取预测数据
     *
     * @param name 传给后端的地区名
     * @author qy, lym
     */
    public static void getPredict(String name) {
        Log.i(TAG, "进入获取预测：" + name);

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
                Log.i(TAG, "预测onResponse --> " + response.code());
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    List<Integer> predict = response.body();
                    //第一天预测的数据
                    Log.i(TAG, "onResponse: 第一天预测的数据： " + predict.get(0));

                    //将预测数据放到线上
                    for (int j = 0; j < predict.size(); ++j) {
                        xyPredict[j] = predict.get(j);
                    }

                    //预测线的节点数量要根据传入数量
                    numOfForecastPoints = predict.size();


                }
                //已经获取完毕
                isGetFinished = true;
                Log.i(TAG, "isGetFinished预测成功" + isGetFinished);

            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Log.i(TAG, "预测onFailure..." + t.toString());

                //已经获取完毕
                isGetFinished = true;
                Log.i(TAG, "isGetFinished预测失败" + isGetFinished);

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
//        Log.i(TAG, "进入initReal");

        for (int i = 0; i < numOfRealLines; ++i) {
            float[] linePoints = new float[numOfRealPoints];//一条线上面的点
            for (int j = 0; j < numOfRealPoints; ++j) {
                //debug TAG
//                Log.d(TAG, "initReal：xyReal[i][j]: " + xyReal[i][j]);
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
            for (int j = 0; j < numOfForecastPoints; ++j) {
                linePoints[j] = xyPredict[j];

//                if (hasControl) {
//                    //如果进行控制
//                    linePoints[j] = 1150000 - j * j * 1000;
//                } else {
//                    //群体免疫
//                    float x = j * 1000;
//                    linePoints[j] = 1150000 + (float) Math.sqrt(x) * 1000;
//                }
            }
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
        //防止null报错，做一个标志
        boolean haveData;
        if (xRealLabel == null || xRealLabel[0] == null || xRealLabel[0].equals("")) {
            Log.i(TAG, "initRealAxis还没加载数据");
            haveData = false;
        } else {
            Log.i(TAG, "initRealAxis已经加载数据，准备建立标签");
            haveData = true;
        }

        //实际的标签列表
        List<String> strings = new ArrayList<>();

        //如果还没有加载，就用空坐标轴
        for (int i = 0; i < numOfRealLines; i++) {
            for (int j = 0; j < numOfRealPoints; j++) {
                if (!haveData) {
                    strings.add("");
                } else {
                    strings.add(xRealLabel[j]);
                }
            }
            if (axisLableList.size() < numOfRealLines) {
                //如果是空的就初始化
                axisLableList.add(strings);
            } else {
                //如果不是空的就应该更新
                axisLableList.set(i, strings);
            }
        }
    }

    /**
     * 给前端用的 初始化预测坐标轴
     *
     * @author lym
     */
    public static void initForecastAxis() {
        //防止null报错，做一个标志
        boolean haveData;
        if (xPredictLabel == null || xPredictLabel[0] == null || xPredictLabel[0].equals("")) {
            haveData = false;
        } else {
            haveData = true;
        }

        List<String> strings = new ArrayList<>();

        //建立预测标签
        for (int i = 0; i < numOfForecastLines; i++) {
            //对于每一条预测线
            for (int j = 0; j < numOfForecastPoints; j++) {
                if (!haveData) {
                    strings.add("");
                } else {
                    strings.add(xPredictLabel[j]);
                }
            }
            if (axisLableList.size() < numOfRealLines + numOfForecastLines) {
                //如果线组里面还没有预测线，就新添加
                axisLableList.add(strings);
            } else {
                //如果已经有预测线，就更新
                axisLableList.set(i + numOfRealLines, strings);
            }
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

    public static Integer[] getOneDayFourNum() {
        return oneDayFourNum;
    }

    public static Integer getMaxY() {
        Integer maxY = 0;
        if (oneDayFourNum[1] != null) {
            //最大的就是最后一天的累计确诊
            maxY = oneDayFourNum[1];
            //留出10%的余地
            maxY = (int) (maxY + maxY * 0.1);
        }
        return maxY;
    }
}
