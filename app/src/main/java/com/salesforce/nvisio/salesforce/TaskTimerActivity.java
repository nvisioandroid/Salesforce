package com.salesforce.nvisio.salesforce;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salesforce.nvisio.salesforce.Model.LoginInfo;
import com.salesforce.nvisio.salesforce.Model.SalesRepProfile;
import com.salesforce.nvisio.salesforce.Model.TaskData;
import com.salesforce.nvisio.salesforce.Model.TimeObject;
import com.salesforce.nvisio.salesforce.Model.job_model;
import com.salesforce.nvisio.salesforce.database.AppDataBase;
import com.salesforce.nvisio.salesforce.utils.DateSalesUtils;
import com.salesforce.nvisio.salesforce.utils.FirebaseReferenceUtils;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;
import com.salesforce.nvisio.salesforce.utils.UtilityClass;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by USER on 08-May-17.
 */

public class TaskTimerActivity extends AppCompatActivity {

    @BindView(R.id.proNameStamp)TextView name;
    @BindView(R.id.proLineManagerStamp)TextView manager;
    @BindView(R.id.proRegionStamp)TextView region;
    @BindView(R.id.proDesignationStamp)TextView designation;
    @BindView(R.id.StartDateStamp)TextView date;
    @BindView(R.id.proTimeStamp)TextView time;
    @BindView(R.id.activityName)TextView activityName;
    @BindView(R.id.startHour)TextView startHour;
    @BindView(R.id.startMin)TextView startMin;
    @BindView(R.id.finishHour)TextView finishHour;
    @BindView(R.id.finishMin)TextView finishMin;
    @BindView(R.id.timeSpent)TextView timeSpent;
    @BindView(R.id.am)TextView startInterval;
    @BindView(R.id.pm)TextView finishInterval;
    @BindView(R.id.startFinish)Button startFinishBtn;
    @BindView(R.id.toolbarTimer)Toolbar toolbar;

    private SharedPrefUtils sharedPrefUtils;
    private FirebaseReferenceUtils firebaseReferenceUtils;
    private DateSalesUtils dateSalesUtils;
    private SalesRepProfile salesRepProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark) );
        }
        setContentView(R.layout.task_timer_activity);
        ButterKnife.bind(this);
        init();
        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar();
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void init(){
        Utils.getmDatabase();
        sharedPrefUtils=new SharedPrefUtils(this);
        firebaseReferenceUtils=new FirebaseReferenceUtils(this);
        dateSalesUtils=new DateSalesUtils(this);
        //set time data to the textViews
        setBasicInformation();
        setTimerData();

    }

    private void setBasicInformation(){
        salesRepProfile=sharedPrefUtils.getSalesRepInfo();
        LoginInfo loginInfo = sharedPrefUtils.getLoginInfo();
        TaskData taskData=sharedPrefUtils.getTaskData();
        name.setText(salesRepProfile.getSalesRepName());
        date.setText(loginInfo.getLoginDateWithDayName());
        time.setText(loginInfo.getLoginTime());
        activityName.setText(taskData.getTask());
    }

    private void setTimerData(){
        if (sharedPrefUtils.checkIfAnyTaskIsRunning()){
            TaskData taskData=sharedPrefUtils.getTaskData();
            if (taskData.getTaskStatus().equals(getResources().getString(R.string.task_status_on_going))){
                //a task is already running
                startHour.setText(taskData.getStartingTimeHour());
                startMin.setText(taskData.getStartingTimeMin());
                startInterval.setText(taskData.getStartInterval());
                startFinishBtn.setText(getResources().getString(R.string.task_button_finish_text));
            }
            else if (taskData.getTaskStatus().equals(getResources().getString(R.string.task_status_done_but_not_saved))){
                //a task has finished but the data is not saved yet
                startHour.setText(taskData.getStartingTimeHour());
                startMin.setText(taskData.getStartingTimeMin());
                startInterval.setText(taskData.getStartInterval());
                finishHour.setText(taskData.getFinishingTimeHour());
                finishMin.setText(taskData.getFinishingTimeMin());
                finishInterval.setText(taskData.getFinishInterval());

                startFinishBtn.setText(getResources().getString(R.string.task_button_save_text));
            }
        }
    }

    public void StartFinishClicked(View view) {
        String type=startFinishBtn.getText().toString();
        switch (type){
            case "Start":
                start();
                break;
            case "Finish":
                finishPressed();
                break;
            default:
                save();
                break;
        }
    }
    //function to be called when user presses "START"
    private void start(){
        startFinishBtn.setText(getResources().getString(R.string.task_button_finish_text));
        YoYo.with(Techniques.FlipInX)
                .duration(2000)
                .playOn(startFinishBtn);
        getTime("Start");
    }
    private void finishPressed(){
        startFinishBtn.setText(getResources().getString(R.string.task_button_save_text));
        YoYo.with(Techniques.FlipInX)
                .duration(2000)
                .playOn(startFinishBtn);
        getTime("Finish");
    }

    private void save(){
        UtilityClass utilityClass=new UtilityClass(TaskTimerActivity.this);
        AlertDialog.Builder builder=new AlertDialog.Builder(TaskTimerActivity.this);
        builder.setTitle("Confirm your action!");
        builder.setMessage("Do you want to save your task completion informations?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            TaskData taskData=sharedPrefUtils.getTaskData();
            firebaseReferenceUtils.getDailyTaskRootRef(salesRepProfile.getSalesRepId(),dateSalesUtils.currentDate()).push().setValue(taskData);
            AppDataBase.getAppDatabase(TaskTimerActivity.this).taskDataDao().insertTaskData(utilityClass.convertTaskToTaskDatabase(taskData));
            sharedPrefUtils.removeTaskData();
            Toast.makeText(this, "Data has saved successfully!", Toast.LENGTH_SHORT).show();
            setTextViewToInitialState();
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();

    }

    private void setTextViewToInitialState(){
        startFinishBtn.setText("Start");
        startHour.setText("00");
        startMin.setText("00");
        finishHour.setText("00");
        finishMin.setText("00");
        startInterval.setText("");
        finishInterval.setText("");
        timeSpent.setText("");
    }

    //function to get current time in both 24 and 12 Hours format
    private void getTime(String buttonState){
        String currentTime=dateSalesUtils.getCurrentTime();
        TimeObject timeObject=dateSalesUtils.breakdownTheGivenTime(currentTime);
        TaskData TaskInfo=sharedPrefUtils.getTaskData();
       //putting the values into sharedPreference
        if (buttonState.equals("Start")){

            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(startHour);
            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(startMin);
            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(startInterval);
            startHour.setText(timeObject.getTimeInHour());
            startMin.setText(timeObject.getTimeInMin());
            startInterval.setText(timeObject.getTimeInterval());
            TaskData taskData=new TaskData();
            taskData.setTask(TaskInfo.getTask());
            taskData.setSubTask(TaskInfo.getSubTask());
            taskData.setTaskStatus(getResources().getString(R.string.task_status_on_going));
            taskData.setStartingTimeHour(timeObject.getTimeInHour());
            taskData.setStartingTimeMin(timeObject.getTimeInMin());
            taskData.setStartInterval(timeObject.getTimeInterval());
            taskData.setStartTime(currentTime);
            taskData.setPerformedDate(dateSalesUtils.currentDate());
            sharedPrefUtils.setTaskInfo(taskData);
        }
        else{
            //FINISH TIME
            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(finishHour);
            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(finishMin);
            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(finishInterval);
            finishHour.setText(timeObject.getTimeInHour());
            finishMin.setText(timeObject.getTimeInMin());
            finishInterval.setText(timeObject.getTimeInterval());
            TaskData taskData=new TaskData();
            taskData.setTask(TaskInfo.getTask());
            taskData.setSubTask(TaskInfo.getSubTask());
            taskData.setTaskStatus(getResources().getString(R.string.task_status_done_but_not_saved));
            taskData.setFinishingTimeHour(timeObject.getTimeInHour());
            taskData.setFinishingTimeMin(timeObject.getTimeInMin());
            taskData.setFinishInterval(timeObject.getTimeInterval());
            taskData.setStartingTimeHour(TaskInfo.getStartingTimeHour());
            taskData.setStartingTimeMin(TaskInfo.getStartingTimeMin());
            taskData.setStartInterval(TaskInfo.getFinishInterval());
            taskData.setStartTime(TaskInfo.getStartTime());
            taskData.setFinishTime(currentTime);
            taskData.setDurationInString(dateSalesUtils.getTimeDifferenceInString(TaskInfo.getStartTime(),currentTime));
            taskData.setDurationInMIn(dateSalesUtils.getTimeDifference(TaskInfo.getStartTime(),currentTime));
            taskData.setPerformedDate(dateSalesUtils.currentDate());
            //taskData.setDurationInString(dateSalesUtils.getTimeDifferenceInString());
            sharedPrefUtils.setTaskInfo(taskData);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*goToTaskActivity(new Intent(TaskTimerActivity.this,MainActivity.class));
        finish();*/
        Intent intent =new Intent(TaskTimerActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.left_to_right_enter,R.anim.left_to_right_exit);
    }


}
