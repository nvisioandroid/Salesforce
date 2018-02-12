package com.salesforce.nvisio.salesforce.Model;

/**
 * Created by USER on 01-Feb-18.
 */

public class TaskData {

    public String getStartingTimeHour() {
        return startingTimeHour;
    }

    public void setStartingTimeHour(String startingTimeHour) {
        this.startingTimeHour = startingTimeHour;
    }

    public String getStartingTimeMin() {
        return startingTimeMin;
    }

    public void setStartingTimeMin(String startingTimeMin) {
        this.startingTimeMin = startingTimeMin;
    }

    public String getStartInterval() {
        return startInterval;
    }

    public void setStartInterval(String startInterval) {
        this.startInterval = startInterval;
    }

    public String getFinishingTimeHour() {
        return finishingTimeHour;
    }

    public void setFinishingTimeHour(String finishingTimeHour) {
        this.finishingTimeHour = finishingTimeHour;
    }

    public String getFinishingTimeMin() {
        return finishingTimeMin;
    }

    public void setFinishingTimeMin(String finishingTimeMin) {
        this.finishingTimeMin = finishingTimeMin;
    }

    public String getFinishInterval() {
        return finishInterval;
    }

    public void setFinishInterval(String finishInterval) {
        this.finishInterval = finishInterval;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
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

    private String startingTimeHour;
    private String startingTimeMin;
    private String startInterval;
    private String finishingTimeHour;
    private String finishingTimeMin;
    private String finishInterval;
    private String taskStatus;
    private String task;
    private String subTask;
    private String durationInString;
    private String startTime;
    private String FinishTime;

    public String getPerformedDate() {
        return PerformedDate;
    }

    public void setPerformedDate(String performedDate) {
        PerformedDate = performedDate;
    }

    private String PerformedDate;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return FinishTime;
    }

    public void setFinishTime(String finishTime) {
        FinishTime = finishTime;
    }

    public String getDurationInString() {
        return durationInString;
    }

    public void setDurationInString(String durationInString) {
        this.durationInString = durationInString;
    }

    public String getDurationInMIn() {
        return durationInMIn;
    }

    public void setDurationInMIn(String durationInMIn) {
        this.durationInMIn = durationInMIn;
    }

    private String durationInMIn;

    public String getTaskStatus() {
        return taskStatus;
    }

    public TaskData() {
    }
}
