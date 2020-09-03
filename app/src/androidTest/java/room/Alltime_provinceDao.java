package room;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public interface Alltime_provinceDao {
    @Insert
    void insertProvince(Alltime_province...alltime_provinces);

    @Delete
    void deleteProvince(Alltime_province...alltime_provinces);

    @Update
    void updateProvince(Alltime_province...alltime_provinces);

    @Query("DELETE FROM Alltime_province")
    void deleteAllProvinces();
    @Query("SELECT * FROM Alltime_province")
    List<Alltime_province> getAllProvinces();

    @Query("SELECT * FROM Alltime_province WHERE id IN (:provinceIds)")
    List<Alltime_province> loadAllByIds(int[] provinceIds);
    @Query("SELECT * FROM Alltime_province WHERE name LIKE :name_ LIMIT 1")
    Alltime_province findProvinceByName(String name_);
}
