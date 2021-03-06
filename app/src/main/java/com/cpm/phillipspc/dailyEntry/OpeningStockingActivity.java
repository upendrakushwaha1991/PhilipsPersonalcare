package com.cpm.phillipspc.dailyEntry;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.cpm.phillipspc.R;
import com.cpm.phillipspc.adapter.skuMasterListAdapter;
import com.cpm.phillipspc.constant.AlertandMessages;
import com.cpm.phillipspc.constant.CommonString;
import com.cpm.phillipspc.database.PhilipsAttendanceDB;
import com.cpm.phillipspc.getterSetter.SkuGetterSetter;

import java.util.ArrayList;

public class OpeningStockingActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private skuMasterListAdapter skuListAdapter;
    private ArrayList<SkuGetterSetter> skuGetterSetter;
    PhilipsAttendanceDB db;
    private FloatingActionButton saveBtn;
    private boolean quy_flag = true;
    private String visit_date, username,store_cd;
    SharedPreferences preferences;
    private ArrayList<SkuGetterSetter> skuList;
    private Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_stock);
        declaration();
    }

    private void declaration()
    {
        saveBtn = findViewById(R.id.fab_btn);
        updateBtn = findViewById(R.id.btn_update);
        mRecyclerView = findViewById(R.id.sku_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Opening Stock");
        db = new PhilipsAttendanceDB(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        db.open();


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.clearFocus();
                skuListAdapter.notifyDataSetChanged();
                if (validateData(skuList)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OpeningStockingActivity.this);
                    builder.setMessage("Are you sure you want to update data ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    db.open();
                                    db.insertSkuDetails(skuList,visit_date,username,store_cd);

                                    AlertandMessages.showSnackbarMsg(OpeningStockingActivity.this,"Data updated successfully");
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }


            private boolean validateData(ArrayList<SkuGetterSetter> list) {
                quy_flag = true;
                for(int i=0;i<list.size();i++){
                    if(list.get(i).getOPENING_STOCK_SKU_QUY().equals("")){
                        quy_flag = false;
                        AlertandMessages.showSnackbarMsg(OpeningStockingActivity.this,"Please fill the quantity of " +list.get(i).getSKU().get(0).toString());
                        break;
                    }
                }
                return quy_flag;
            }

        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.clearFocus();
                skuListAdapter.notifyDataSetChanged();
                if (validateData(skuGetterSetter)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OpeningStockingActivity.this);
                    builder.setMessage("Are you sure you want to save data ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    db.open();
                                    db.insertSkuDetails(skuGetterSetter,visit_date,username,store_cd);

                                    AlertandMessages.showSnackbarMsg(OpeningStockingActivity.this,"Data has been saved");
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            private boolean validateData(ArrayList<SkuGetterSetter> skuGetterSetter) {
                quy_flag = true;
                for(int i=0;i<skuGetterSetter.size();i++){
                    if(skuGetterSetter.get(i).getOPENING_STOCK_SKU_QUY().equals("")){
                        quy_flag = false;
                        AlertandMessages.showSnackbarMsg(OpeningStockingActivity.this,"Please fill the quantity of " +skuGetterSetter.get(i).getSKU().get(0).toString());
                        break;
                    }
                }
                return quy_flag;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setOpeningStockData();
    }

    private void setOpeningStockData() {
        skuList = db.getSkuData(visit_date,store_cd);
        skuGetterSetter = db.getSkuMasterData();

        if(skuList.size()> 0){
            if(!skuList.get(0).getOPENING_STOCK_SKU_QUY().equalsIgnoreCase("")) {
                skuListAdapter = new skuMasterListAdapter(getApplicationContext(), skuList, quy_flag, "1");
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(skuListAdapter);
                saveBtn.setVisibility(View.GONE);
                updateBtn.setVisibility(View.VISIBLE);
            }
        }else{
            if(skuGetterSetter.size() > 0){
                skuListAdapter = new skuMasterListAdapter(getApplicationContext(),skuGetterSetter,quy_flag, "1");
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(skuListAdapter);
            }
        }
    }
}
