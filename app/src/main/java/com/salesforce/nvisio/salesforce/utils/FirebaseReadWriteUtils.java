package com.salesforce.nvisio.salesforce.utils;

import android.content.Context;
import android.widget.Toast;

import com.salesforce.nvisio.salesforce.Model.OutletInformation;

import java.util.List;
import java.util.Map;

/**
 * Created by USER on 31-Jan-18.
 */

public class FirebaseReadWriteUtils {
    private Context context;
    private FirebaseReferenceUtils firebaseReferenceUtils;

    public FirebaseReadWriteUtils(Context context) {
        this.context = context;
        firebaseReferenceUtils=new FirebaseReferenceUtils(context);
    }

    public void SaveAppointmentSchedules(String userId, List<String> selectedOutlet, Map<String,OutletInformation>outletInformationMap){
        if (selectedOutlet.size()>0){
            OutletInformation outletInformation;
            for (int i = 0; i <selectedOutlet.size() ; i++) {
                outletInformation=outletInformationMap.get(selectedOutlet.get(i));
                firebaseReferenceUtils.getAppointmentRef(userId).push().setValue(outletInformation);
            }
            Toast.makeText(context, "Appointment schedules have been succesfully set for "+userId, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "No appointment has been set!", Toast.LENGTH_SHORT).show();
        }
    }


}
