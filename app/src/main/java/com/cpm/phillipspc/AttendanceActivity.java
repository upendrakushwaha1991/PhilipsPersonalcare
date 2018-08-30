package com.cpm.phillipspc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.cpm.phillipspc.constant.AlertandMessages;
import com.cpm.phillipspc.constant.CommonString;
import com.cpm.phillipspc.database.PhilipsAttendanceDB;
import com.cpm.phillipspc.getterSetter.NonWorkingReasonGetterSetter;

import java.util.ArrayList;

public class AttendanceActivity extends AppCompatActivity {
    Spinner spinner;
    Context context;
    PhilipsAttendanceDB db;
    SharedPreferences preferences;
    ArrayAdapter<String> spn_adapter;
    String reason_cd = "0", user_name, visit_date,entry_allow = "0";
    ArrayList<NonWorkingReasonGetterSetter> nonWorkingReasonnewList;
    ArrayList<String> reasonlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cpm.phillipspc.R.layout.activity_attendance);
        declaration();
        prepareList();


        FloatingActionButton fab = (FloatingActionButton) findViewById(com.cpm.phillipspc.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    long id = db.saveAttendanceData(user_name, visit_date, reason_cd,entry_allow);
                    if (id > 0) {
                       /* Intent intent = new Intent(context, CheckoutActivity.class)
                                .putExtra(CommonString.KEY_STORE_CD, reason_cd)
                                .putExtra(CommonString.KEY_FROMSTORE, false);
                        startActivity(intent);
                        finish();*/
                    } else {
                        AlertandMessages.showToastMsg(context, "Attendance not Saved");
                    }
                }

            }
        });
    }

    boolean validate() {
        boolean isvalidate = true;
        if (reason_cd.equalsIgnoreCase("0") || reason_cd.equalsIgnoreCase("-1")) {
            AlertandMessages.showToastMsg(context, "Please select a reason");
            isvalidate = false;
        }
        return isvalidate;
    }


    void prepareList() {
        nonWorkingReasonnewList = db.getNonWorkingData(false);
        reasonlist = new ArrayList<>();
        reasonlist.clear();
        if (nonWorkingReasonnewList.size() > 0) {
            reasonlist = new ArrayList<String>();
            for (int i = 0; i < nonWorkingReasonnewList.size(); i++) {
                reasonlist.add(nonWorkingReasonnewList.get(i).getReason().get(0));
            }
        }
        spn_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, reasonlist);
        spinner.setAdapter(spn_adapter);
    }


    void declaration() {
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(com.cpm.phillipspc.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner = (Spinner) findViewById(com.cpm.phillipspc.R.id.attendance_spn);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = preferences.getString(CommonString.KEY_USERNAME, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        db = new PhilipsAttendanceDB(context);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                reason_cd = nonWorkingReasonnewList.get(position).getReason_cd().get(0);
                entry_allow = nonWorkingReasonnewList.get(position).getEntry_allow().get(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

}
