package uk.co.jatra.room1.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import java.util.Date;

/**
 * Created by tim on 04/06/2017.
 */
@Database(entities = {LogItItem.class}, version = 1)
@TypeConverters({LogItDatabase.Convertor.class})
public abstract class LogItDatabase extends RoomDatabase {
    private static LogItDatabase instance;

    public static LogItDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    LogItDatabase.class, "logit-db").build();
        }
        return instance;
    }

    public abstract LogItItemDAO logitItemDAO();

    public static class Convertor {
        @TypeConverter
        public static Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }
    }
}
