package viewModel;

import android.arch.lifecycle.ViewModel;
import android.graphics.Color;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import domain.Alltime_province;
import domain.Alltime_world;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LineViewModel extends ViewModel {
    //调试使用的日志标签
    private static final String TAG = "LineViewModel";

    //“真实线”的数量
    private int numOfRealLines = 4;
    //“真实线”的节点数
    private int numOfRealPoints = 120;

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
    public int controlGrade;
    //地区名称
    public String name;

    /**
     * 初始化预测的图表
     * 包括点、线、轴；
     * 数据采用默认的预测参数来计算。
     *
     * @author lym
     */
    public void initForecastChart() {
        Log.i(TAG, "initForecastChart 进入函数");
        //————预测————
        //数
        //“预测线”的数量
        int numOfForecastLines = 1;
        //“预测线”的节点数
        int numOfForecastPoints = 15;
        for (int i = 0; i < numOfForecastLines; ++i) {
            float[] linePoints = new float[numOfForecastPoints];//一条线上面的点
            for (int j = 0; j < numOfForecastPoints; ++j) {
                if (hasControl) {
                    //如果进行控制
                    Log.i(TAG, "initForecastChart 函数：进行控制");
                    linePoints[j] = 1200 - j * j;
                } else {
                    //群体免疫
                    Log.i(TAG, "initForecastChart 函数：群体免疫");
                    float x = j * 1000;
                    linePoints[j] = 1200 + (float) Math.sqrt(x);
                }
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
            if (lines.size() >= numOfRealLines + numOfForecastLines) {
                //如果已经有这条线了
                lines.set(i + numOfRealLines, line);
            } else {
                lines.add(line);
            }
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

    /**
     * 初始化真实的图表
     * 包括点、线、轴；
     * 数据先用随机数。
     *
     * @author lym
     */
    public void initRealChart() {
        Log.i(TAG, "initRealChart 进入函数");
        //数
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
    }

    /**
     * 从后端获取省份疫情数据
     *
     * @author qy
     */
    public void getProvince(){
        Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.5:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        API api = retrofit.create(API.class);
        Call<List<Alltime_province>> province_task = api.getProvince(this.name);
        province_task.enqueue(new Callback<List<Alltime_province>>() {
            @Override
            public void onResponse(Call<List<Alltime_province>> call, Response<List<Alltime_province>> response) {
                Log.d(TAG, "onResponse --> " + response.code());
                if(response.code() == HttpURLConnection.HTTP_OK){
                    List<Alltime_province> province = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Alltime_province>> call, Throwable t) {
                Log.d(TAG,"onFailure..." + t.toString());
            }
        });



    }

    /**
     * 从后端获取国家疫情数据
     *
     * @author qy
     */
    public void getWorld(){
        Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.5:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        Call<List<Alltime_world>> world_task = api.getWorld(this.name);
        world_task.enqueue(new Callback<List<Alltime_world>>() {
            @Override
            public void onResponse(Call<List<Alltime_world>> call, Response<List<Alltime_world>> response) {
                Log.d(TAG, "onResponse --> " + response.code());
                if(response.code() == HttpURLConnection.HTTP_OK){
                    List<Alltime_world> world = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Alltime_world>> call, Throwable t) {
                Log.d(TAG,"onFailure..." + t.toString());
            }
        });
    }

    /**
     * 从后端获取预测数据
     *
     * @author qy
     */
    public void   getPredict(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.5:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        Call<List<Integer>> predict_task = api.getPredict(this.name);
        predict_task.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                Log.d(TAG, "onResponse --> " + response.code());
                if(response.code() == HttpURLConnection.HTTP_OK){
                    List<Integer> predict = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Log.d(TAG,"onFailure..." + t.toString());
            }
        });
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

    public boolean isHasControl() {
        return isHasControl();
    }

    public static void setHasControl(boolean hasControl) {
        setHasControl(hasControl);
    }


    public void setNumOfRealLines(int numOfRealLines) {
        this.numOfRealLines = numOfRealLines;
    }

    public int getNumOfRealPoints() {
        return numOfRealPoints;
    }

    public void setNumOfRealPoints(int numOfRealPoints) {
        this.numOfRealPoints = numOfRealPoints;
    }

    public List<float[]> getLineData() {
        return lineData;
    }

    public void setLineData(List<float[]> lineData) {
        this.lineData = lineData;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public void setAxesList(List<Axis[]> axesList) {
        this.axesList = axesList;
    }

    public List<List<String>> getAxisLableList() {
        return axisLableList;
    }

    public void setAxisLableList(List<List<String>> axisLableList) {
        this.axisLableList = axisLableList;
    }

    public String getStartControlDate() {
        return startControlDate;
    }

    public void setStartControlDate(String startControlDate) {
        this.startControlDate = startControlDate;
    }

    public int getRaiseLastTime() {
        return raiseLastTime;
    }

    public void setRaiseLastTime(int raiseLastTime) {
        this.raiseLastTime = raiseLastTime;
    }

    public int getControlGrade() {
        return controlGrade;
    }

    public void setControlGrade(int controlGrade) {
        this.controlGrade = controlGrade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}