package com.salesforce.nvisio.salesforce.Model;

/**
 * Created by USER on 31-Jan-18.
 */

public class SalesRepCred {
    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    private String Password;
    private String userId;

    public String getAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(String accessStatus) {
        this.accessStatus = accessStatus;
    }

    private String accessStatus;



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SalesRepCred() {

    }
}
