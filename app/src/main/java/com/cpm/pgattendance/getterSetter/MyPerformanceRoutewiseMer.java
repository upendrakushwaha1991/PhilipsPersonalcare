
package com.cpm.pgattendance.getterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyPerformanceRoutewiseMer {

    @SerializedName("Route")
    @Expose
    private String route;
    @SerializedName("Merchandise")
    @Expose
    private Integer merchandise;
    @SerializedName("PSS")
    @Expose
    private Integer pSS;

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Integer getMerchandise() {
        return merchandise;
    }

    public void setMerchandise(Integer merchandise) {
        this.merchandise = merchandise;
    }

    public Integer getPSS() {
        return pSS;
    }

    public void setPSS(Integer pSS) {
        this.pSS = pSS;
    }

}
