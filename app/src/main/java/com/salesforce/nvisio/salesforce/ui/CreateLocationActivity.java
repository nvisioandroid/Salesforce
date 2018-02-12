package com.salesforce.nvisio.salesforce.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.salesforce.nvisio.salesforce.Model.OutletInformation;
import com.salesforce.nvisio.salesforce.R;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by USER on 28-Dec-17.
 */

public class CreateLocationActivity extends AppCompatActivity {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private OnMapReadyCallback onMapReadyCallback;
    private LatLng latLng;
    private SharedPrefUtils sharedPrefUtils;
    private LatLng draggedLatLng;
    private String TAG = "logg>>";

    @BindView(R.id.startLocationToolbar)
    Toolbar toolbar;
    @BindView(R.id.dragg_result)
    TextView resutText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        setContentView(R.layout.start_location_map);
        ButterKnife.bind(this);
        init();
        onMapReadyCallback=this::InitMap;
        mapInit();

    }


    private void init(){
        setSupportActionBar(toolbar);
        getSupportActionBar();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        sharedPrefUtils=new SharedPrefUtils(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void mapInit(){
        Log.d(TAG,"mapInit");
        new Thread(() -> {
            try {
                Log.d(TAG,"try");
                mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       mapFragment.getMapAsync(onMapReadyCallback);
                   }
               });
            }
            catch (Exception ignored){
                Log.d(TAG,"error: "+ignored);
            }
        }).start();
    }

    private void InitMap(GoogleMap googleMap) {
        Log.d(TAG,"InitMap");
        mMap = googleMap;
        MapStyleOptions styleOptions=MapStyleOptions.loadRawResourceStyle(CreateLocationActivity.this,R.raw.ubermapstyle);
        mMap.setMapStyle(styleOptions);
        LatLng latLng=new LatLng(23.738369, 90.395894);//shahbagh
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.738369, 90.395894), 15));
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng).draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(markerOptions);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
               Double lat=marker.getPosition().latitude;
               Double lon=marker.getPosition().longitude;
                draggedLatLng= new LatLng(lat,lon);
                //getStreenName();
                getCompleteAddressString(lat,lon);
            }
        });
    }

    private void getStreenName(){
        try {
            List<Address> addresses;
            Geocoder geocoder= new Geocoder(CreateLocationActivity.this);
            addresses = geocoder.getFromLocation(draggedLatLng.latitude,draggedLatLng.longitude,1);
            if(addresses != null && addresses.size() > 0 ){
                Address address = addresses.get(0);

                Log.d("street>>","local: "+address.getLocality()+" postal: "+address.getPostalCode()+" country: "+address.getCountryName());
                resutText.setText(address.getLocality()+", "+address.getPostalCode()+", "+address.getCountryName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        Log.w("cur>>", "lat: "+LATITUDE+" lon: "+LONGITUDE);
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                resutText.setText(strAdd);
                Log.w("cur>>", strReturnedAddress.toString());
            } else {
                Log.w("cur>>", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("cur>>", "Canont get Address!");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent =new Intent(CreateLocationActivity.this,MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.left_to_right_enter,R.anim.left_to_right_exit);
    }
    public void DoneClicked(View view) {

        if (draggedLatLng!=null){
            OutletInformation outletInformation=new OutletInformation();
            outletInformation.setOutletName("Starting Location");
            outletInformation.setOutletLatitude(draggedLatLng.latitude);
            outletInformation.setOutletLongitude(draggedLatLng.longitude);
            sharedPrefUtils.setLocationData(outletInformation);
            Intent intent =new Intent(CreateLocationActivity.this,MapActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right_enter,R.anim.left_to_right_exit);

        }
        else{
            Toast.makeText(this, "No location has been chosen!", Toast.LENGTH_SHORT).show();
        }
    }
    /*public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n\nAddress:\n" + result;
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n Unable to get address for this lat-long.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }*/
}
