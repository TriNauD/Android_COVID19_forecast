package room;

import androidx.room.TypeConverter;

import java.sql.Date;

//数据类型转换器
public class Converters {
    //date转long
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    //long转date
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
