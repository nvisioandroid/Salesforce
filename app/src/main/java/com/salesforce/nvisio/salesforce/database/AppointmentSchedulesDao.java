package com.salesforce.nvisio.salesforce.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.salesforce.nvisio.salesforce.Model.AppointmentSchedules;
import com.salesforce.nvisio.salesforce.Model.OutletInformation;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by USER on 01-Feb-18.
 */
@Dao
public interface AppointmentSchedulesDao {

    @Insert
    void insertAppointment(AppointmentSchedules appointmentSchedules);

    @Update
    void updateLocateSR(AppointmentSchedules appointmentSchedules);

    @Query("SELECT COUNT(*) FROM appointment")
    int positionTableCount();

    @Query("SELECT * FROM appointment")
    Maybe<List<AppointmentSchedules>> getAllAppoitment();

    @Query("DELETE FROM appointment")
    void deleteAllAppointments();
}
