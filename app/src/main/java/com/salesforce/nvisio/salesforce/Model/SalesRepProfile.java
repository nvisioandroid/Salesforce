package com.salesforce.nvisio.salesforce.Model;

/**
 * Created by USER on 31-Jan-18.
 */

public class SalesRepProfile {
    private String SalesRepName;
    private String SalesRepId;
    private String Password;
    private String ManagerName;
    private String Designation;

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    private String Region;

    public SalesRepProfile() {
    }

    public String getSalesRepName() {
        return SalesRepName;
    }

    public void setSalesRepName(String salesRepName) {
        SalesRepName = salesRepName;
    }

    public String getSalesRepId() {
        return SalesRepId;
    }

    public void setSalesRepId(String salesRepId) {
        SalesRepId = salesRepId;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getManagerName() {
        return ManagerName;
    }

    public void setManagerName(String managerName) {
        ManagerName = managerName;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }
}
