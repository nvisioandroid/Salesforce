package com.salesforce.nvisio.salesforce;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.salesforce.nvisio.salesforce.Model.LoginInfo;
import com.salesforce.nvisio.salesforce.Model.SalesRepProfile;
import com.salesforce.nvisio.salesforce.Model.TaskData;
import com.salesforce.nvisio.salesforce.Sectioned.ItemClickListener;
import com.salesforce.nvisio.salesforce.Sectioned.Section;
import com.salesforce.nvisio.salesforce.Sectioned.SectionedExpandableLayoutHelper;
import com.salesforce.nvisio.salesforce.Sectioned.section_item;
import com.salesforce.nvisio.salesforce.utils.DateSalesUtils;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by USER on 07-May-17.
 */

public class StartTaskActivity extends AppCompatActivity implements ItemClickListener {

    private StringBuilder dateBuilder, timeBuilder;
    private ArrayList<String> buttonHeader;
    private SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;
    private String[] list={"F2F Customer Visit - Job Site", "F2F Customer Visit - Prospect","Complaint Managment","Phone Calls","Informal Relationships"};
    @BindView(R.id.proNameStart) TextView name;
    @BindView(R.id.proDesignationStart)TextView designation;
    @BindView(R.id.proRegionStart)TextView region;
    @BindView(R.id.proLineManagerStart)TextView manager;
    @BindView(R.id.StartDate)TextView date;
    @BindView(R.id.proTimeStart)TextView time;
    @BindView(R.id.activityRecycler)RecyclerView activityRecycler;
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
        setContentView(R.layout.start_task_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        sharedPrefUtils=new SharedPrefUtils(this);
        if (sharedPrefUtils.checkIfAnyTaskIsRunning()){
            //go to activityStamp
            goToActivityStamp();
        }
        else{
            //continue
            //Toggle Mechanism
            sectionedExpandableLayoutHelper=new SectionedExpandableLayoutHelper(this,activityRecycler,this,1);
            //get SharedPreference Values
            SalesRepProfile salesRepProfile=sharedPrefUtils.getSalesRepInfo();
            LoginInfo loginInfo=sharedPrefUtils.getLoginInfo();
            DateSalesUtils dateSalesUtils=new DateSalesUtils(this);
            //StringBuilder
            dateBuilder=new StringBuilder();
            timeBuilder=new StringBuilder();

            dateBuilder.append("Logging ").append(loginInfo.getLoginDateWithDayName());
            timeBuilder.append("Started from ").append(loginInfo.getLoginTime()).append(", ").append(dateSalesUtils.getTimeDifference(loginInfo.getLoginTime(),dateSalesUtils.getCurrentTime())).append(" hours logged").toString();

            date.setText(dateBuilder);
            time.setText(timeBuilder);
            name.setText(salesRepProfile.getSalesRepName());
            //sectionedRecyclerView
            setTaskHeader();
        }


    }
    private void setTaskHeader(){
        //sectionedRecyclerView
        //header data entry
        buttonHeader=new ArrayList<>();
        buttonHeader.add("Project");
        buttonHeader.add("Research");
        buttonHeader.add("Meeting");
        buttonHeader.add("Lunch");
        buttonHeader.add("Other");

        setHeader();
    }
    @Override
    public void itemClicked(section_item item) {
            if (item.getName().equals("Please specify")){
                Log.d("task>>","Please specify: ");
                final Dialog dialog=new Dialog(StartTaskActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                EditText custom= dialog.findViewById(R.id.custom_edit);
                dialog.show();
                Button cancel= dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(v -> dialog.dismiss());
                Button submit=dialog.findViewById(R.id.startJob);
                submit.setOnClickListener(v -> {
                    Intent intent=new Intent(StartTaskActivity.this,TaskTimerActivity.class);
                    TaskData taskData=new TaskData();
                    taskData.setTask(sharedPrefUtils.getTaskName());
                    taskData.setSubTask(custom.getText().toString());
                    taskData.setTaskStatus(getResources().getString(R.string.task_status_initial));
                    sharedPrefUtils.setTaskInfo(taskData);
                    startActivity(intent);
                    finish();
                });
            }
            else{
                Log.d("task>>","Please specify ELSE ");
                Intent intent=new Intent(StartTaskActivity.this,TaskTimerActivity.class);
                TaskData taskData=new TaskData();
                taskData.setTask(sharedPrefUtils.getTaskName());
                taskData.setSubTask(item.getName());
                taskData.setTaskStatus(getResources().getString(R.string.task_status_initial));
                sharedPrefUtils.setTaskInfo(taskData);
                startActivity(intent);
                finish();
            }


    }

    @Override
    public void itemClicked(Section section) {
        //this is triggered when TASK is pressed
        sharedPrefUtils.setTaskName(section.getName());

    }
    private void goToActivityStamp(){
        Intent intent=new Intent(StartTaskActivity.this, TaskTimerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left_enter,R.anim.right_to_left_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(StartTaskActivity.this,MainActivity.class));
        finish();
    }
    //setting header to recyclerView
    private void setHeader(){
        ArrayList<section_item>Item;
        for (int i = 0; i <buttonHeader.size() ; i++) {
            Item=new ArrayList<>();
            switch (i){
                case 0:
                    /*for (int j = 0; j <5 ; j++) {
                       Item.add(new section_item(list[j],j));
                        }
                        sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);*/
                    Item.add(new section_item("Please specify",0));
                    sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);

                    break;
                case 1:
                    /*for (int j = 0; j <5 ; j++) {
                        Item.add(new section_item(list[j],j));
                    }
                        sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);*/

                    Item.add(new section_item("Please specify",0));
                    sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);

                    break;
                case 2:
                    /*for (int j = 0; j <5 ; j++) {
                        Item.add(new section_item(list[j],j));
                    }
                        sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);*/
                    Item.add(new section_item("Please specify",0));
                    sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);

                    break;
                case 3:
                   /* for (int j = 0; j <5 ; j++) {
                        Item.add(new section_item(list[j],j));
                    }
                        sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);*/
                    Item.add(new section_item("Please specify",0));
                    sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);

                    break;
                case 4:
                    /*for (int j = 0; j <5 ; j++) {
                        Item.add(new section_item("Please specify",0));
                    }*/
                    Item.add(new section_item("Please specify",0));
                        sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);

                    break;
            }
        }
        sectionedExpandableLayoutHelper.notifyDataSetChanged();
    }

}
