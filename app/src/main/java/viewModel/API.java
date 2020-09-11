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
    Call<List<Integer>> getPredict(@Query("name") String name,
                                   @Query("isNation") Boolean isNation,
                                   @Query("controlType") int controlType,
                                   @Query("startControlDate") String startControlDate,
                                   @Query("raiseLastTime") int raiseLastTime,
                                   @Query("controlGrade") int controlGrade,
                                   @Query("r1") int r1,
                                   @Query("b1") float b1,
                                   @Query("r2") int r2,
                                   @Query("b2") float b2,
                                   @Query("a") float a,
                                   @Query("v") float v,
                                   @Query("d") float d,
                                   @Query("n") int n
    );
}
