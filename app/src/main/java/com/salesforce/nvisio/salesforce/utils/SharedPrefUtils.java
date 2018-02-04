package com.salesforce.nvisio.salesforce.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.salesforce.nvisio.salesforce.Model.LoginInfo;
import com.salesforce.nvisio.salesforce.Model.SalesRepProfile;
import com.salesforce.nvisio.salesforce.Model.TaskData;
import com.salesforce.nvisio.salesforce.R;

import java.util.List;

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


    public String getUserAccess(){
        return sharedPreferences.getString(context.getResources().getString(R.string.pref_whoIsTheUser),null);
    }

    public void removeUserAccess(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_whoIsTheUser)).apply();
    }


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

    public void setStartWorkday(String time){
        sharedPreferences.edit().putString(context.getResources().getString(R.string.pref_startWorkday),time).apply();
    }
    public String getStartWorkday(){
        return sharedPreferences.getString(context.getResources().getString(R.string.pref_startWorkday),null);
    }
    public void removeStartWorkday(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_startWorkday)).apply();
    }

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

    public boolean checkIfAnyTaskIsRunning(){
        return getTaskData() != null;
    }

    //LOGIN LOGOUT STARTS
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

    public void setLoginStatus(){
        sharedPreferences.edit().putBoolean(context.getResources().getString(R.string.pref_isLogged),true).apply();
    }

    public boolean getLoginStatus(){
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.pref_isLogged),false);
    }

    public void removeLoginStatus(){
        sharedPreferences.edit().remove(context.getResources().getString(R.string.pref_isLogged)).apply();
    }
    //LOGIN LOGOUT ENDS


    public boolean isAdminTheUser(){
        if (getUserAccess()!=null){
            if (getUserAccess().equals(context.getResources().getString(R.string.sha_value_admin))){
                return true;
            }
        }
        return false;
    }









}
