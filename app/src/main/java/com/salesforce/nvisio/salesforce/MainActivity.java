package com.salesforce.nvisio.salesforce;


import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.salesforce.nvisio.salesforce.Model.LoginInfo;
import com.salesforce.nvisio.salesforce.Model.SalesRepProfile;
import com.salesforce.nvisio.salesforce.RecyclerViewAdapter.LogAdapter;
import com.salesforce.nvisio.salesforce.database.AppDataBase;
import com.salesforce.nvisio.salesforce.database.WorkdayData;
import com.salesforce.nvisio.salesforce.ui.MapActivity;
import com.salesforce.nvisio.salesforce.ui.UserDefinedActivity;
import com.salesforce.nvisio.salesforce.utils.DateSalesUtils;
import com.salesforce.nvisio.salesforce.utils.FirebaseReferenceUtils;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.proName)TextView proName;
    @BindView(R.id.proDesignation)TextView proDesignation;
    @BindView(R.id.proRegion)TextView proRegion;
    @BindView(R.id.proLineManager)TextView proLineManager;
    @BindView(R.id.proDate)TextView proDate;
    @BindView(R.id.proTime)TextView proTime;
    @BindView(R.id.viewLogs) TextView viewlog;
    @BindView(R.id.preHeader)TextView prelog;
    @BindView(R.id.noData)TextView noData;
    @BindView(R.id.profileImage)CircleImageView profileImage;
    @BindView(R.id.startActivity)Button goToTaskActivity;
    @BindView(R.id.startWorkday)Button startWorkday;
    @BindView(R.id.endWorkday)Button endWorkday;
    @BindView(R.id.logRecycler)RecyclerView logRecycler;

    private LogAdapter logAdapter;
    private SharedPrefUtils sharedPrefUtils;
    private FirebaseReferenceUtils firebaseReferenceUtils;
    private SalesRepProfile salesRepProfile;
    private LoginInfo loginInfo;
    private DateSalesUtils dateSalesUtils;
    private CompositeDisposable disposable;
    private List<WorkdayData> logDataList;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark) );
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        initRecycler();

    }
    private void init(){
        JodaTimeAndroid.init(this);
        disposable=new CompositeDisposable();
        sharedPrefUtils=new SharedPrefUtils(this);
        firebaseReferenceUtils=new FirebaseReferenceUtils(this);
        logDataList=new ArrayList<>();
        salesRepProfile=new SalesRepProfile();
        loginInfo=new LoginInfo();
        dateSalesUtils=new DateSalesUtils(this);
        getSalesRepProfileInfo();
        getLoginInformation();
        checkIfAnyTaskIsRunning();
        if (sharedPrefUtils.getStartWorkday()!=null){
            startWorkday.setText(getResources().getString(R.string.end_workday_btn));

            goToTaskActivity.setVisibility(View.VISIBLE);
            endWorkday.setVisibility(View.VISIBLE);
            startWorkday.setVisibility(View.GONE);
        }

        getLogData();
    }
    private void initRecycler(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        logRecycler.setLayoutManager(linearLayoutManager);
    }

    private void getSalesRepProfileInfo(){
        salesRepProfile=sharedPrefUtils.getSalesRepInfo();
       proName.setText(salesRepProfile.getSalesRepName());
        proDesignation.setText(salesRepProfile.getDesignation());
        proLineManager.setText(salesRepProfile.getManagerName());
        //proRegion.setText(salesRepProfile.getRegion());
    }

    private void getLoginInformation(){
        loginInfo=sharedPrefUtils.getLoginInfo();
        proDate.setText(loginInfo.getLoginDate());
        proTime.setText(dateSalesUtils.getCurrentTime());
    }

    private void checkIfAnyTaskIsRunning(){
        if (sharedPrefUtils.checkIfAnyTaskIsRunning()){
            Toast.makeText(this, "You have a task running!", Toast.LENGTH_SHORT).show();
        }
    }
    public void StartWorkClicked(View view) {
        Toast.makeText(this, "Your working hour has started!", Toast.LENGTH_SHORT).show();
           sharedPrefUtils.setStartWorkday(dateSalesUtils.getCurrentTime());
           startWorkday.setText(getResources().getString(R.string.end_workday_btn));
            goToTaskActivity.setVisibility(View.VISIBLE);
            endWorkday.setVisibility(View.VISIBLE);
            startWorkday.setVisibility(View.GONE);
    }

    public void TaskActivityClicked(View view) {
       startActivity(new Intent(MainActivity.this,StartTaskActivity.class));
       finish();
    }

    public void OptimizedMapClicked(View view) {
        startActivity(new Intent(MainActivity.this,MapActivity.class));
        finish();
    }

    public void ViewLogsClicked(View view) {
        startActivity(new Intent(MainActivity.this,ViewLogsActivity.class));
        finish();
    }

    public void endWork(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirm Action");
        builder.setMessage("Do you want to end your workday?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            //sharedPrefUtils.LogoutClicked();
            String endTime=dateSalesUtils.getCurrentTime();
            String spentTime=dateSalesUtils.getTimeDifference(sharedPrefUtils.getStartWorkday(),endTime);
            WorkdayData workdayData=new WorkdayData();
            workdayData.setSalesId(sharedPrefUtils.getSalesRepInfo().getSalesRepId());
            workdayData.setDurationInM(spentTime);
            workdayData.setStartTime(sharedPrefUtils.getStartWorkday());
            workdayData.setEndTime(endTime);
            workdayData.setTotalDuration(dateSalesUtils.getTimeDifferenceInString(sharedPrefUtils.getStartWorkday(),endTime));
            workdayData.setPerformedDate(dateSalesUtils.currentDateWithDateFormat());
            AppDataBase.getAppDatabase(MainActivity.this).workdayDao().insertWorkData(workdayData);
            firebaseReferenceUtils.getRootDailyLogRef().child(sharedPrefUtils.getSalesRepInfo().getSalesRepId()).child(dateSalesUtils.currentDate()).setValue(workdayData);
            sharedPrefUtils.LogoutClicked();
            AppDataBase.getAppDatabase(MainActivity.this).appointmentSchedulesDao().deleteAllAppointments();
            startActivity(new Intent(MainActivity.this, UserDefinedActivity.class));
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        builder.show();
    }

    private void getLogData(){
        firebaseReferenceUtils.getIndividualDailyLogRef(salesRepProfile.getSalesRepId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                logDataList.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        WorkdayData workdayData=snapshot.getValue(WorkdayData.class);
                        logDataList.add(workdayData);
                    }

                    logAdapter=new LogAdapter(logDataList,MainActivity.this);
                    logRecycler.setAdapter(logAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

