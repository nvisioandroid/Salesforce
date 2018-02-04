package com.salesforce.nvisio.salesforce.Model;

/**
 * Created by USER on 01-Feb-18.
 */

public class LoginInfo {

    private String loginTime,loginDate,loginDateWithDayName;

    public LoginInfo() {
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getLoginDateWithDayName() {
        return loginDateWithDayName;
    }

    public void setLoginDateWithDayName(String loginDateWithDayName) {
        this.loginDateWithDayName = loginDateWithDayName;
    }
}
