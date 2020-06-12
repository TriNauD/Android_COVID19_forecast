package viewModel;

import java.util.List;

import domain.Alltime_province;
import domain.Alltime_world;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {
    @GET("/server/getProvince")
    Call<List<Alltime_province>> getProvince(@Query("name") String name);

    @GET("/server/getWorld")
    Call<List<Alltime_world>> getWorld(@Query("name") String name);

    @GET("/server/getPredict")
    Call<List<Integer>> getPredict(@Query("name") String name);
}
