package room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Alltime_world.class,Alltime_province.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public Alltime_worldDao alltime_worldDao;
    public Alltime_provinceDao alltime_provinceDao;
}
