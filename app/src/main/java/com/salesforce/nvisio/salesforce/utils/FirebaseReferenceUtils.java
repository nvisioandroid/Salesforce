package com.salesforce.nvisio.salesforce.utils;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salesforce.nvisio.salesforce.R;

/**
 * Created by USER on 30-Jan-18.
 */

public class FirebaseReferenceUtils {
    private Context context;
    private DatabaseReference root;

    public FirebaseReferenceUtils(Context context) {
        this.context = context;
        root= FirebaseDatabase.getInstance().getReference();
    }

    //Path reference
    public DatabaseReference ManagerCredentialRef(){
        return root.child(context.getResources().getString(R.string.firebase_managerCredential));
    }

    public DatabaseReference srCredentialRef(){
        return root.child(context.getResources().getString(R.string.firebase_srCredential));
    }

    public DatabaseReference getSrProfileRef(String SID){
        DatabaseReference profile=root.child(context.getResources().getString(R.string.firebase_srProfile));
        return profile.child(SID);
    }

    public DatabaseReference getSalesRepProRef(){
        return root.child(context.getResources().getString(R.string.firebase_srProfile));
    }


    public DatabaseReference getIndividualDailyTask(String date){
        DatabaseReference dailyTask=root.child(context.getResources().getString(R.string.firebase_srDailyTask));
        return dailyTask.child(date);
    }

    public DatabaseReference getDailyTaskRootRef(String userId,String date){
        DatabaseReference databaseReference=root.child(context.getResources().getString(R.string.firebase_srDailyTask));
        return databaseReference.child(userId).child(date);
    }


    public DatabaseReference getSRListRef(){
        return root.child(context.getResources().getString(R.string.firebase_SrList));
    }

    public DatabaseReference getOutletListRef(String route){
        DatabaseReference routeRef=root.child(context.getResources().getString(R.string.firebase_outletList));
        return routeRef.child(route);
    }

    public DatabaseReference getOutletListDemo(){
        return root.child(context.getResources().getString(R.string.firebase_outletList));
    }

    public DatabaseReference getRouteRef(){
        return root.child(context.getResources().getString(R.string.firebase_route));
    }

    public DatabaseReference getAppointmentRef(String userId){
        DatabaseReference appointmentRef=root.child(context.getResources().getString(R.string.firebase_appointments));
        return appointmentRef.child(userId);
    }

    public DatabaseReference getRootDailyLogRef(){
        return root.child(context.getResources().getString(R.string.firebase_srDailyLog));
    }

    public DatabaseReference getIndividualDailyLogRef(String userId){
        return root.child(context.getResources().getString(R.string.firebase_srDailyLog)).child(userId);
    }



}
