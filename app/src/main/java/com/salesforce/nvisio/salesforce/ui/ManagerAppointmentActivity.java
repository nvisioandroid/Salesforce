package com.salesforce.nvisio.salesforce.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.salesforce.nvisio.salesforce.Model.OutletInformation;
import com.salesforce.nvisio.salesforce.Model.Route;
import com.salesforce.nvisio.salesforce.R;
import com.salesforce.nvisio.salesforce.utils.FirebaseReferenceUtils;
import com.salesforce.nvisio.salesforce.utils.FirebaseReadWriteUtils;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerAppointmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    @BindView(R.id.progressInRela)RelativeLayout progressBar;
    @BindView(R.id.containerApp)RelativeLayout container;
    @BindView(R.id.srSpinner)Spinner spinner;
    @BindView(R.id.routeSpinner)Spinner routeSpinner;
    @BindView(R.id.mainRelative)RelativeLayout rootRelative;
    @BindView(R.id.listViewContainer)RelativeLayout listViewContainer;
    @BindView(R.id.listv)ListView listView;
    @BindView(R.id.toolbar)Toolbar toolbar;

    private FirebaseReferenceUtils firebaseReferenceUtils;
    private SharedPrefUtils sharedPrefUtils;

    private List<String> SalesRepList;
    private List<String> outletList;
    private List<String> routeList;
    private List<String> SelectedOutletList;

    private Map<String,String> SalesRepMapList;
    private Map<String,OutletInformation>outletMapList;

    private String SelectedUserId=null;
    private String selectedRoute=null;

    private AdapterView.OnItemSelectedListener SrListItemListener;
    private AdapterView.OnItemSelectedListener RouteListItemListener;

    private FirebaseReadWriteUtils firebaseReadWriteUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark) );
        }
        setContentView(R.layout.appointment_layout);
        ButterKnife.bind(this);
        init();
        spinner.setOnItemSelectedListener(this);
        routeSpinner.setOnItemSelectedListener(this);
        //get Sales Rep List
        getSalesRepList();
    }

    private void init(){
        firebaseReferenceUtils=new FirebaseReferenceUtils(this);
        firebaseReadWriteUtils =new FirebaseReadWriteUtils(this);
        sharedPrefUtils=new SharedPrefUtils(this);
        SalesRepList=new ArrayList<>();
        outletList=new ArrayList<>();
        SalesRepMapList=new HashMap<>();
        outletMapList=new HashMap<>();
        SelectedOutletList=new ArrayList<>();
        routeList=new ArrayList<>();
        setLoading(true);
        setSupportActionBar(toolbar);
        getSupportActionBar();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void AddAppointment(View view) {
        if (SelectedUserId==null){
            Toast.makeText(this, "Please selec a sales representative first", Toast.LENGTH_SHORT).show();
        }
        else{
            setAlertDialogForToAddAppointment();
        }
    }



    private void getSalesRepList(){
        firebaseReferenceUtils.getSRListRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ClearList("sales");
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        com.salesforce.nvisio.salesforce.Model.SalesRepList salesRepList =snapshot.getValue(com.salesforce.nvisio.salesforce.Model.SalesRepList.class);
                        SalesRepList.add(salesRepList.getName());//only name
                        SalesRepMapList.put(salesRepList.getName(), salesRepList.getUserId()); //name with userId
                    }
                    setDataIntoSRListSpinner();
                    getRouteList();

                    //setLoading(false);

                }
                else{
                    Toast.makeText(ManagerAppointmentActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("err>>","error: "+databaseError);
                Toast.makeText(ManagerAppointmentActivity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getRouteList(){
        firebaseReferenceUtils.getRouteRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ClearList("route");
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Route route=snapshot.getValue(Route.class);
                        routeList.add(route.getRouteName());//route
                    }
                    setDataToRouteListSpinner();
                    setLoading(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                setLoading(false);
            }
        });
    }
    private void getOutletList(){
        firebaseReferenceUtils.getOutletListRef(selectedRoute).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ClearList("outlet");
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        OutletInformation outletInformation=snapshot.getValue(OutletInformation.class);
                        outletList.add(outletInformation.getOutletName());
                        outletMapList.put(outletInformation.getOutletName(),outletInformation);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ;
            }
        });
    }

    private void setDataIntoSRListSpinner(){
        ArrayAdapter<String> dataAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,SalesRepList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void setDataToRouteListSpinner(){
        ArrayAdapter<String> dataAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,routeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(dataAdapter);
    }



    private void setAlertDialogForToAddAppointment(){
        String[] outletNames=new String[outletList.size()];
        outletNames=outletList.toArray(outletNames);
        boolean[] is_Checked=new boolean[outletNames.length];
        for (int i = 0; i <outletNames.length ; i++) {
            is_Checked[i]= SelectedOutletList.size() != 0 && SelectedOutletList.contains(outletList.get(i));
        }
        SelectedOutletList.clear();
        AlertDialog.Builder builder=new AlertDialog.Builder(ManagerAppointmentActivity.this);
        builder.setTitle("Add appointments");
        builder.setMultiChoiceItems(outletNames, is_Checked, (dialog, which, isChecked) -> {
            is_Checked[which]=isChecked;
        });
        builder.setPositiveButton("Add", (dialog, which) -> {
            for (int i = 0; i <is_Checked.length ; i++) {
                boolean checked=is_Checked[i];
                if (checked){
                    SelectedOutletList.add(outletList.get(i));
                }
            }
            //call to add dynamic row
            if (SelectedOutletList.size()>0){
                setListView();
            }
            else{
                Toast.makeText(ManagerAppointmentActivity.this, "You have not added any appointment!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog=builder.create();
        dialog.show();

    }

    private void setListView(){
        ListAdapter listAdapter=new ArrayAdapter<String>(ManagerAppointmentActivity.this,R.layout.simple_outlet_row,SelectedOutletList);
        listView.setAdapter(listAdapter);
        showListView(true);
    }

    private void setLoading(boolean status){
        if (status){
            progressBar.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
        }
    }

    private void showListView(boolean status){
        if (status){
            listViewContainer.setVisibility(View.VISIBLE);
        }
        else{
            listViewContainer.setVisibility(View.GONE);
        }
    }

    private void ClearList(String type){
        if (type.equals("sales")){
            SalesRepMapList.clear();
            SalesRepList.clear();
        }
        else if (type.equals("route")){
            routeList.clear();
        }
        else{
            outletList.clear();
            outletMapList.clear();
        }

    }
    public void SetAppointment(View view) {
        firebaseReadWriteUtils.SaveAppointmentSchedules(SelectedUserId,SelectedOutletList,outletMapList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(ManagerAppointmentActivity.this,ManagerMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.left_to_right_enter,R.anim.left_to_right_exit);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.srSpinner:
                String item=parent.getItemAtPosition(position).toString();
                if (SelectedUserId!=null){
                    if (SelectedUserId.equals(SalesRepMapList.get(item))){
                        //same Sales rep is selected
                    }
                    else{
                        //difference Sales Rep
                        //remove all the appointments
                        SelectedUserId=SalesRepMapList.get(item);
                        SelectedOutletList.clear();
                        showListView(false);
                    }
                }
                else{
                    SelectedUserId=SalesRepMapList.get(item);
                }
                break;
            case R.id.routeSpinner:
                selectedRoute=parent.getItemAtPosition(position).toString();
                getOutletList();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
