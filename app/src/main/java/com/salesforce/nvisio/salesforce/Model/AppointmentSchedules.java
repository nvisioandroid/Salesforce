package com.salesforce.nvisio.salesforce.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by USER on 01-Feb-18.
 */
@Entity(tableName = "appointment")
public class AppointmentSchedules {
    @PrimaryKey(autoGenerate = true)
    public int appointId;
    public String outletName;
    public Double outletLatitude,outletLongitude;

    public int getAppointId() {
        return appointId;
    }

    public void setAppointId(int appointId) {
        this.appointId = appointId;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public Double getOutletLatitude() {
        return outletLatitude;
    }

    public void setOutletLatitude(Double outletLatitude) {
        this.outletLatitude = outletLatitude;
    }

    public Double getOutletLongitude() {
        return outletLongitude;
    }

    public void setOutletLongitude(Double outletLongitude) {
        this.outletLongitude = outletLongitude;
    }

    public AppointmentSchedules() {

    }
}
