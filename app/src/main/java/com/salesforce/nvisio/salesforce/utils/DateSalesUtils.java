package com.salesforce.nvisio.salesforce.utils;

import android.content.Context;

import com.salesforce.nvisio.salesforce.Model.TimeObject;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by USER on 01-Feb-18.
 */

public class DateSalesUtils {
    private Context context;

    public DateSalesUtils(Context context) {
        this.context = context;
        JodaTimeAndroid.init(context);
    }

    public TimeObject breakdownTheGivenTime(String time){
        TimeObject timeObject=new TimeObject();
        String replaceSpace=time.trim().replace(" ",":");
        String[] splitTime=replaceSpace.split(":");
        timeObject.setTimeInHour(splitTime[0]);
        timeObject.setTimeInMin(splitTime[1]);
        timeObject.setTimeInterval(splitTime[2]);
        return timeObject;
    }
    
    public String getCurrentDateWithoutDayName(){
        DateTime crrntdate=new DateTime();
        String dayNumber=crrntdate.dayOfMonth().getAsText();//11
        String monthName=crrntdate.monthOfYear().getAsText();//May
        int year=crrntdate.getYear();///2017
        return  monthName+" "+dayNumber+", "+year;// November 17, 2017
    }
    public String getCurrentDateWithDayName(){
        DateTime crrntdate=new DateTime();
        String dayName=crrntdate.dayOfWeek().getAsText();//Tuesday
        String dayNumber=crrntdate.dayOfMonth().getAsText();//11
        String monthName=crrntdate.monthOfYear().getAsText();//May
        int year=crrntdate.getYear();///2017
        return dayName+", "+monthName+" "+dayNumber+", "+year;//Tuesday, November 17, 2017
    }
    public String getCurrentTime(){
        //current time in 12 hour format
        DateTime currentTime=DateTime.now(DateTimeZone.forOffsetHoursMinutes(6,00));
        DateTimeFormatter timeFormatter= DateTimeFormat.forPattern("hh:mm aa");
        return timeFormatter.print(currentTime);
    }

    public String currentDate(){
        //DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return String.valueOf(date);
    }

    public String currentDateWithDateFormat(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    //getting time duration
    public String getTimeDifference(String startTime,String endTime){
        String duration = null;
        SimpleDateFormat format=new SimpleDateFormat("hh:mm aa");
        Date date1=null;
        Date date2=null;
        try{
            date1=format.parse(startTime);
            date2=format.parse(endTime);

            DateTime d1=new DateTime(date1);
            DateTime d2=new DateTime(date2);

            int  diffInHours= Hours.hoursBetween(d1,d2).getHours() % 24;
            int diffInMins= Minutes.minutesBetween(d1,d2).getMinutes() % 60;
            duration=""+diffInHours+":"+diffInMins;


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return duration;
    }

    public String getTimeDifferenceInString(String loginTime,String logoutTime){
        String duration = null;
        SimpleDateFormat format=new SimpleDateFormat("hh:mm aa");
        Date date1=null;
        Date date2=null;
        try{
            date1=format.parse(loginTime);
            date2=format.parse(logoutTime);

            DateTime d1=new DateTime(date1);
            DateTime d2=new DateTime(date2);

            int  diffInHours= Hours.hoursBetween(d1,d2).getHours() % 24;
            int diffInMins= Minutes.minutesBetween(d1,d2).getMinutes() % 60;

            //hour=0, min=10+
            if (diffInHours==0 && diffInMins!=0){
                //min=10
                if (diffInMins>1){
                    duration=String.valueOf(diffInMins)+" minutes";
                }
                else{
                    //min=0-1
                    duration=String.valueOf(diffInMins)+" minute";
                }
            }
            //hour=1+, min=0
            else if (diffInHours!=0 && diffInMins==0){
                //hour=2
                if (diffInHours>1){
                    duration=String.valueOf(diffInHours)+" hours";
                }
                else{
                    //hour=0-1
                    duration=String.valueOf(diffInHours)+" hour";
                }
            }
            else{
                //hour=2+, min=2+
                if (diffInHours>1 && diffInMins>1){
                    duration=String.valueOf(diffInHours)+" hours "+diffInMins+" minutes";
                }
                else if (diffInHours>1 && diffInMins<2){
                    //hour=2+, min=1
                    duration=String.valueOf(diffInHours)+" hours "+diffInMins+" minute";
                }
                else if (diffInHours<2 && diffInMins>1){
                    //hour=1, min=2+
                    duration=String.valueOf(diffInHours)+" hour "+diffInMins+" minutes";
                }
                else{
                    //hour=1, min=1
                    duration=String.valueOf(diffInHours)+" hour "+diffInMins+" minute";
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return duration;
    }
    
}
