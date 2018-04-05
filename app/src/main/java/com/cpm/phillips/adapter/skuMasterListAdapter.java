package com.cpm.phillips.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cpm.phillips.R;
import com.cpm.phillips.constant.AlertandMessages;
import com.cpm.phillips.constant.CommonString;
import com.cpm.phillips.database.PhilipsAttendanceDB;
import com.cpm.phillips.getterSetter.SkuGetterSetter;

import java.util.ArrayList;

/**
 * Created by neerajg on 2/16/2018.
 */

public class skuMasterListAdapter extends RecyclerView.Adapter<skuMasterListAdapter.ViewHolder> {

    ArrayList<SkuGetterSetter> skuList;
    private SharedPreferences preferences;
    String date, username, store_cd;
    PhilipsAttendanceDB db;
    Context context;
    ArrayList<SkuGetterSetter> skuGetterSetters;
    boolean flag;
    String stockType = "";

    public skuMasterListAdapter(Context applicationContext, ArrayList<SkuGetterSetter> skuGetterSetter, boolean quy_flag, String stockType) {
        this.context = applicationContext;
        this.skuGetterSetters = skuGetterSetter;
        this.flag = quy_flag;
        this.stockType = stockType;

        db = new PhilipsAttendanceDB(context);
        db.open();

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        skuList = db.getSkuData(date, store_cd);
    }

    @Override
    public skuMasterListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.sku_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final skuMasterListAdapter.ViewHolder holder, final int position) {
        holder.sku_name.setText(skuGetterSetters.get(position).getSKU().get(0).toString());

        if(stockType.equalsIgnoreCase("3")){
            holder.openingMidDayStocklayout.setVisibility(View.VISIBLE);
            holder.opening_stock.setText(skuList.get(position).getOPENING_STOCK_SKU_QUY());
            holder.mid_day_stock.setText(skuList.get(position).getMID_DAT_STOCK_SKU_QUY());
        }else{
            holder.openingMidDayStocklayout.setVisibility(View.GONE);
        }

        holder.sku_quy.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String value = holder.sku_quy.getText().toString().replaceFirst("^0+(?!$)", "");
                    if(stockType.equalsIgnoreCase("1")){
                        if(value.equals("")){
                            skuGetterSetters.get(position).setOPENING_STOCK_SKU_QUY("");
                            holder.sku_quy.setText("");
                        }
                        else{
                            holder.sku_quy.setText(value);
                            skuGetterSetters.get(position).setOPENING_STOCK_SKU_QUY(value);
                        }
                    }else if(stockType.equalsIgnoreCase("2")){
                        if(value.equals("")){
                            skuGetterSetters.get(position).setMID_DAT_STOCK_SKU_QUY("");
                            holder.sku_quy.setText("");
                        }
                        else{
                            holder.sku_quy.setText(value);
                            skuGetterSetters.get(position).setMID_DAT_STOCK_SKU_QUY(value);
                        }
                    }else if(stockType.equalsIgnoreCase("3")){
                        if(value.equals("")){
                            skuGetterSetters.get(position).setCLOSING_STOCK_SKU_QUY("");
                            holder.sku_quy.setText("");
                        }
                        else{
                            int opening_stock  = Integer.parseInt(skuList.get(position).getOPENING_STOCK_SKU_QUY());
                            int mid_day_stock  = Integer.parseInt(skuList.get(position).getMID_DAT_STOCK_SKU_QUY());
                            int closing_stock  = Integer.parseInt(value);
                            int stock_value    = opening_stock + mid_day_stock;

                            if(closing_stock > stock_value){
                                skuGetterSetters.get(position).setCLOSING_STOCK_SKU_QUY("");
                                holder.sku_quy.setText("");
                                AlertandMessages.showSnackbarMsg(holder.openingMidDayStocklayout,"Closing stock and greater then sum of opening stock and mid day stock");
                            }else{
                                holder.sku_quy.setText(value);
                                skuGetterSetters.get(position).setCLOSING_STOCK_SKU_QUY(value);
                            }
                        }
                    }
                }
            }
        });

        if(stockType.equalsIgnoreCase("1")){
            holder.sku_quy.setText(skuGetterSetters.get(position).getOPENING_STOCK_SKU_QUY());
            holder.sku_quy.setId(position);
        }else if (stockType.equalsIgnoreCase("2")){
            holder.sku_quy.setText(skuGetterSetters.get(position).getMID_DAT_STOCK_SKU_QUY());
            holder.sku_quy.setId(position);
        }else if (stockType.equalsIgnoreCase("3")){
            holder.sku_quy.setText(skuGetterSetters.get(position).getCLOSING_STOCK_SKU_QUY());
            holder.sku_quy.setId(position);
        }
    }


    @Override
    public int getItemCount() {
        return skuGetterSetters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sku_name,opening_stock,mid_day_stock;
        EditText sku_quy;
        CardView skuQuyView;
        LinearLayout openingMidDayStocklayout;

        public ViewHolder(View itemView) {
            super(itemView);
            sku_name = itemView.findViewById(R.id.skuName);
            skuQuyView = itemView.findViewById(R.id.sku_quy_view);
            sku_quy  = itemView.findViewById(R.id.skuQuy);
            openingMidDayStocklayout = itemView.findViewById(R.id.opening_mid_day_stock_layout);
            opening_stock = itemView.findViewById(R.id.openingStockTxt);
            mid_day_stock = itemView.findViewById(R.id.midDayStockTxt);
        }
    }
}
