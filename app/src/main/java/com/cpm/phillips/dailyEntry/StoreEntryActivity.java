package com.cpm.phillips.dailyEntry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.cpm.phillips.R;
import com.cpm.phillips.adapter.ItemValueAdapter;
import com.cpm.phillips.constant.CommonString;
import com.cpm.phillips.database.PhilipsAttendanceDB;
import com.cpm.phillips.getterSetter.CustomerInfoGetterSetter;
import com.cpm.phillips.getterSetter.NavMenuItemGetterSetter;
import com.cpm.phillips.getterSetter.SkuGetterSetter;

import java.util.ArrayList;
import java.util.List;

public class StoreEntryActivity extends AppCompatActivity {
    Context context;
    String date, username, store_cd;
    ArrayList<SkuGetterSetter> skuList;
    ArrayList<CustomerInfoGetterSetter> cusInfoList;
    PhilipsAttendanceDB db;
    private SharedPreferences preferences;
    private ArrayList<SkuGetterSetter> skuGetterSetter;
    ItemValueAdapter adapter;
    RecyclerView recyclerView;
    int customerImg, ClosingStock, OpeningStockImg, MidDayStockImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_entry);
        declaration();
    }

    @Override
    protected void onResume() {
        super.onResume();
        skuList = db.getSkuData(date, store_cd);
        skuGetterSetter = db.getSkuMasterData();
        cusInfoList = db.getCustomerInfoDetails(date, store_cd);

        recyclerView = findViewById(R.id.drawer_layout_recycle);
        adapter = new ItemValueAdapter(this, getdata());
        recyclerView.setHasFixedSize(true);
        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recyclerView.setAdapter(adapter);
        db.open();
        if (validatedataforcheckout()) {
            db.updateCoverageValidData(CommonString.KEY_VALID, store_cd, date);
        }else{
            db.updateCoverageValidData(CommonString.KEY_CHECK_IN, store_cd, date);
        }
    }

    private List<NavMenuItemGetterSetter> getdata() {
        List<NavMenuItemGetterSetter> data = new ArrayList<>();

        if(skuGetterSetter.size()>0) {
            if (skuList.size() > 0) {
                if (!skuList.get(0).getOPENING_STOCK_SKU_QUY().equalsIgnoreCase("")) {
                    OpeningStockImg = R.drawable.opening_stock_done;
                } else {
                    OpeningStockImg = R.drawable.opening_stock;
                }
            } else {
                OpeningStockImg = R.drawable.opening_stock;
            }
        }else{
            OpeningStockImg = R.drawable.opening_stock_grey;
        }

        if(skuGetterSetter.size()>0) {
            if (skuList.size() > 0) {
                if (!skuList.get(0).getMID_DAT_STOCK_SKU_QUY().equalsIgnoreCase("")) {
                    MidDayStockImg = R.drawable.mid_day_stock_done;
                } else {
                    MidDayStockImg = R.drawable.mid_day_stock;
                }
            } else {
                MidDayStockImg = R.drawable.mid_day_stock;
            }
        } else {
            MidDayStockImg = R.drawable.midday_stock_grey;
        }

        if(skuGetterSetter.size()>0) {
            if (skuList.size() > 0) {
                if (!skuList.get(0).getCLOSING_STOCK_SKU_QUY().equalsIgnoreCase("")) {
                    ClosingStock = R.drawable.closing_stock_done;
                } else {
                    ClosingStock = R.drawable.closing_stock;
                }
            } else {
                ClosingStock = R.drawable.closing_stock;
            }
        } else {
            ClosingStock = R.drawable.closing_stock_grey;
        }


        if (cusInfoList.size() > 0) {
            customerImg = R.drawable.customer_info_done;
        } else {
            customerImg = R.drawable.customer_info;
        }

        int img[]  = {customerImg, OpeningStockImg,MidDayStockImg,ClosingStock};
        String name[] = {"Customer Info", "Opening Stock","Mid Day Stock","Closing Stock"};
        for (int i = 0; i < img.length; i++) {
            NavMenuItemGetterSetter recData = new NavMenuItemGetterSetter();
            recData.setIconImg(img[i]);
            recData.setIconName(name[i]);
            data.add(recData);
        }

        return data;
    }

    private void declaration() {
        context = this;
        getSupportActionBar().setTitle("Store Entry");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);

        db = new PhilipsAttendanceDB(context);
        db.open();

    }

    private boolean validatedataforcheckout() {
        boolean status = false;
        if(skuList.size()>0) {
            if (!skuList.get(0).getCLOSING_STOCK_SKU_QUY().equalsIgnoreCase("") && cusInfoList.size() > 0) {
                status = true;
                return status;
            }
        }
        return status;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StoreEntryActivity.this.finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
