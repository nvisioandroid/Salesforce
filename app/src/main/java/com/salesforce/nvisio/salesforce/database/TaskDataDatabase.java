package com.salesforce.nvisio.salesforce.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by USER on 01-Feb-18.
 */
@Entity(tableName = "task")
public class TaskDataDatabase {
    public TaskDataDatabase() {
    }

    @PrimaryKey(autoGenerate = true)

    public int taskId;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getPerformDate() {
        return performDate;
    }

    public void setPerformDate(String performDate) {
        this.performDate = performDate;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getSubTask() {
        return subTask;
    }

    public void setSubTask(String subTask) {
        this.subTask = subTask;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String performDate,task,subTask,startTime,endTime,duration;


    public String getDurationInMins() {
        return durationInMins;
    }

    public void setDurationInMins(String durationInMins) {
        this.durationInMins = durationInMins;
    }

    public String durationInMins;

}
