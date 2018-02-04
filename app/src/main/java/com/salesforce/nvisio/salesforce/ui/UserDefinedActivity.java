package com.salesforce.nvisio.salesforce.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.salesforce.nvisio.salesforce.MainActivity;
import com.salesforce.nvisio.salesforce.Model.SalesRepCred;
import com.salesforce.nvisio.salesforce.Model.SalesRepList;
import com.salesforce.nvisio.salesforce.Model.SalesRepProfile;
import com.salesforce.nvisio.salesforce.R;
import com.salesforce.nvisio.salesforce.utils.FirebaseReferenceUtils;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;

public class UserDefinedActivity extends AppCompatActivity {

    private SharedPrefUtils sharedPrefUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark) );
        }
        setContentView(R.layout.who_is_the_user);
        sharedPrefUtils=new SharedPrefUtils(this);

        if (sharedPrefUtils.getLoginStatus()){
            if (sharedPrefUtils.isAdminTheUser()){
             startActivity(new Intent(UserDefinedActivity.this,ManagerMainActivity.class));
             finish();
            }
            else{
                startActivity(new Intent(UserDefinedActivity.this,MainActivity.class));
                finish();
            }
        }
        /*if (sharedPrefUtils.getLoginStatus()){
            //go to corresponding activity
            if (sharedPrefUtils.isAdminTheUser()){
                //go to admin activity
            }
            else{
                // go to sr activity
            }
        }*/
       // demoUser();
    }

    public void SRClicked(View view) {
        sharedPrefUtils.setUserAccess(getResources().getString(R.string.sha_value_sr));
        startActivity(new Intent(UserDefinedActivity.this,LoginActivityNew.class));
    }

    public void ManagerClicked(View view) {
        sharedPrefUtils.setUserAccess(getResources().getString(R.string.sha_value_admin));
        startActivity(new Intent(UserDefinedActivity.this,LoginActivityNew.class));
    }

    private void demoUser(){
        SalesRepProfile salesRepProfile=new SalesRepProfile();
        SalesRepCred cred=new SalesRepCred();
        SalesRepList listModel=new SalesRepList();
        FirebaseReferenceUtils firebaseReferenceUtils=new FirebaseReferenceUtils(UserDefinedActivity.this);
        for (int i = 0; i <10 ; i++) {
            salesRepProfile.setSalesRepName("Shohel "+i);
            salesRepProfile.setSalesRepId("sr"+i);
            salesRepProfile.setPassword("pass"+i);
            salesRepProfile.setManagerName("manager"+i);
            salesRepProfile.setDesignation("Sales Representative");
            //profile
            firebaseReferenceUtils.getSalesRepProRef().child("sr"+i).setValue(salesRepProfile);
            //credential
            cred.setUserId("sr"+i);
            cred.setPassword("pass"+i);
            cred.setAccessStatus(getResources().getString(R.string.access_status));
            firebaseReferenceUtils.srCredentialRef().child("sr"+i).setValue(cred);
            //sales rep list
            listModel.setName("Shohel "+i);
            listModel.setUserId("sr"+i);
            firebaseReferenceUtils.getSRListRef().child("sr"+i).setValue(listModel);


        }
    }
}
