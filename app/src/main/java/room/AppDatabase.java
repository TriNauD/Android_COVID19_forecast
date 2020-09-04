package room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Alltime_world.class,Alltime_province.class},version = 1,exportSchema = false)
@TypeConverters({Converters.class}) //date的数据类型转换
public abstract class AppDatabase extends RoomDatabase {
    //singleton,保证只有一个生成
    private static AppDatabase INSTANCE;
    public static synchronized AppDatabase getDatabase(final Context context){
        if (INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"app.db")
                    .createFromAsset("app.db") //预填充数据库
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public abstract Alltime_worldDao getAlltime_worldDao();
    public abstract Alltime_provinceDao getAlltime_provinceDao();
}
