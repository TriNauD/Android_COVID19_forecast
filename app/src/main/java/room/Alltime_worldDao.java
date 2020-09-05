package room;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface Alltime_worldDao {

    //插入数据
    @Insert
    void insertWorlds(Alltime_world...alltime_worlds);

    //删除数据，默认根据主键删除
    @Delete
    void deleteWorld(Alltime_world...alltime_worlds);
    //根据name删除数据
    @Query("DELETE FROM Alltime_world WHERE name=:name")
    void deleteAllWorlds(String name);

    //更新数据
    @Update
    void updateWorld(Alltime_world...alltime_worlds);

    //查询数据
    @Query(" SELECT * FROM Alltime_world")
    List<Alltime_world> getAllWorldsLive();
    //根据国家名查询
    @Query("SELECT * FROM Alltime_world WHERE name= :name")
    Alltime_world getWorldByName(String name);

    //根据名字查询某个国家是否存在，存在返回1
    @Query("SELECT * FROM Alltime_world WHERE name LIKE :name LIMIT 1")
    int findWorldByName(String name);

}
