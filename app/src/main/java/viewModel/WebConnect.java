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
    private static final String TAG = "Repository";

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


    //后端用的国家名
    private static String name;

    /**
     * 从后端获取省份疫情数据
     *
     * @param name 国家名
     * @author qy
     */
    public static List<Alltime_province> getProvince(String name) {
        //返回值
        final List<Alltime_province>[] provinceList = new List[]{new ArrayList<>()};

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
                Log.d(TAG, "onResponse --> " + response.code());
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    List<Alltime_province> province = response.body();
                    //todo 自动生成的[0]，不懂？ ——by lym
                    provinceList[0] = province;
                }
            }

            @Override
            public void onFailure(Call<List<Alltime_province>> call, Throwable t) {
                Log.d(TAG, "onFailure..." + t.toString());
            }
        });

        return provinceList[0];
    }


    /**
     * 网络
     */
    public static void web() {
        //尝试传一个地区名字
        name = "湖北";

        //网络获取数据，得到一个省份的列表
        List<Alltime_province> provinceList = getProvince(name);

        //一天的现存确诊
        Integer a = provinceList.get(0).getPresent_confirm();

        Log.d(TAG, "湖北的现存确诊1： " + a);


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
