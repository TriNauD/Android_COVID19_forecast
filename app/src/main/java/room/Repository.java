package room;

import android.content.Context;

import androidx.room.Room;

public class Repository {

    //初始化数据库
    private static AppDatabase appDatabase;
    private static Alltime_worldDao worldDao;

    /**
     * 更新本地数据库
     *
     * @author yk
     */
    public static void updateDB(Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "appdb").build();
        worldDao = appDatabase.getAlltime_worldDao();
        //Alltime_world world1 = new Alltime_world(WebConnect.getWorld(currentNation));
        //Alltime_world world2 = new Alltime_world(WebConnect.getWorld(currentNation));
        //worldDao.insertWorlds(WebConnect.getWorld(world1,world2));
    }
}
