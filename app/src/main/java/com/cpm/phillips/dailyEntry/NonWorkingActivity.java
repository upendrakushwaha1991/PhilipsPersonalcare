package com.cpm.phillips.dailyEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cpm.phillips.R;
import com.cpm.phillips.constant.CommonFunctions;
import com.cpm.phillips.constant.CommonString;
import com.cpm.phillips.database.PhilipsAttendanceDB;
import com.cpm.phillips.getterSetter.CoverageBean;
import com.cpm.phillips.getterSetter.JCPMasterGetterSetter;
import com.cpm.phillips.getterSetter.NonWorkingReasonGetterSetter;
import com.cpm.phillips.xmlHandler.UploadCoverageClass;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.util.ArrayList;

public class NonWorkingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    ArrayList<NonWorkingReasonGetterSetter> reasondata = new ArrayList<NonWorkingReasonGetterSetter>();

    private Spinner reasonspinner, merNotAllowedSpinner;
    private PhilipsAttendanceDB database;
    ImageView img_selfie, img_cam_selfie;
    String reasonname, reasonid, subreasonname, subreasonid = "0", entry_allow, image_allow, image, entry, reason_reamrk, intime;
    Button save;
    private ArrayAdapter<CharSequence> reason_adapter;
    protected String _path, str,img_str;
    protected String _pathforcheck = "";
    private String image1 = "";
    private ArrayList<CoverageBean> cdata = new ArrayList<CoverageBean>();
    protected boolean _taken;
    protected static final String PHOTO_TAKEN = "photo_taken";
    private SharedPreferences preferences;
    String _UserId, visit_date, store_id;
    protected boolean status = true;
    EditText text, informTo;
    AlertDialog alert;
    ImageButton camera;
    RelativeLayout reason_lay, rel_cam;
    LinearLayout ll_camera;
    boolean leave_flag = false;
    Context context;
    ArrayList<JCPMasterGetterSetter> jcp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_working);
        declaration();
        jcp = database.getStoreData(visit_date, _UserId);
        if (jcp.size() > 0) {
            try {
                for (int i = 0; i < jcp.size(); i++) {
                    if (!jcp.get(i).getUPLOAD_STATUS().get(0).equals(CommonString.KEY_N)
                            || !jcp.get(i).getCHECKOUT_STATUS().get(0).equals(CommonString.KEY_N)) {

                        reasondata = database.getNonWorkingData(true);
                        break;
                    } else {
                        reasondata = database.getNonWorkingData(false);
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }


        intime = CommonFunctions.getCurrentTimeHHMMSSWithColon();

        reason_adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item);
        for (int i = 0; i < reasondata.size(); i++) {
            reason_adapter.add(reasondata.get(i).getReason().get(0));
        }
        reasonspinner.setAdapter(reason_adapter);
        reason_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonspinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub

        switch (arg0.getId()) {
            case R.id.spinner2:
                if (position != 0) {
                    reasonname = reasondata.get(position).getReason().get(0);
                    reasonid = reasondata.get(position).getReason_cd().get(0);
                    entry_allow = reasondata.get(position).getEntry_allow().get(0);
                    image_allow = reasondata.get(position).getIMAGE_ALLOW().get(0);

                    if (image_allow.equalsIgnoreCase("1")) {
                        _pathforcheck = "";
                        ll_camera.setVisibility(View.VISIBLE);
                        image = "true";
                    } else {
                        ll_camera.setVisibility(View.GONE);
                        image = "false";
                    }

                } else {
                    reasonname = "";
                    reasonid = "0";
                    image = "";
                    entry = "";
                    //reason_reamrk = "";
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }




    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub


         if(v.getId() == R.id.non_working_img_cam_selfie) {
             img_str = "";
             _pathforcheck = store_id + "_NonWorking_" + _UserId + ".jpg";
             intime = CommonFunctions.getCurrentTimeHHMMSSWithColon();
             _path = CommonString.FILE_PATH + _pathforcheck;
             CommonFunctions.startCameraActivity((Activity) context, _path);
         }


        if (v.getId() == R.id.fab) {
            if (validatedata()) {
                if (imageAllowed()) {
                    //if (isDataUploaded()) {
            /*        if (true) {*/

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Do you want to save the data ?")
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {

                                                alert.getButton(
                                                        AlertDialog.BUTTON_POSITIVE)
                                                        .setEnabled(false);


                                                if (entry_allow.equals("0")) {

                                                    database.deleteAllTables();

                                                    for (int i = 0; i < jcp.size(); i++) {

                                                        String stoteid = String.valueOf(jcp.get(i).getSTORE_CD().get(0));

                                                        CoverageBean cdata = new CoverageBean();
                                                        cdata.setStoreId(stoteid);
                                                        cdata.setUserId(_UserId);
                                                        cdata.setInTime(intime);
                                                        cdata.setOutTime(CommonFunctions.getCurrentTimeHHMMSSWithColon());
                                                        cdata.setVisitDate(visit_date);
                                                        cdata.setLatitude("0.0");
                                                        cdata.setLongitude("0.0");
                                                        cdata.setIntime_Image("");
                                                        cdata.setOuttime_Image("");
                                                        cdata.setStatus(CommonString.KEY_U);
                                                        cdata.setReason(reasonname);
                                                        cdata.setReasonid(reasonid);

                                                        new UploadCoverageClass(context, "Non working Data Uploading", cdata).execute();
                                                    }

                                                } else {

                                                    CoverageBean cdata = new CoverageBean();
                                                    cdata.setStoreId(store_id);
                                                    cdata.setUserId(_UserId);
                                                    cdata.setInTime(intime);
                                                    cdata.setOutTime(CommonFunctions.getCurrentTimeHHMMSSWithColon());
                                                    cdata.setVisitDate(visit_date);
                                                    cdata.setLatitude("0.0");
                                                    cdata.setLongitude("0.0");
                                                    cdata.setIntime_Image(image1);
                                                    cdata.setOuttime_Image("");
                                                    cdata.setStatus(CommonString.KEY_U);
                                                    cdata.setReason(reasonname);
                                                    cdata.setReasonid(reasonid);

                                                    new UploadCoverageClass(context, "Non working Data Uploading", cdata).execute();

                                                }
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                dialog.cancel();
                                            }
                                        });

                        alert = builder.create();
                        alert.show();
                 /*   } else {
                        Toast.makeText(context, "Data has been uploaded for some" +
                                " stores, please select another reason", Toast.LENGTH_LONG).show();
                    }*/
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please Capture Image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Please Select a Reason", Toast.LENGTH_SHORT).show();

            }
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
                        image1 = _pathforcheck;
                        _pathforcheck = "";
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public boolean imageAllowed() {
        boolean result = true;
        if (image.equalsIgnoreCase("true")) {

            if (image1.equalsIgnoreCase("")) {

                result = false;
            }
        }

        return result;

    }



    public boolean validatedata() {
        boolean result = false;
        if (reasonid != null && !reasonid.equalsIgnoreCase("") && !reasonid.equalsIgnoreCase("0")) {
            result = true;
        }
        return result;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }

    void declaration() {
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        reasonspinner = (Spinner) findViewById(R.id.spinner2);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = getIntent().getStringExtra(CommonString.KEY_STORE_CD);


        img_selfie =  findViewById(R.id.img_selfie);
        img_cam_selfie = findViewById(R.id.non_working_img_cam_selfie);
        ll_camera = findViewById(R.id.ll_camera);

        database = new PhilipsAttendanceDB(this);
        database.open();
        str = CommonString.FILE_PATH;

        img_cam_selfie.setOnClickListener(this);
        fab.setOnClickListener(this);
    }
}
