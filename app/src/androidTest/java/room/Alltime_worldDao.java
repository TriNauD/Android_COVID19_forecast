package room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface Alltime_worldDao {
    @Insert
    void insertWorld(Alltime_world...worlds);

    @Delete
    void deleteWorld(Alltime_world...alltime_worlds);

    @Update
    void updateWorld(Alltime_world...alltime_worlds);

    @Query("DELETE FROM Alltime_world")
    void deleteAllWorlds();
    @Query("SELECT * FROM Alltime_world")
    List<Alltime_world> getAllWorlds();

    @Query("SELECT * FROM Alltime_world WHERE id IN (:worldIds)")
    List<Alltime_world> loadAllWorldsByIds(int[] worldIds);
    @Query("SELECT * FROM Alltime_world WHERE name LIKE :name_ LIMIT 1")
    Alltime_world findWorldByName(String name_);

}
