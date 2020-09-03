package room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities = {Alltime_world.class,Alltime_province.class},version = 1,exportSchema = false)

//date的数据类型转换
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public Alltime_worldDao alltime_worldDao;
    public Alltime_provinceDao alltime_provinceDao;
}
