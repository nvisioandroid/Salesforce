package com.salesforce.nvisio.salesforce.application;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by USER on 28-Dec-17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
