package com.salesforce.nvisio.salesforce.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.salesforce.nvisio.salesforce.Model.AppointmentSchedules;

/**
 * Created by USER on 26-Dec-17.
 */
@Database(entities = {AppointmentSchedules.class,TaskDataDatabase.class,WorkdayData.class,OptimizedMapData.class},version = 2)
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase INSTANCE;
    public abstract AppointmentSchedulesDao appointmentSchedulesDao();
    public abstract TaskDataDao taskDataDao();
    public abstract WorkdayDao workdayDao();
    public abstract OptimizedMapDataDAO optimizedMapDataDAO();

    public static AppDataBase getAppDatabase(Context context){
        if (INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),AppDataBase.class,"salesforce").allowMainThreadQueries().build();

        }
        return INSTANCE;
    }
}
