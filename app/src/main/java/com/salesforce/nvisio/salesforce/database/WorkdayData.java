package com.salesforce.nvisio.salesforce.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by USER on 01-Feb-18.
 */
@Entity(tableName = "workday")
public class WorkdayData {
    public WorkdayData() {
    }

    @PrimaryKey(autoGenerate = true)
    public int workdayId;

    public int getWorkdayId() {
        return workdayId;
    }

    public void setWorkdayId(int workdayId) {
        this.workdayId = workdayId;
    }

    public String getPerformedDate() {
        return performedDate;
    }

    public void setPerformedDate(String performedDate) {
        this.performedDate = performedDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getDurationInM() {
        return durationInM;
    }

    public void setDurationInM(String durationInM) {
        this.durationInM = durationInM;
    }

    public String getSalesId() {
        return SalesId;
    }

    public void setSalesId(String salesId) {
        SalesId = salesId;
    }

    private String performedDate,startTime,EndTime,totalDuration,durationInM,SalesId;
}
