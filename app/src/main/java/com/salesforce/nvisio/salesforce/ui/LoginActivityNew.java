package com.salesforce.nvisio.salesforce.ui;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.salesforce.nvisio.salesforce.MainActivity;
import com.salesforce.nvisio.salesforce.Model.AppointmentSchedules;
import com.salesforce.nvisio.salesforce.Model.Credential;
import com.salesforce.nvisio.salesforce.Model.LoginInfo;
import com.salesforce.nvisio.salesforce.Model.OutletInformation;
import com.salesforce.nvisio.salesforce.Model.SalesRepCred;
import com.salesforce.nvisio.salesforce.Model.SalesRepProfile;
import com.salesforce.nvisio.salesforce.R;
import com.salesforce.nvisio.salesforce.Utils;
import com.salesforce.nvisio.salesforce.database.AppDataBase;
import com.salesforce.nvisio.salesforce.utils.DateSalesUtils;
import com.salesforce.nvisio.salesforce.utils.FirebaseReferenceUtils;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivityNew extends AppCompatActivity {
    @BindView(R.id.userid)EditText userid;
    @BindView(R.id.password)EditText password;
    @BindView(R.id.credentialContainer)RelativeLayout relativeLayout;
    @BindView(R.id.progress)ProgressBar progressBar;
    private FirebaseReferenceUtils firebaseReferenceUtils;
    private SharedPrefUtils sharedPrefUtils;
    private DateSalesUtils dateSalesUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark) );
        }
        setContentView(R.layout.login_activity_new);
        ButterKnife.bind(this);
        Utils.getmDatabase();
        init();
    }

    private void init(){
        firebaseReferenceUtils=new FirebaseReferenceUtils(this);
        sharedPrefUtils=new SharedPrefUtils(this);
        dateSalesUtils=new DateSalesUtils(this);
        checkIfAnyoneLoggedIn();
    }

    private void checkIfAnyoneLoggedIn(){
        if (sharedPrefUtils.getLoginStatus()){
            if (sharedPrefUtils.isAdminTheUser()){
                startActivity(new Intent(LoginActivityNew.this,ManagerMainActivity.class));
                finish();
            }
            else{
                startActivity(new Intent(LoginActivityNew.this,MainActivity.class));
                finish();
            }
        }
    }


    public void Login(View view) {
        LoadingStatus(true); //show progress bar
        if (!TextUtils.isEmpty(userid.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())){
            Query query = null;
            if (sharedPrefUtils.isAdminTheUser()){
                //user is admin
                checkCredential(firebaseReferenceUtils.ManagerCredentialRef().child(userid.getText().toString()));
            }
            else{
                //user is SR
                checkCredentialForSalesRep(firebaseReferenceUtils.srCredentialRef().child(userid.getText().toString()));
            }
        }
        else{
            Toast.makeText(this, "Please provide all informations", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCredentialForSalesRep(DatabaseReference databaseReference){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("scc>>","data: "+dataSnapshot);
                if (dataSnapshot.exists()){
                    SalesRepCred salesRepCred=dataSnapshot.getValue(SalesRepCred.class);
                    Log.d("scc>>","data inside: "+salesRepCred.getUserId()+" pass: "+salesRepCred.getPassword());
                    if (password.getText().toString().equals(salesRepCred.getPassword())&&salesRepCred.getAccessStatus().equals(getResources().getString(R.string.access_status))){
                        getSalesProfileInfo();
                        sharedPrefUtils.setLoginStatus();
                        sharedPrefUtils.setUserAccess("Sales");

                    }
                    else{
                        Toast.makeText(LoginActivityNew.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(LoginActivityNew.this, "Userid not matched", Toast.LENGTH_SHORT).show();
                    LoadingStatus(false); //hide progress bar
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LoadingStatus(false); //hide progress bar
                Toast.makeText(LoginActivityNew.this, "Data connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkCredential(DatabaseReference databaseReference){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                            Credential credential=dataSnapshot.getValue(Credential.class);
                            if (password.getText().toString().equals(credential.getPassword())){
                                Toast.makeText(LoginActivityNew.this, "You have successfully logged in!", Toast.LENGTH_SHORT).show();
                                sharedPrefUtils.setUserAccess("Admin");
                                startActivity(new Intent(LoginActivityNew.this,ManagerMainActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(LoginActivityNew.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                            }


                    }
                    else{
                        LoadingStatus(false); //hide progress bar
                        Toast.makeText(LoginActivityNew.this, "Userid not matched", Toast.LENGTH_SHORT).show();
                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    LoadingStatus(false); //hide progress bar
                }
            });
    }

    private void getSalesProfileInfo(){
        firebaseReferenceUtils.getSrProfileRef(userid.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    SalesRepProfile salesRepProfile=dataSnapshot.getValue(SalesRepProfile.class);
                    sharedPrefUtils.setSalesRepInfo(salesRepProfile);
                    getLoginInformation();
                    getAppointmentSchdeules();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getLoginInformation(){
        LoginInfo loginInfo=new LoginInfo();
        loginInfo.setLoginTime(dateSalesUtils.getCurrentTime());
        loginInfo.setLoginDate(dateSalesUtils.getCurrentDateWithoutDayName());
        loginInfo.setLoginDateWithDayName(dateSalesUtils.getCurrentDateWithDayName());
        sharedPrefUtils.setLoginInfo(loginInfo);
    }
    private void getAppointmentSchdeules(){
        firebaseReferenceUtils.getAppointmentRef(userid.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    OutletInformation outletInformation;
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        outletInformation=snapshot.getValue(OutletInformation.class);
                        AppointmentSchedules appointmentSchedules=new AppointmentSchedules();
                        appointmentSchedules.setOutletName(outletInformation.getOutletName());
                        appointmentSchedules.setOutletLatitude(outletInformation.getOutletLatitude());
                        appointmentSchedules.setOutletLongitude(outletInformation.getOutletLongitude());
                        AppDataBase.getAppDatabase(LoginActivityNew.this).appointmentSchedulesDao().insertAppointment(appointmentSchedules);

                    }
                    startActivity(new Intent(LoginActivityNew.this,MainActivity.class));
                    finish();
                    LoadingStatus(false); //hide progress bar


                }
                else{
                    startActivity(new Intent(LoginActivityNew.this,MainActivity.class));
                    finish();
                    LoadingStatus(false); //hide progress bar
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LoadingStatus(false); //hide progress bar
            }
        });
    }

    private void LoadingStatus(boolean status){
        if (status){
            progressBar.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.INVISIBLE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }


}
