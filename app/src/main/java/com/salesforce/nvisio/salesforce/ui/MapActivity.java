package com.salesforce.nvisio.salesforce.ui;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.salesforce.nvisio.salesforce.MainActivity;
import com.salesforce.nvisio.salesforce.Model.AppointmentSchedules;
import com.salesforce.nvisio.salesforce.Model.Element;
import com.salesforce.nvisio.salesforce.Model.Row;
import com.salesforce.nvisio.salesforce.R;
import com.salesforce.nvisio.salesforce.database.AppDataBase;
import com.salesforce.nvisio.salesforce.database.OptimizedMapData;
import com.salesforce.nvisio.salesforce.mapDataParser.DataParser;
import com.salesforce.nvisio.salesforce.network.RetrofitClient;
import com.salesforce.nvisio.salesforce.network.RetrofitInstance;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;
import com.salesforce.nvisio.salesforce.utils.ShortestPathUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by USER on 29-May-17.
 */

public class MapActivity extends AppCompatActivity{
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    private Marker mCurrLocationMarker;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private Map<String,String> nameKeyLatValue;
    private Map<Integer,String> numberKeyNameValue;
    private Map<String,String> latKeyNameValue;
    ArrayList<String> listContaingLatLng;

    private int counted=0;
    private float kmToMeter;
    private float previousValue= 0;
    private ArrayList<String> valueThatWillGoToMap;
    private String currentSource;

    private OnMapReadyCallback readyCallback;
    private int shortestMin=0;
    





    @BindView(R.id.progresContainer)RelativeLayout progress;
    @BindView(R.id.mapContainer)RelativeLayout mapContainer;
    @BindView(R.id.toolbarMap)Toolbar toolbar;
    private SharedPrefUtils sharedPrefUtils;
    //rxjava and retrofit
    private RetrofitClient client;
    private CompositeDisposable disposable;
    private List<AppointmentSchedules> appointmentSchedulesList;
    private Map<Integer,AppointmentSchedules>ShortestPathList;
    private Map<String,LatLng> outletNameWithLatLngValue;
    private ShortestPathUtils shortestPathUtils;
    private String origin="23.738369,90.395894";
    private String startingPosition;
    private int initialTime=0;
    //flags
    private boolean mergeDone=false;
    private int listIndex=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark) );
        }

        setContentView(R.layout.map_activity);
        ButterKnife.bind(this);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar();
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        //retrofit client
        client= RetrofitInstance.createService(RetrofitClient.class);
        disposable=new CompositeDisposable();
        readyCallback=this::setOnReadyMap;
    }
    private void init(){
        sharedPrefUtils=new SharedPrefUtils(this);
        disposable=new CompositeDisposable();
        appointmentSchedulesList=new ArrayList<>();
        outletNameWithLatLngValue=new HashMap<>();
        ShortestPathList=new HashMap<>();
        shortestPathUtils=new ShortestPathUtils(this);
        listContaingLatLng=new ArrayList<>();
        if (sharedPrefUtils.getLocationData()!=null){
            showLoading();
            startingPosition=sharedPrefUtils.getLocationData().getOutletLatitude()+","+sharedPrefUtils.getLocationData().getOutletLongitude();
            origin=startingPosition;
            if (AppDataBase.getAppDatabase(MapActivity.this).optimizedMapDataDAO().optimizedTableCount()!=0){
                //call map
                setUpGClient();
                startMap();
            }
            else{
                getAppointmentData();
            }



        }
        else{
          showAlertWhenNoStartingPosition();
        }
    }

    private void setOnReadyMap(GoogleMap googleMap){
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapStyleOptions mapStyleOptions=MapStyleOptions.loadRawResourceStyle(MapActivity.this,R.raw.ubermapstyle);
        mMap.setMapStyle(mapStyleOptions);
        mMap.setTrafficEnabled(false);
        MarkerOptions markerOptions = new MarkerOptions();
        disposable.add(AppDataBase.getAppDatabase(MapActivity.this).optimizedMapDataDAO().getOptimizedData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(optimizedMapData -> {
                    for (int i = 0; i <optimizedMapData.size() ; i++) {
                        LatLng latLng=new LatLng(optimizedMapData.get(i).getLatitude(),optimizedMapData.get(i).getLongitude());
                        markerOptions.position(latLng);
                        if (i==0){
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            markerOptions.title("I am here");
                        }
                        else{
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                            markerOptions.title(optimizedMapData.get(i).outletName);
                            //draw polyline
                            LatLng preLatLng=new LatLng(optimizedMapData.get(i-1).getLatitude(),optimizedMapData.get(i-1).getLongitude());
                            new FetchUrl().execute(shortestPathUtils.createDirectionApiURL(preLatLng+"",latLng+""));
                        }
                        mMap.addMarker(markerOptions);

                    }

                    LatLng startingPosition=new LatLng(optimizedMapData.get(0).getLatitude(),optimizedMapData.get(0).getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(startingPosition));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

                    /*if (i==0){
                        currentSource=getValue;
                    }

                    else{
                        String url=getUrl(currentSource,getValue);
                        new FetchUrl().execute(url);
                    }*/
                }));
    }
    private void getAppointmentData(){
        disposable.add(AppDataBase.getAppDatabase(MapActivity.this).appointmentSchedulesDao().getAllAppoitment()
        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        .subscribe(appointmentSchedules -> {
            if (appointmentSchedules!=null){
                appointmentSchedulesList.addAll(appointmentSchedules);
                for (int i = 0; i <appointmentSchedulesList.size() ; i++) {
                    String latlng=""+appointmentSchedulesList.get(i).getOutletLatitude()+","+appointmentSchedulesList.get(i).getOutletLongitude();
                    listContaingLatLng.add(latlng);
                }
                createURL();
            }
            else {
                Toast.makeText(this, "No appointments to show!", Toast.LENGTH_SHORT).show();
            }

        },throwable -> {
            hideLoading();
        }));
    }

    private void goToCreateLocationActivity(){
        Intent intent=new Intent(MapActivity.this, CreateLocationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent); overridePendingTransition(R.anim.right_to_left_enter,R.anim.right_to_left_exit);
    }

    private void showAlertWhenNoStartingPosition(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Request data");
        builder.setMessage("You have not assigned your starting position. Do you want to assign your starting position?");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok",(dialog, which) -> {
           dialog.dismiss();
           goToCreateLocationActivity();
        });
        builder.setNegativeButton("Cancel",(dialog, which) -> {
            //getAppointmentData();
            dialog.dismiss();

        });
        builder.show();
    }
    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
     /*   if (googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //googleApiClient.connect();
    }

    private void createURL(){
        String url=shortestPathUtils.getUrlReady(listContaingLatLng,origin);
        getDistanceMeasured(url);
    }

    private void getDistanceMeasured(String url){
        disposable.add(client.getD(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((distanceApiResponse) -> {
                    List<String> distanceList=distanceApiResponse.destinationAddresses;
                    List<Row> data=distanceApiResponse.rows;
                    int loopNumber=0;
                    int currentShortestTime =0;
                    String currentShortestDistance="0";
                    ShortestPathList.put(listIndex,shortestPathUtils.createAppointmentScheduleObjectForStartingPosition("Starting Position",sharedPrefUtils.getLocationData().getOutletLatitude(),sharedPrefUtils.getLocationData().getOutletLongitude()));
                    listIndex++;
                    for (int i = 0; i <data.size() ; i++) {
                        List<Element> distanceData=data.get(i).elements;
                        if (!mergeDone){
                            outletNameWithLatLngValue=shortestPathUtils.MergeOutletNameWithLatLong(distanceList,appointmentSchedulesList);
                            mergeDone=true;
                        }
                        for (int j = 0; j <distanceData.size() ; j++) {

                            String distance=distanceData.get(j).distance.text;
                            String duration=distanceData.get(j).duration.text;
                            //check the new time is the shortest time
                            int resultedShortestTime=shortestPathUtils.getShortestPath(duration,currentShortestTime,currentShortestDistance,distance);
                            if (resultedShortestTime!=currentShortestTime){
                                currentShortestTime=resultedShortestTime;
                                currentShortestDistance=shortestPathUtils.StructureDistance(distance)+"";
                                loopNumber=j;
                            }
                        }

                        String reultedOutletName=distanceList.get(loopNumber); //this is the outlet which has the shortest path
                        LatLng reultedLatLng=outletNameWithLatLngValue.get(reultedOutletName);
                        origin=reultedLatLng+"";
                        AppointmentSchedules appointmentSchedules=new AppointmentSchedules();
                        appointmentSchedules.setOutletName(reultedOutletName);
                        appointmentSchedules.setOutletLatitude(reultedLatLng.latitude);
                        appointmentSchedules.setOutletLongitude(reultedLatLng.longitude);
                        ShortestPathList.put(listIndex,appointmentSchedules);
                        listIndex++;

                        if (listContaingLatLng.size()!=0){
                            listContaingLatLng.remove(reultedLatLng+"");
                            createURL();
                        }
                        else{
                            //save into database
                            OptimizedMapData optimizedMapData=new OptimizedMapData();
                            for (int j = 0; j <ShortestPathList.size() ; j++) {
                                optimizedMapData.setOutletName(ShortestPathList.get(j).getOutletName());
                                optimizedMapData.setLatitude(ShortestPathList.get(j).getOutletLatitude());
                                optimizedMapData.setLongitude(ShortestPathList.get(j).getOutletLongitude());
                                AppDataBase.getAppDatabase(MapActivity.this).optimizedMapDataDAO().insertOptimizedData(optimizedMapData);
                            }
                            //call map
                            startMap();
                        }
                    }
                }));
    }

    private void startMap(){
        new Thread(() -> {
            try {
                mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                runOnUiThread(() -> {
                    mapFragment.getMapAsync(readyCallback);
                    hideLoading();
                });
            }
            catch (Exception ignored){
                Log.d("err>>","error: "+ignored);
            }
        }).start();
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    //ENDS

    //PARSING STARTS
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
            progress.setVisibility(View.GONE);
            mapContainer.setVisibility(View.VISIBLE);
        }
    }
    //ENDS
    private void showLoading(){
        progress.setVisibility(View.VISIBLE);
        mapContainer.setVisibility(View.GONE);
    }
    private void hideLoading(){
        progress.setVisibility(View.GONE);
        mapContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MapActivity.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
    }

}
