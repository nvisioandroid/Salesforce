package com.salesforce.nvisio.salesforce;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salesforce.nvisio.salesforce.Model.TaskData;
import com.salesforce.nvisio.salesforce.Model.login_data;
import com.salesforce.nvisio.salesforce.Sectioned.ItemClickListener;
import com.salesforce.nvisio.salesforce.Sectioned.Section;
import com.salesforce.nvisio.salesforce.Sectioned.SectionedExpandableLayoutHelper;
import com.salesforce.nvisio.salesforce.Sectioned.section_item;
import com.salesforce.nvisio.salesforce.utils.FirebaseReferenceUtils;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by USER on 07-May-17.
 */

public class StartWorkday extends AppCompatActivity implements ItemClickListener {

    private SharedPreferences settings;
    private Button facing, widoutFacing, nonSales,travelling,other,stop;
    private String dateStart,nameStart,imageData,timeIn12start, timeIn24start, timeIn24End, timeIn12End,timein24;
    private StringBuilder dateBuilder, timeBuilder;
    private long diffSeconds,diffMinutes,diffInHours;
    private ArrayList<String> buttonHeader;
    private RecyclerView activityRecycler;
    private SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;
    private String[] list={"F2F Customer Visit - Job Site", "F2F Customer Visit - Prospect","Complaint Managment","Phone Calls","Informal Relationships"};
    private DatabaseReference LoginInfoDatabase;
    private FirebaseDatabase firebase;
    @BindView(R.id.proNameStart) TextView name;
    @BindView(R.id.proDesignationStart)TextView designation;
    @BindView(R.id.proRegionStart)TextView region;
    @BindView(R.id.proLineManagerStart)TextView manager;
    @BindView(R.id.StartDate)TextView date;
    @BindView(R.id.proTimeStart)TextView time;
    @BindView(R.id.running)Button ongoing;

    private FirebaseReferenceUtils firebaseReferenceUtils;
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
        setContentView(R.layout.start_workday);
        ButterKnife.bind(this);
        init();
        firebase=FirebaseDatabase.getInstance();
        // get Reference
        settings=getSharedPreferences("salesforce",MODE_PRIVATE);
        LoginInfoDatabase= firebase.getReference("UserLogin/"+settings.getString("phone","")+"-"+settings.getString("name","")+"/details");

        //Toggle Mechanism
        activityRecycler= (RecyclerView) findViewById(R.id.activityRecycler);
        sectionedExpandableLayoutHelper=new SectionedExpandableLayoutHelper(this,activityRecycler,this,1);

        //get SharedPreference Values
        nameStart=settings.getString("name","");
        imageData=settings.getString("image_data","");
        timeIn12start=settings.getString("loginTime","");
        dateStart=settings.getString("loginDateWithDayName","");


        //set values to TextView
        name.setText(nameStart);

        //getting diffrence
        String diff=String.valueOf(timeDifferenceInHours());

        //StringBuilder
        dateBuilder=new StringBuilder();
        timeBuilder=new StringBuilder();
        //date
        dateBuilder.append("Logging ").append(dateStart).toString();
        date.setText(dateBuilder);

        //time
        timeBuilder.append("Started from ").append(timeIn12start).append(", ").append(timeDifferenceInHours()).append(" hours logged").toString();
        time.setText(timeBuilder);

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

    private void init(){
        JodaTimeAndroid.init(this);
        Utils.getmDatabase();
        sharedPrefUtils=new SharedPrefUtils(this);
        firebaseReferenceUtils=new FirebaseReferenceUtils(this);
    }
    @Override
    public void itemClicked(section_item item) {
        //no job is running
        if (sharedPrefUtils.checkIfAnyTaskIsRunning()){
            if (item.getName().equals("Please specify")){
                final Dialog dialog=new Dialog(StartWorkday.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                EditText custom= dialog.findViewById(R.id.custom_edit);
                dialog.show();
                Button cancel= dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(v -> dialog.dismiss());

                Button submit=dialog.findViewById(R.id.startJob);
                submit.setOnClickListener(v -> {
                    Intent intent=new Intent(StartWorkday.this,ActivityTimeStamp.class);
                    TaskData taskData=new TaskData();
                    taskData.setTask(custom.getText().toString());
                    taskData.setSubTask(custom.getText().toString());
                    taskData.setStartTime(time.getText().toString());
                    taskData.setTaskStatus(getResources().getString(R.string.task_status_on_going));
                    sharedPrefUtils.setTaskInfo(taskData);

                    /*SharedPreferences.Editor editor=settings.edit();
                    editor.putString("activityName",custom.getText().toString());
                    editor.apply();
                    intent.putExtra("date",date.getText().toString());
                    intent.putExtra("time",time.getText().toString());
                    intent.putExtra("name",name.getText().toString());*/
                    startActivity(intent);
                    finish();
                });
            }
            else{
                Intent intent=new Intent(StartWorkday.this,ActivityTimeStamp.class);
                TaskData taskData=new TaskData();
                taskData.setTask(item.getName());
                taskData.setSubTask(item.getName());
                taskData.setStartTime(time.getText().toString());
                taskData.setTaskStatus(getResources().getString(R.string.task_status_on_going));
                sharedPrefUtils.setTaskInfo(taskData);

                /*SharedPreferences.Editor editor=settings.edit();
                editor.putString("activityName",item.getName());
                editor.apply();
                intent.putExtra("date",date.getText().toString());
                intent.putExtra("time",time.getText().toString());
                intent.putExtra("name",name.getText().toString());*/
                startActivity(intent);
                finish();
            }

        }
        else {
            Toast.makeText(this, "You already have a sub-job running!", Toast.LENGTH_SHORT).show();
            TaskData taskData=new TaskData();
            taskData.setStartTime(time.getText().toString());
            taskData.setTaskStatus(getResources().getString(R.string.task_status_on_going));
            sharedPrefUtils.setTaskInfo(taskData);
            Intent intent=new Intent(StartWorkday.this,ActivityTimeStamp.class);
            /*intent.putExtra("date",date.getText().toString());
            intent.putExtra("time",time.getText().toString());
            intent.putExtra("name",name.getText().toString());*/
            startActivity(intent);
            finish();
        }


    }

    @Override
    public void itemClicked(Section section) {
        if (settings.getString("job","").equals("")){
            SharedPreferences.Editor editor=settings.edit();
            editor.putString("job",section.getName());
            editor.apply();
        }

    }




    //This function helps to show the amount of hours the user has logged in
    private long timeDifferenceInHours(){

        SimpleDateFormat format=new SimpleDateFormat("hh:mm aa");
        Date date1=null;
        Date date2=null;

        try{
            date1=format.parse(settings.getString("loginTime",""));
            date2=format.parse(getCurrentTime());

            DateTime d1=new DateTime(date1);
            DateTime d2=new DateTime(date2);

            diffInHours= Hours.hoursBetween(d1,d2).getHours()%24;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diffInHours;
    }

    //value found from this function will be inserted into DATABASE
    private String totalSpentTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date start = null;
        Date end = null;
        try{
            start=format.parse(settings.getString("loginTime",""));
            end=format.parse(getCurrentTime());

            DateTime d1=new DateTime(start);
            DateTime d2=new DateTime(end);

            diffInHours= Hours.hoursBetween(d1,d2).getHours()%24;
            diffMinutes= Minutes.minutesBetween(d1,d2).getMinutes()%60;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  String.valueOf(diffInHours)+" hours "+diffMinutes+" minutes";
    }

    //getting start time (login time) in 24 hours format. It will be useful for finding the difference between starting and ending time
    private String getCurrentTime(){

        //24 hours format starts
        DateTime dateTime=DateTime.now(DateTimeZone.forOffsetHoursMinutes(6,00));
        DateTimeFormatter formatter= DateTimeFormat.forPattern("hh:mm aa");
        timein24=formatter.print(dateTime);
        return timein24;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(StartWorkday.this,MainActivity.class));
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
