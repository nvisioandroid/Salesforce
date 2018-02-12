package com.salesforce.nvisio.salesforce.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.salesforce.nvisio.salesforce.Model.LoginInfo;
import com.salesforce.nvisio.salesforce.Model.OutletInformation;
import com.salesforce.nvisio.salesforce.Model.SalesRepProfile;
import com.salesforce.nvisio.salesforce.Model.TaskData;
import com.salesforce.nvisio.salesforce.R;

import java.util.List;

import butterknife.BindView;

/**
 * Created by USER on 28-Dec-17.
 */

public class SharedPrefUtils {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefUtils(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences(context.getResources().getString(R.string.pref_name),Context.MODE_PRIVATE);
    }


    public void setUserAccess(String User){
        editor=sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.pref_whoIsTheUser),User).apply();
    }

    //USER ACCESS LEVEL RELATED
    public String getUserAccess(){
        return sharedPreferences.getString(context.getResources().getString(R.string.pref_whoIsTheUser),null);
    }

    public void removeUserAccess(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_whoIsTheUser)).apply();
    }
    public boolean isAdminTheUser(){
        if (getUserAccess()!=null){
            if (getUserAccess().equals(context.getResources().getString(R.string.sha_value_admin))){
                return true;
            }
        }
        return false;
    }

    //SALES REPS PROFILE REALTED
    public void setSalesRepInfo(SalesRepProfile salesRepProfile){
        Gson gson=new Gson();
        String json=gson.toJson(salesRepProfile);
        sharedPreferences.edit().putString(context.getResources().getString(R.string.pref_salesObject),json).apply();
    }

    public SalesRepProfile getSalesRepInfo(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString(context.getResources().getString(R.string.pref_salesObject), null);
        return gson.fromJson(json, SalesRepProfile.class);
    }

    public void removeSalesInfoObject(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_salesObject)).apply();
    }

    //START WORKDAY RELATED
    public void setStartWorkday(String time){
        sharedPreferences.edit().putString(context.getResources().getString(R.string.pref_startWorkday),time).apply();
    }
    public String getStartWorkday(){
        return sharedPreferences.getString(context.getResources().getString(R.string.pref_startWorkday),null);
    }
    public void removeStartWorkday(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_startWorkday)).apply();
    }

    //TASK RELATED
    //TASK AS AN OBJECT
    public void setTaskInfo(TaskData taskData){
        Gson gson=new Gson();
        String json=gson.toJson(taskData);
        sharedPreferences.edit().putString(context.getResources().getString(R.string.pref_taskObject),json).apply();
    }

    public TaskData getTaskData(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString(context.getResources().getString(R.string.pref_taskObject), null);
        return gson.fromJson(json, TaskData.class);
    }

    public void removeTaskData(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_taskObject)).apply();
    }

    //INDIVIDUAL TASK
    public void setTaskName(String taskName){
        sharedPreferences.edit().putString(context.getResources().getString(R.string.pref_task),taskName).apply();
    }

    public String getTaskName(){
        return sharedPreferences.getString(context.getResources().getString(R.string.pref_task),null);
    }

    public void removeTask(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_task)).apply();
    }

    //CHECK TASK RUNNING STATUS
    public boolean checkIfAnyTaskIsRunning(){
        TaskData taskData=getTaskData();
        if (taskData!=null){
            Log.d("sta>>","status: "+taskData.getTaskStatus());
            return !taskData.getTaskStatus().equals(context.getResources().getString(R.string.task_status_initial));
        }
        else{
            return false;
        }
    }

    //LOGIN LOGOUT STARTS
    //LOGIN INFO AS AN OBJECT
    public void setLoginInfo(LoginInfo loginInfo){
        Gson gson=new Gson();
        String json=gson.toJson(loginInfo);
        sharedPreferences.edit().putString(context.getResources().getString(R.string.pref_loginInfoObject),json).apply();
    }
    public LoginInfo getLoginInfo(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString(context.getResources().getString(R.string.pref_loginInfoObject), null);
        return gson.fromJson(json, LoginInfo.class);
    }
    public void removeLoginInfo(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_loginInfoObject)).apply();
    }

    //INDIVIDUAL LOGIN INFO
    public void setLoginStatus(){
        sharedPreferences.edit().putBoolean(context.getResources().getString(R.string.pref_isLogged),true).apply();
    }

    public boolean getLoginStatus(){
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.pref_isLogged),false);
    }

    public void removeLoginStatus(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_isLogged)).apply();
    }


    public void LogoutClicked(){
        removeSalesInfoObject();
        removeLoginInfo();
        removeUserAccess();
        removeLoginStatus();
        removeStartWorkday();
        removeTaskData();
    }

    public void managerLogout(){
        removeUserAccess();
        removeLoginStatus();
    }

    //MAP AND APPOINTMENT RELATED
    //STORING THE STARTING LOCATION INFORMATION OF THE SR WHILE OPTIMIIZING THE ROUTE MAP
    public void setLocationData(OutletInformation outletInformation){
        Gson gson=new Gson();
        String json=gson.toJson(outletInformation);
        sharedPreferences.edit().putString(context.getResources().getString(R.string.pref_initialLocation),json).apply();
    }

    public OutletInformation getLocationData(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString(context.getResources().getString(R.string.pref_initialLocation), null);
        return gson.fromJson(json, OutletInformation.class);
    }

    public void removeLocationData(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_initialLocation)).apply();
    }

    //PERMISSION
    public void setPermissionStatus(){
        sharedPreferences.edit().putBoolean(context.getResources().getString(R.string.pref_permission),true).apply();
    }

    public boolean getPermissionStatus(){
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.pref_permission),false);
    }

    public void removePermsission(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_permission)).apply();
    }
    //LOGIN LOGOUT ENDS












}
