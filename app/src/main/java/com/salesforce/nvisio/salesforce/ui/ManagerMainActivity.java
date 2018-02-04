package com.salesforce.nvisio.salesforce.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.salesforce.nvisio.salesforce.Model.Route;
import com.salesforce.nvisio.salesforce.Model.SalesRepCred;
import com.salesforce.nvisio.salesforce.Model.SalesRepList;
import com.salesforce.nvisio.salesforce.Model.SalesRepProfile;
import com.salesforce.nvisio.salesforce.R;
import com.salesforce.nvisio.salesforce.utils.FirebaseReferenceUtils;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerMainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)Toolbar toolbar;
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
        setContentView(R.layout.manager_main_layout);
        ButterKnife.bind(this);
        sharedPrefUtils=new SharedPrefUtils(this);
        setSupportActionBar(toolbar);
        getSupportActionBar();
    }

    public void AppointmentClicked(View view) {
        Intent intent=new Intent(ManagerMainActivity.this, ManagerAppointmentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left_enter,R.anim.right_to_left_exit);
    }

    public void WorktimeClicked(View view) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manager, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_salesrep:
                AddSalesRep();
                break;
            case R.id.manager_logout:
                logout();
                break;
            default:
                break;
        }

        return true;
    }

    private void logout(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ManagerMainActivity.this);
        builder.setTitle("Confirm Action");
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            //sharedPrefUtils.LogoutClicked();
            sharedPrefUtils.managerLogout();
            startActivity(new Intent(ManagerMainActivity.this, UserDefinedActivity.class));
            finish();

        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        builder.show();
    }
    private void AddSalesRep(){
        SalesRepProfile profile=new SalesRepProfile();
        SalesRepCred cred=new SalesRepCred();
        SalesRepList salesList=new SalesRepList();
        FirebaseReferenceUtils firebaseReferenceUtils=new FirebaseReferenceUtils(ManagerMainActivity.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(ManagerMainActivity.this);
        alert.setTitle("Sales Representative's Information");

        LinearLayout layout = new LinearLayout(ManagerMainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText SalesRepName = new EditText(ManagerMainActivity.this);
        SalesRepName.setHint("Name");
        SalesRepName.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(SalesRepName);

        final EditText SalesId = new EditText(ManagerMainActivity.this);
        SalesId.setHint("Sales Representative's Id");
        SalesId.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(SalesId);

        final EditText SalesPassword = new EditText(ManagerMainActivity.this);
        SalesPassword.setHint("Password");
        SalesPassword.setGravity(Gravity.CENTER_HORIZONTAL);
        SalesPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(SalesPassword);

        alert.setView(layout);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!TextUtils.isEmpty(SalesRepName.getText().toString()) && !TextUtils.isEmpty(SalesId.getText().toString()) && !TextUtils.isEmpty(SalesPassword.getText().toString())) {
                    profile.setSalesRepName(SalesRepName.getText().toString().trim());
                    profile.setDesignation("Sales Representative");
                    profile.setManagerName("Rokon Ahmed");
                    profile.setSalesRepId(SalesId.getText().toString());
                    profile.setPassword(SalesPassword.getText().toString());

                    cred.setAccessStatus(getResources().getString(R.string.access_status));
                    cred.setUserId(SalesId.getText().toString());
                    cred.setPassword(SalesPassword.getText().toString());

                    salesList.setName(SalesRepName.getText().toString().trim());
                    salesList.setUserId(SalesId.getText().toString());

                    firebaseReferenceUtils.getSrProfileRef(SalesId.getText().toString()).setValue(profile);
                    firebaseReferenceUtils.getSRListRef().child(SalesId.getText().toString()).setValue(salesList);
                    firebaseReferenceUtils.srCredentialRef().child(SalesId.getText().toString()).setValue(cred);
                    Toast.makeText(ManagerMainActivity.this, "Sales Rep has been added successfully", Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(ManagerMainActivity.this, "Please fill all the informations!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }


    /*Map<String, User> users = new HashMap<>();
    users.put("alanisawesome", new User("June 23, 1912", "Alan Turing"));
    users.put("gracehop", new User("December 9, 1906", "Grace Hopper"));

    usersRef.setValueAsync(users);*/

    /*{
  "users": {
    "alanisawesome": {
      "date_of_birth": "June 23, 1912",
      "full_name": "Alan Turing"
    },
    "gracehop": {
      "date_of_birth": "December 9, 1906",
      "full_name": "Grace Hopper"
    }
  }
}*/
}
