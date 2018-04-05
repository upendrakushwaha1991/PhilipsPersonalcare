package com.cpm.phillips.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.phillips.R;
import com.cpm.phillips.constant.AlertandMessages;
import com.cpm.phillips.constant.CommonString;
import com.cpm.phillips.dailyEntry.ClosingStockActivity;
import com.cpm.phillips.dailyEntry.CustomerInfoActivity;
import com.cpm.phillips.dailyEntry.MidDayStockActivity;
import com.cpm.phillips.dailyEntry.OpeningStockingActivity;
import com.cpm.phillips.database.PhilipsAttendanceDB;
import com.cpm.phillips.getterSetter.NavMenuItemGetterSetter;
import com.cpm.phillips.getterSetter.SkuGetterSetter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ItemValueAdapter extends RecyclerView.Adapter<ItemValueAdapter.ViewHolder> {

    ArrayList<SkuGetterSetter> skuList;
    private SharedPreferences preferences;
    String date, username, store_cd;
    List<NavMenuItemGetterSetter> data = Collections.emptyList();
    private ArrayList<SkuGetterSetter> skuGetterSetter;
    Context context;
    LayoutInflater inflator;
    PhilipsAttendanceDB db;


    public ItemValueAdapter(Context applicationContext, List<NavMenuItemGetterSetter> getdata) {
        this.data = getdata;
        this.context = applicationContext;
        inflator = LayoutInflater.from(context);

        db = new PhilipsAttendanceDB(context);
        db.open();

        skuGetterSetter = db.getSkuMasterData();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        skuList = db.getSkuData(date, store_cd);

    }

    @Override
    public ItemValueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.custom_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemValueAdapter.ViewHolder holder, int position) {

        final NavMenuItemGetterSetter current = data.get(position);
        holder.image.setImageResource(current.getIconImg());
        holder.txtDyamic.setText(current.getIconName());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current.getIconImg() == R.drawable.opening_stock || current.getIconImg() == R.drawable.opening_stock_done ||  current.getIconImg() == R.drawable.opening_stock_grey){
                    if(skuGetterSetter.size() > 0){
                        if(skuList.size()>0){
                            if(skuList.get(0).getMID_DAT_STOCK_SKU_QUY().equalsIgnoreCase("") && skuList.get(0).getCLOSING_STOCK_SKU_QUY().equalsIgnoreCase("")) {
                                goToOpeningStockActivity();
                            }else{
                                AlertandMessages.showSnackbarMsg(context, "Opening Stock Can't be change");
                            }
                        }else{
                            goToOpeningStockActivity();
                        }
                    }
                    else
                    {
                        AlertandMessages.showSnackbarMsg(context, "No Opening Stock Found");
                    }
               }

               if(current.getIconImg() == R.drawable.mid_day_stock || current.getIconImg() == R.drawable.mid_day_stock_done || current.getIconImg() == R.drawable.midday_stock_grey)
               {
                    if (skuGetterSetter.size() > 0) {
                        if(skuList.size()>0) {
                            if (!skuList.get(0).getOPENING_STOCK_SKU_QUY().equalsIgnoreCase("")) {
                                if (skuList.get(0).getCLOSING_STOCK_SKU_QUY().equalsIgnoreCase("")) {
                                    goToMidDayStockActivity();
                                } else {
                                    AlertandMessages.showSnackbarMsg(context, "Mid Day Stock Can't be change");
                                }
                            } else {
                                AlertandMessages.showSnackbarMsg(context, "Fill Opening Stock Data First");
                            }
                        }else{
                            AlertandMessages.showSnackbarMsg(context, "Fill Opening Stock Data First");
                        }
                    }else{
                        AlertandMessages.showSnackbarMsg(context, "No Mid Day Stock Found");
                    }
                }

                else if(current.getIconImg() == R.drawable.closing_stock || current.getIconImg() == R.drawable.closing_stock_done || current.getIconImg() == R.drawable.closing_stock_grey){

                   if (skuGetterSetter.size() > 0)
                   {
                       if(skuList.size()>0) {
                           if (!skuList.get(0).getOPENING_STOCK_SKU_QUY().equalsIgnoreCase("")) {
                               if (!skuList.get(0).getMID_DAT_STOCK_SKU_QUY().equalsIgnoreCase("")) {
                                   Intent skuIntent = new Intent(context, ClosingStockActivity.class);
                                   context.startActivity(skuIntent);
                               } else {
                                   AlertandMessages.showSnackbarMsg(context, "Fill Mid Day Stock Data First");
                               }
                           } else {
                               AlertandMessages.showSnackbarMsg(context, "Fill Opening Stock Data First");
                           }
                       }else{
                           AlertandMessages.showSnackbarMsg(context, "Fill Opening Stock Data First");
                       }
                   }
                   else
                   {
                       AlertandMessages.showSnackbarMsg(context, "No Closing Stock Found");
                   }
               }
               else if(current.getIconImg() == R.drawable.customer_info || current.getIconImg() == R.drawable.customer_info_done){
                    Intent skuIntent = new Intent(context, CustomerInfoActivity.class);
                    context.startActivity(skuIntent);
                }
            }
        });
    }

    private void goToMidDayStockActivity() {
        Intent skuIntent = new Intent(context, MidDayStockActivity.class);
        context.startActivity(skuIntent);
    }


    private void goToOpeningStockActivity() {
        Intent openingStockIntent = new Intent(context, OpeningStockingActivity.class);
        context.startActivity(openingStockIntent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView txtDyamic;

        public ViewHolder(View itemView) {
            super(itemView);
            image =  itemView.findViewById(R.id.list_iconEntry);
            txtDyamic  = itemView.findViewById(R.id.txtDynamic);
        }
    }
}
