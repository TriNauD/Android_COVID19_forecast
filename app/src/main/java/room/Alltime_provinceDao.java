package room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface Alltime_provinceDao {
    //插入数据
    @Insert
    void insertProvince(Alltime_province...alltime_provinces);

    //删除数据，默认根据主键删除
    @Delete
    void deleteProvince(Alltime_province...alltime_provinces);
    //根据name删除数据
    @Query("DELETE FROM Alltime_province WHERE name=:name")
    void deleteAllProvinces(String name);

    //更新数据
    @Update
    void updateProvince(Alltime_province...alltime_provinces);

    //查询数据
    @Query("SELECT * FROM Alltime_province")
    List<Alltime_province> getAllProvinces();
    //根据省份名查询
    @Query("SELECT * FROM Alltime_province WHERE name= :name")
    Alltime_province getProvinceByName(String name);
    //根据名字查询某个省份是否存在，存在返回1
    @Query("SELECT * FROM Alltime_province WHERE name LIKE :name LIMIT 1")
    Alltime_province findProvinceByName(String name);
}
