package com.cpm.phillips.getterSetter;

import java.util.ArrayList;

/**
 * Created by deepakp on 12/27/2017.
 */

public class VisitorLoginGetterSetter {

    public ArrayList<String> getEMP_CD() {
        return EMP_CD;
    }

    public void setEMP_CD(String EMP_CD) {
        this.EMP_CD.add(EMP_CD);
    }

    public ArrayList<String> getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME.add(NAME);
    }

    public ArrayList<String> getDESIGNATION() {
        return DESIGNATION;
    }

    public void setDESIGNATION(String DESIGNATION) {
        this.DESIGNATION.add(DESIGNATION);
    }

    public String getTable_VisitorLogin() {
        return table_VisitorLogin;
    }

    public void setTable_VisitorLogin(String table_VisitorLogin) {
        this.table_VisitorLogin = table_VisitorLogin;
    }

    String table_VisitorLogin;
    ArrayList<String> EMP_CD = new ArrayList<>();
    ArrayList<String> NAME = new ArrayList<>();
    ArrayList<String> DESIGNATION = new ArrayList<>();
}
