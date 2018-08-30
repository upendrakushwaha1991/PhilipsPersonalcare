package com.cpm.phillipspc.getterSetter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by neerajg on 2/16/2018.
 */

public class SkuGetterSetter {

    String table_SKU_MASTER;
    ArrayList<String> SKU_CD = new ArrayList<>();
    ArrayList<String> SKU = new ArrayList<>();
    String OPENING_STOCK_SKU_QUY = "";
    String MID_DAT_STOCK_SKU_QUY = "";
    String CLOSING_STOCK_SKU_QUY = "";
    String VISITED_DATE;

    public String getOPENING_STOCK_SKU_QUY() {
        return OPENING_STOCK_SKU_QUY;
    }

    public void setOPENING_STOCK_SKU_QUY(String OPENING_STOCK_SKU_QUY) {
        this.OPENING_STOCK_SKU_QUY = OPENING_STOCK_SKU_QUY;
    }

    public String getMID_DAT_STOCK_SKU_QUY() {
        return MID_DAT_STOCK_SKU_QUY;
    }

    public void setMID_DAT_STOCK_SKU_QUY(String MID_DAT_STOCK_SKU_QUY) {
        this.MID_DAT_STOCK_SKU_QUY = MID_DAT_STOCK_SKU_QUY;
    }

    public String getCLOSING_STOCK_SKU_QUY() {
        return CLOSING_STOCK_SKU_QUY;
    }

    public void setCLOSING_STOCK_SKU_QUY(String CLOSING_STOCK_SKU_QUY) {
        this.CLOSING_STOCK_SKU_QUY = CLOSING_STOCK_SKU_QUY;
    }

    public String getVISITED_DATE() {
        return VISITED_DATE;
    }

    public void setVISITED_DATE(String VISITED_DATE) {
        this.VISITED_DATE = VISITED_DATE;
    }


    public String getTable_SKU_MASTER() {
        return table_SKU_MASTER;
    }

    public void setTable_SKU_MASTER(String table_SKU_MASTER) {
        this.table_SKU_MASTER = table_SKU_MASTER;
    }

    public ArrayList<String> getSKU_CD() {
        return SKU_CD;
    }

    public void setSKU_CD(String SKU_CD) {
        this.SKU_CD.add(SKU_CD);
    }

    public ArrayList<String> getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU.add(SKU);
    }

    @Override
    public String toString() {
        return "SkuGetterSetter{" +
                "table_SKU_MASTER='" + table_SKU_MASTER + '\'' +
                ", SKU_CD=" + SKU_CD +
                ", SKU=" + SKU +
                '}';
    }
}
