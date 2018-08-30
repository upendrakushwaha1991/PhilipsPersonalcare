package com.cpm.phillipspc.dailyEntry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.cpm.phillipspc.constant.AlertandMessages;
import com.cpm.phillipspc.xmlHandler.CheckOutfromCurrentStore;
import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.phillipspc.R;
import com.cpm.phillipspc.constant.CommonFunctions;
import com.cpm.phillipspc.constant.CommonString;
import com.cpm.phillipspc.database.PhilipsAttendanceDB;
import com.cpm.phillipspc.getterSetter.CoverageBean;
import com.cpm.phillipspc.getterSetter.JCPMasterGetterSetter;
import com.cpm.phillipspc.xmlHandler.UploadCoverageClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.util.ArrayList;

public class StoreImageActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context context;
    SharedPreferences preferences;
    PhilipsAttendanceDB database;
    ImageView img_selfie, img_cam_selfie;
    TextView textview;
    String visit_date, username, visit_date_formatted;
    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 500; // 5 sec
    private static int FATEST_INTERVAL = 100; // 1 sec
    private static int DISPLACEMENT = 5; // 10 meters
    LocationManager locationManager;
    boolean enabled;
    private Location mLastLocation;
    double lat, lon;
    AlertDialog alert;
    String tag_for;
    JCPMasterGetterSetter jcpGetSetOfIntent;
    ArrayList<CoverageBean> coverageBeans;
    String store_id, _pathforcheck, _path, img_str, intime, outtime, intimeforStoreId, intimeImageforStoreId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_image);
        declaration();
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle(getResources().getString(R.string.gps));
            alertDialog.setMessage(getResources().getString(R.string.gpsebale));
            alertDialog.setPositiveButton(getResources().getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
            alertDialog.setNegativeButton(getResources().getString(R.string.no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();

        }
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            new AlertandMessages((Activity) context, null, null, null).backpressedAlertWithMessage("Image is not saved,Are you sure?");
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertandMessages((Activity) context, null, null, null).backpressedAlertWithMessage("Image is not saved,Are you sure?");
    }

    void declaration() {
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            tag_for = getIntent().getStringExtra(CommonString.TAG_FOR);
            jcpGetSetOfIntent = (JCPMasterGetterSetter) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
            store_id = jcpGetSetOfIntent.getSTORE_CD().get(0);
        } else {
            tag_for = "";
            store_id = "0";
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        img_selfie =  findViewById(R.id.img_selfie);
        img_cam_selfie = findViewById(R.id.img_cam_selfie);
        textview = findViewById(R.id.textimg);
        FloatingActionButton btn_save =  findViewById(R.id.fab);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        visit_date_formatted = preferences.getString(CommonString.KEY_YYYYMMDD_DATE, "");
        database = new PhilipsAttendanceDB(context);
        database.open();
        img_cam_selfie.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        textview.setText("Please Click Selfie With Store Full Image");
        if (tag_for.equalsIgnoreCase(CommonString.KEY_IN_TIME)) {
            //textview.setText("Please Click Store Intime Image");

        } else {
            //textview.setText("Please Click Store Outtime Image");

            coverageBeans = database.getCoverageSpecificData(visit_date, jcpGetSetOfIntent.getUSERNAME().get(0), store_id);
            if (coverageBeans.size() > 0) {
                intimeforStoreId = coverageBeans.get(0).getInTime();
                intimeImageforStoreId = coverageBeans.get(0).getIntime_Image();
            } else {
                intimeforStoreId = "00:00:00";
                intimeImageforStoreId = "";
            }

        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_cam_selfie:
                img_str = "";
                if (tag_for.equalsIgnoreCase(CommonString.KEY_IN_TIME)) {
                    _pathforcheck = store_id + "_" + username.replace(".", "") + "_StoreIntimeImg-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                    intime = CommonFunctions.getCurrentTimeHHMMSSWithColon();
                    outtime = "00:00:00";
                } else {
                    _pathforcheck = store_id + "_" + username.replace(".", "") + "_StoreOutTimeImg-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                    if (!intimeforStoreId.equalsIgnoreCase("00:00:00")) {
                        intime = intimeforStoreId;
                    } else {
                        intime = "00:00:00";
                    }
                    outtime = CommonFunctions.getCurrentTimeHHMMSSWithColon();
                }
                _path = CommonString.FILE_PATH + _pathforcheck;
                CommonFunctions.startCameraActivity((Activity) context, _path);
                break;
            case R.id.fab:
                if (img_str != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    String msg = "";
                    if (tag_for.equalsIgnoreCase(CommonString.KEY_IN_TIME)) {
                        msg = "Do you want to save Intime Data?";
                    } else {
                        msg = "Do you want to save outtime Data?";
                    }
                    builder.setMessage(msg)
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                                    CoverageBean cdata = new CoverageBean();
                                    cdata.setStoreId(store_id);
                                    cdata.setUserId(username);
                                    cdata.setInTime(intime);
                                    cdata.setOutTime(outtime);
                                    cdata.setVisitDate(visit_date);
                                    cdata.setLatitude(lat + "");
                                    cdata.setLongitude(lon + "");
                                    if (tag_for.equalsIgnoreCase(CommonString.KEY_IN_TIME)) {
                                        cdata.setIntime_Image(img_str);
                                        cdata.setOuttime_Image("");
                                        cdata.setStatus(CommonString.KEY_CHECK_IN);
                                    } else {
                                        cdata.setIntime_Image(intimeImageforStoreId);
                                        cdata.setOuttime_Image(img_str);
                                    }
                                    cdata.setReasonid("0");
                                    cdata.setReason("");

                                    String msg2 = "";
                                    if (tag_for.equalsIgnoreCase(CommonString.KEY_IN_TIME)) {
                                        msg2 = "InTimeData Uploading";
                                        new UploadCoverageClass(context, msg2, cdata).execute();
                                    } else {
                                        msg2 = "OutTimeData Uploading";
                                        new CheckOutfromCurrentStore(context, msg2, cdata).execute();
                                    }
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    alert = builder.create();
                    alert.show();

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.clickimage), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(_path).exists()) {
                        try {
                            Bitmap bmp = BitmapFactory.decodeFile(_path);
                            img_selfie.setImageBitmap(bmp);
                        } catch (OutOfMemoryError ex) {
                            CommonFunctions.setScaledImage(img_selfie, _path);
                        }
                        img_cam_selfie.setVisibility(View.GONE);
                        img_selfie.setVisibility(View.VISIBLE);
                        img_str = _pathforcheck;
                        _pathforcheck = "";
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mLastLocation != null) {
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();

            }
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("error", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.notsuppoted)
                        , Toast.LENGTH_LONG)
                        .show();
                //finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

}
