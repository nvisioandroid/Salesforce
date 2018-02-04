package com.salesforce.nvisio.salesforce.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by USER on 01-Feb-18.
 */
@Dao
public interface TaskDataDao {
    @Insert
    void insertTaskData(TaskDataDatabase taskDataDatabase);

    @Update
    void updateLocateSR(TaskDataDatabase taskDataDatabase);

    @Query("SELECT COUNT(*) FROM task")
    int positionTableCount();

    @Query("SELECT * FROM task")
    Maybe<List<TaskDataDatabase>> getAllTaskData();

    @Query("SELECT * FROM task WHERE performDate LIKE :performedDate")
    Maybe<List<TaskDataDatabase>> getTaskDataBasedOnDate(String performedDate);

    @Query("DELETE FROM task")
    void deleteAllTaskData();
}
