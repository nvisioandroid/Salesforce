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
public interface WorkdayDao {
    @Insert
    void insertWorkData(WorkdayData workdayData);

    @Update
    void updateLocateSR(WorkdayData workdayData);

    @Query("SELECT COUNT(*) FROM workday")
    int positionTableCount();

    @Query("SELECT * FROM workday")
    Maybe<List<WorkdayData>> getAllWorkData();

    @Query("SELECT * FROM workday WHERE performedDate LIKE :performedDate")
    Maybe<List<WorkdayData>> getWorkDataBasedOnDate(String performedDate);

    @Query("SELECT * FROM workday WHERE performedDate LIKE :performedDate AND SalesId LIKE :salesId")
    Maybe<List<WorkdayData>> getWorkDataBasedOnDateAndId(String performedDate,String salesId);

    @Query("DELETE FROM workday")
    void deleteAllWorkData();
}
