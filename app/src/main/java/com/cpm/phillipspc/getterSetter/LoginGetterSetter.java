package com.cpm.phillipspc.getterSetter;

/**
 * Created by deepakp on 2/1/2017.
 */

public class LoginGetterSetter {
    private String result, VERSION, PATH, DATE, RIGHTNAME;

    public String getRIGHTNAME() {
        return RIGHTNAME;
    }

    public void setRIGHTNAME(String rIGHTNAME) {
        RIGHTNAME = rIGHTNAME;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getVERSION() {
        return VERSION;
    }

    public void setVERSION(String vERSION) {
        VERSION = vERSION;
    }

    public String getPATH() {
        return PATH;
    }

    public void setPATH(String pATH) {
        PATH = pATH;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String dATE) {
        DATE = dATE;
    }
}
