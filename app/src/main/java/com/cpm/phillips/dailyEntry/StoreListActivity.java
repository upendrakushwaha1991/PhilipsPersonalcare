package com.cpm.phillips.dailyEntry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cpm.phillips.R;
import com.cpm.phillips.constant.AlertandMessages;
import com.cpm.phillips.constant.CommonString;
import com.cpm.phillips.database.PhilipsAttendanceDB;
import com.cpm.phillips.download.DownloadActivity;
import com.cpm.phillips.getterSetter.CoverageBean;
import com.cpm.phillips.getterSetter.CustomerInfoGetterSetter;
import com.cpm.phillips.getterSetter.JCPMasterGetterSetter;
import com.cpm.phillips.getterSetter.SkuGetterSetter;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

public class StoreListActivity extends AppCompatActivity {
    Context context;
    RecyclerView recyclerView;
    Intent storeImageIntent;
    PhilipsAttendanceDB db;
    private String date, username,store_cd;
    ArrayList<JCPMasterGetterSetter> storelist;
    ArrayList<CoverageBean> coverage;
    StoreListAdapter adapter;
    LinearLayout storelist_ll, no_data_ll;
    FloatingActionButton fab;
    ArrayList<SkuGetterSetter> skuList;
    ArrayList<CustomerInfoGetterSetter> cusInfoList;

    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        declaration();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareList();
    }

    void prepareList() {
        db.open();
        storelist = db.getStoreData(date, username);
        coverage = db.getCoverageData(date, username);
        skuList = db.getSkuData(date,store_cd);
        cusInfoList = db.getCustomerInfoDetails(date,store_cd);

        if (storelist.size() > 0) {
            adapter = new StoreListAdapter(context, storelist);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            storelist_ll.setVisibility(View.VISIBLE);
            no_data_ll.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);

        } else {
            no_data_ll.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            storelist_ll.setVisibility(View.GONE);
        }
    }

    class StoreListAdapter extends RecyclerView.Adapter<MyViewHolder> {

        LayoutInflater inflater;
        ArrayList<JCPMasterGetterSetter> list;

        StoreListAdapter(Context context, ArrayList<JCPMasterGetterSetter> list) {
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_add_store_list_item_view, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final JCPMasterGetterSetter jcpMasterGetterSetter = list.get(position);
            int storeCd = 0;
            if (coverage.size() > 0) {
                for (int i = 0; i < coverage.size(); i++) {
                    if (Integer.parseInt(jcpMasterGetterSetter.getSTORE_CD().get(0)) == Integer.parseInt(coverage.get(i).getStoreId())) {
                        storeCd = Integer.parseInt(coverage.get(i).getStoreId());
                        break;
                    }
                }
            }

            //if (db.getCoverageStatus(storeCd).getStatus().equalsIgnoreCase("U")) {
            if (jcpMasterGetterSetter.getUPLOAD_STATUS().get(0).equalsIgnoreCase("U")) {
                holder.img_storeImage.setImageDrawable(getResources().getDrawable(R.drawable.ticku));
                holder.img_storeImage.setVisibility(View.VISIBLE);
            } else if (jcpMasterGetterSetter.getCHECKOUT_STATUS().get(0).equalsIgnoreCase(CommonString.KEY_C)) {
                holder.img_storeImage.setImageDrawable(getResources().getDrawable(R.drawable.tick_c));
                holder.img_storeImage.setVisibility(View.VISIBLE);
                holder.checkoutbtn.setVisibility(View.GONE);
            } else if (jcpMasterGetterSetter.getUPLOAD_STATUS().get(0).equalsIgnoreCase(CommonString.KEY_L)) {
                holder.img_storeImage.setImageDrawable(getResources().getDrawable(R.drawable.tickl));
                holder.img_storeImage.setVisibility(View.VISIBLE);
            }

            if (db.getCoverageStatus(storeCd).getStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                // holder.img_storeImage.setVisibility(View.INVISIBLE);
                holder.img_storeImage.setImageDrawable(getResources().getDrawable(R.drawable.checkin_ico));
                holder.img_storeImage.setVisibility(View.VISIBLE);
                holder.checkoutbtn.setVisibility(View.GONE);

            } else  if (db.getCoverageStatus(storeCd).getStatus().equalsIgnoreCase(CommonString.KEY_VALID)){
                holder.checkoutbtn.setVisibility(View.VISIBLE);
                holder.img_storeImage.setVisibility(View.GONE);
            }


            holder.textView.setText(jcpMasterGetterSetter.getSTORENAME().get(0));
            holder.checkoutbtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are you sure you want to Checkout ?")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog, int id) {
                                                    Intent i = new Intent(context, StoreImageActivity.class);
                                                    i.putExtra(CommonString.TAG_FOR, CommonString.KEY_OUT_TIME);
                                                    i.putExtra(CommonString.TAG_OBJECT, jcpMasterGetterSetter);
                                                    startActivity(i);

                                                }
                                            })
                                    .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();

                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                        Log.e("error", e.toString());
                    }

                }
            });


            holder.storelist_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int store_id = Integer.parseInt(jcpMasterGetterSetter.getSTORE_CD().get(0));
                    String username = (jcpMasterGetterSetter.getUSERNAME().get(0));
                    String status = db.getCoverageStatus(store_id).getStatus();

                    JCPMasterGetterSetter jcpgetset = db.getJCPStatus(store_id, username);

                    if (jcpgetset != null && jcpgetset.getUPLOAD_STATUS().get(0).equalsIgnoreCase(CommonString.KEY_U)) {
                        AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_store_already_done));
                    } else if (status.equalsIgnoreCase(CommonString.KEY_C)) {
                        AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_store_already_checkout));
                    } else if (status.equalsIgnoreCase(CommonString.KEY_P)) {
                        AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_store_again_uploaded));
                    } else if (status.equalsIgnoreCase(CommonString.KEY_L)) {
                        boolean isVisitlater = false;
                        for (int i = 0; i < coverage.size(); i++) {
                            if (store_id == Integer.parseInt(coverage.get(i).getStoreId())) {
                                if (coverage.get(i).getReasonid().equalsIgnoreCase("11")
                                        || coverage.get(i).getReason().equalsIgnoreCase("Visit Later")) {
                                    isVisitlater = true;
                                    break;
                                }
                            }
                        }
                        if (isVisitlater) {

                            boolean entry_flag = true;
                            for (int j = 0; j < storelist.size(); j++) {
                                if (status.equalsIgnoreCase(CommonString.KEY_CHECK_IN)|| status.equalsIgnoreCase(CommonString.KEY_VALID)) {

                                    if (store_id != Integer.parseInt(storelist.get(j).getSTORE_CD().get(0))) {
                                        entry_flag = false;
                                        break;

                                    } else {
                                        break;
                                    }
                                }
                            }

                            if (entry_flag) {
                                showMyDialog(jcpMasterGetterSetter);
                            } else {
                                AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_checkout_current));
                            }
                        } else {
                            AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_already_store_closed));

                        }

                    } else {
                        boolean entry_flag = true;
                        for (int j = 0; j < storelist.size(); j++) {
                            String status2 = db.getCoverageStatus(Integer.parseInt(storelist.get(j).getSTORE_CD().get(0))).getStatus();
                            if (status2.equalsIgnoreCase(CommonString.KEY_CHECK_IN) || status2.equalsIgnoreCase(CommonString.KEY_VALID)) {
                                if (store_id != Integer.parseInt(storelist.get(j).getSTORE_CD().get(0))) {
                                    entry_flag = false;
                                    break;
                                } else {
                                    break;
                                }
                            }
                        }

                        if (entry_flag) {
                            showMyDialog(jcpMasterGetterSetter);
                        } else {
                            AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_checkout_current));

                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    void showMyDialog(final JCPMasterGetterSetter as) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    int coverage_id = 0;
                    String checkout_status = "";
                    boolean flag = true;

                    editor = preferences.edit();
                    editor.putString(CommonString.KEY_STORE_CD, as.getSTORE_CD().get(0));
                    editor.commit();

                    dialog.cancel();
                    if (coverage.size() > 0) {
                        for (int i = 0; i < coverage.size(); i++) {
                            if (as.getSTORE_CD().get(0).equals(coverage.get(i).getStoreId())) {
                                //date = preferences.getString(CommonString.KEY_DATE, null);
                                flag = false;
                                break;
                            }
                        }
                    }

                    if(flag == true){
                        startActivity(storeImageIntent.putExtra(CommonString.TAG_FOR, CommonString.KEY_IN_TIME).putExtra(CommonString.TAG_OBJECT, as));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }else{

                        Intent storeEntry = new Intent(StoreListActivity.this,StoreEntryActivity.class);
                        startActivity(storeEntry);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }

                } else if (checkedId == R.id.no) {
                    dialog.cancel();
                    int coverage_id = 0;
                    String checkout_status = "";
                    if (coverage.size() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                        builder.setMessage(CommonString.DATA_DELETE_ALERT_MESSAGE)
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                               db.deleteSpecificTables(String.valueOf( as.getSTORE_CD().get(0)));
                                                Intent in = new Intent(StoreListActivity.this, NonWorkingActivity.class)
                                                        .putExtra(CommonString.KEY_STORE_CD, as.getSTORE_CD().get(0));
                                                startActivity(in);
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();

                        alert.show();
                    } else {
                        Intent in = new Intent(StoreListActivity.this, NonWorkingActivity.class)
                                .putExtra(CommonString.KEY_STORE_CD, as.getSTORE_CD().get(0));
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    }
                    //finish();
                }
            }
        });

        dialog.show();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout storelist_ll;
        Button checkoutbtn;
        ImageView img_storeImage;


        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.txt_storeName);
            checkoutbtn = view.findViewById(R.id.checkoutbtn);
            storelist_ll = view.findViewById(R.id.storelist_ll);
            img_storeImage = view.findViewById(R.id.img_storeImage);
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
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    void declaration() {
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        storelist_ll = (LinearLayout) findViewById(R.id.storelist);
        no_data_ll = (LinearLayout) findViewById(R.id.no_data_lay);
        storeImageIntent = new Intent(context, StoreImageActivity.class);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);

        db = new PhilipsAttendanceDB(context);
        db.open();

        fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), DownloadActivity.class);
                in.putExtra("flag_status","2");
                startActivity(in);
                finish();
            }
        });
    }

}
