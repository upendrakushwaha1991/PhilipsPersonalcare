
package com.cpm.pgattendance.getterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyPerformanceMer {

    @SerializedName("Emp_Id")
    @Expose
    private Integer empId;
    @SerializedName("Period")
    @Expose
    private String period;
    @SerializedName("Attendance")
    @Expose
    private Integer attendance;
    @SerializedName("Merchandise")
    @Expose
    private Integer merchandise;
    @SerializedName("PSS")
    @Expose
    private Integer pSS;

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getAttendance() {
        return attendance;
    }

    public void setAttendance(Integer attendance) {
        this.attendance = attendance;
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
