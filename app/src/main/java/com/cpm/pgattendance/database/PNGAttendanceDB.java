package com.cpm.pgattendance.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cpm.pgattendance.bean.TableBean;
import com.cpm.pgattendance.constant.CommonString;
import com.cpm.pgattendance.getterSetter.CoverageBean;
import com.cpm.pgattendance.getterSetter.JCPMasterGetterSetter;
import com.cpm.pgattendance.getterSetter.MyPerformanceMer;
import com.cpm.pgattendance.getterSetter.MyPerformanceRoutewiseMer;
import com.cpm.pgattendance.getterSetter.NonWorkingReasonGetterSetter;
import com.cpm.pgattendance.getterSetter.VisitorDetailGetterSetter;
import com.cpm.pgattendance.getterSetter.VisitorLoginGetterSetter;

import java.util.ArrayList;

/**
 * Created by deepakp on 12/20/2017.
 */

public class PNGAttendanceDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "PNG_ATTENDANCE_DB";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    Context context;

    public PNGAttendanceDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(CommonString.CREATE_TABLE_ATTENDANCE);
            db.execSQL(CommonString.CREATE_TABLE_COVERAGE_DATA);
            db.execSQL(CommonString.CREATE_TABLE_VISITOR_LOGIN);
            db.execSQL(TableBean.getTable_JCPMaster());
            db.execSQL(TableBean.getNonworkingtable());
            // db.execSQL(TableBean.getVisitorLogintable());
            //db.execSQL(TableBean.getQuestiontable());

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteAllTables() {
        // DELETING TABLES
        db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL("DROP TABLE IF EXISTS " + TableBean.getJourneyPlan());
    }

    public int createtable(String sqltext) {
        try {
            db.execSQL(sqltext);
            return 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public long saveAttendanceData(String username, String visit_date, String reason_cd, String entry_allow) {
        long id = 0;
        db.delete(CommonString.TABLE_ATTENDANCE, "USERNAME = '" + username + "' and VISITDATE = '" + visit_date + "'", null);
        try {
            ContentValues values = new ContentValues();

            values.put("USERNAME", username);
            values.put("VISITDATE", visit_date);
            values.put("REASON_CD", reason_cd);
            values.put("ENTRY_ALLOW", entry_allow);
            values.put("ATTENDANCE_STATUS", CommonString.KEY_Y);

            id = db.insert(CommonString.TABLE_ATTENDANCE, null, values);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return id;
    }

    public ArrayList<NonWorkingReasonGetterSetter> getNonWorkingData(boolean fromStore) {

        ArrayList<NonWorkingReasonGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            if (fromStore) {
              /*  dbcursor = db
                        .rawQuery(
                                "SELECT -1 AS REASON_CD,'Select' as REASON,'-1' as ENTRY_ALLOW,'-1' AS IMAGE_ALLOW,'-1' AS FOR_STORE,'-1' AS FOR_ATT union SELECT * FROM NON_WORKING_REASON_NEW WHERE FOR_STORE ='1'"
                                , null);*/

                dbcursor = db
                        .rawQuery(
                                "SELECT -1 AS REASON_CD,'Select' as REASON,'-1' as ENTRY_ALLOW,'-1' AS FOR_STORE,'-1' AS FOR_ATT union SELECT * FROM NON_WORKING_REASON WHERE ENTRY_ALLOW ='1'"
                                , null);

            } else {
                dbcursor = db
                        .rawQuery(
                                "SELECT -1 AS REASON_CD,'Select' as REASON,'-1' as ENTRY_ALLOW,'-1' AS FOR_STORE,'-1' AS FOR_ATT union SELECT * FROM NON_WORKING_REASON WHERE FOR_ATT ='1'"
                                , null);
            }

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonWorkingReasonGetterSetter sb = new NonWorkingReasonGetterSetter();

                    sb.setReason_cd(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("REASON_CD")));
                    sb.setReason(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("REASON")));
                    sb.setEntry_allow(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("ENTRY_ALLOW")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }

    public void insertJCPMasterData(JCPMasterGetterSetter data) {

        db.delete("JOURNEY_PLAN", null, null);
        ContentValues values = new ContentValues();

        try {
            for (int i = 0; i < data.getSTORE_CD().size(); i++) {
                values.put("STORE_CD", Integer.parseInt(data.getSTORE_CD().get(i)));
                values.put("EMP_CD", Integer.parseInt(data.getEMP_CD().get(i)));
                values.put("USERID", (data.getUSERNAME().get(i)));
                values.put("VISIT_DATE", (data.getVISIT_DATE().get(i)));
                values.put("KEYACCOUNT", (data.getKEYACCOUNT().get(i)));
                values.put("STORENAME", (data.getSTORENAME().get(i)));
                values.put("CITY", (data.getCITY().get(i)));
                values.put("STORETYPE", (data.getSTORETYPE().get(i)));
                values.put("UPLOAD_STATUS", (data.getUPLOAD_STATUS().get(i)));
                values.put("CHECKOUT_STATUS", (data.getCHECKOUT_STATUS().get(i)));

                db.insert("JOURNEY_PLAN", null, values);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void insertNonWorkingData(NonWorkingReasonGetterSetter data) {

        db.delete("NON_WORKING_REASON", null, null);
        ContentValues values = new ContentValues();

        try {

            for (int i = 0; i < data.getReason_cd().size(); i++) {

                values.put("REASON_CD", Integer.parseInt(data.getReason_cd().get(i)));
                values.put("REASON", data.getReason().get(i));
                values.put("FOR_ATT", data.getFOR_ATT().get(i));
                values.put("FOR_STORE", data.getFOR_STORE().get(i));
                values.put("ENTRY_ALLOW", data.getEntry_allow().get(i));

                db.insert("NON_WORKING_REASON", null, values);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void insertVisitorLoginData(VisitorLoginGetterSetter data) {

        db.delete("VISITOR_DETAIL", null, null);
        ContentValues values = new ContentValues();

        try {

            for (int i = 0; i < data.getEMP_CD().size(); i++) {

                values.put("EMP_CD", Integer.parseInt(data.getEMP_CD().get(i)));
                values.put("NAME", data.getNAME().get(i));
                values.put("DESIGNATION", data.getDESIGNATION().get(i));

                db.insert("VISITOR_DETAIL", null, values);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public ArrayList<JCPMasterGetterSetter> getStoreData(String date, String userid) {
        ArrayList<JCPMasterGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from JOURNEY_PLAN  " +
                    "where VISIT_DATE ='" + date + "' and USERID = '" + userid + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JCPMasterGetterSetter sb = new JCPMasterGetterSetter();

                    sb.setSTORE_CD((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORE_CD"))));
                    sb.setEMP_CD((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("EMP_CD"))));
                    sb.setUSERNAME(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("USERID")));
                    sb.setVISIT_DATE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("VISIT_DATE")));
                    sb.setKEYACCOUNT((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("KEYACCOUNT"))));
                    sb.setSTORENAME(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORENAME")));
                    sb.setCITY(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("CITY")));
                    sb.setSTORETYPE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORETYPE")));
                    sb.setUPLOAD_STATUS(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("UPLOAD_STATUS")));
                    sb.setCHECKOUT_STATUS(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("CHECKOUT_STATUS")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }

    public long InsertCoverageData(CoverageBean data) {
        ContentValues values = new ContentValues();
        try {
            values.put(CommonString.KEY_STORE_CD, data.getStoreId());
            values.put(CommonString.KEY_USER_ID, data.getUserId());
            values.put(CommonString.KEY_IN_TIME, data.getInTime());
            values.put(CommonString.KEY_OUT_TIME, data.getOutTime());
            values.put(CommonString.KEY_VISIT_DATE, data.getVisitDate());
            values.put(CommonString.KEY_LATITUDE, data.getLatitude());
            values.put(CommonString.KEY_LONGITUDE, data.getLongitude());
            values.put(CommonString.KEY_COVERAGE_STATUS, data.getStatus());
            values.put(CommonString.KEY_IN_TIME_IMAGE, data.getIntime_Image());
            values.put(CommonString.KEY_OUT_TIME_IMAGE, data.getOuttime_Image());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());

            return db.insert(CommonString.TABLE_COVERAGE_DATA, null, values);

        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }

    }

    public ArrayList<CoverageBean> getCoverageData(String visitdate, String username) {
        ArrayList<CoverageBean> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "='" + visitdate + "' and USER_ID = '" + username + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();

                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setUserId((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID))));
                    sb.setInTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)))));
                    sb.setOutTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)))));
                    sb.setVisitDate((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE))))));
                    sb.setLatitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)))));
                    sb.setLongitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)))));
                    sb.setStatus((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS))))));
                    sb.setIntime_Image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME_IMAGE))))));
                    sb.setOuttime_Image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME_IMAGE))))));
                    sb.setReasonid((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID))))));
                    sb.setReason((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON))))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;
    }

    public ArrayList<CoverageBean> getCoverageSpecificData(String visitdate, String username, String storecd) {
        ArrayList<CoverageBean> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "='" + visitdate + "' and USER_ID = '" + username + "' and STORE_CD = '" + storecd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();

                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setUserId((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID))));
                    sb.setInTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)))));
                    sb.setOutTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)))));
                    sb.setVisitDate((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE))))));
                    sb.setLatitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)))));
                    sb.setLongitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)))));
                    sb.setStatus((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS))))));
                    sb.setIntime_Image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME_IMAGE))))));
                    sb.setOuttime_Image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME_IMAGE))))));
                    sb.setReasonid((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID))))));
                    sb.setReason((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON))))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;
    }


    public void deleteSpecificTables(String coverage_id) {
        // DELETING TABLES
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_ID + "= '" + coverage_id + "'", null);
    }

    public JCPMasterGetterSetter getJCPStatus(int storecd, String username) {
        JCPMasterGetterSetter sb = new JCPMasterGetterSetter();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT UPLOAD_STATUS from JOURNEY_PLAN where "
                    + CommonString.KEY_STORE_CD + "='" + storecd + "' and USERID = '" + username + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setUPLOAD_STATUS((((dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS"))))));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return sb;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }


    public CoverageBean getCoverageStatus(int storecd) {
        CoverageBean sb = new CoverageBean();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT " + CommonString.KEY_COVERAGE_STATUS + " from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_STORE_CD + "='" + storecd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setStatus((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS))))));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return sb;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }


    public ArrayList<VisitorDetailGetterSetter> getVisitorData() {

        ArrayList<VisitorDetailGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db
                    .rawQuery(
                            "SELECT -1 AS EMP_CD,'Select Visitor' as NAME,'Select' as DESIGNATION union SELECT * FROM VISITOR_DETAIL"
                            , null);


            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    VisitorDetailGetterSetter sb = new VisitorDetailGetterSetter();

                    sb.setEmp_code(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("EMP_CD")));
                    sb.setName(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("NAME")));
                    sb.setDesignation(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("DESIGNATION")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            return list;
        }
        return list;
    }

    public void updateOutTimeVisitorLoginData(String out_time_image, String out_time, String emp_id) {

        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_OUT_TIME_IMAGE, out_time_image);
            values.put(CommonString.KEY_OUT_TIME, out_time);

            db.update(CommonString.TABLE_VISITOR_LOGIN, values,
                    CommonString.KEY_EMP_CD + "='" + emp_id + "'", null);

        } catch (Exception e) {

        }
    }

    public long updateCoverageData(CoverageBean coverageBean) {

        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_OUT_TIME_IMAGE, coverageBean.getOuttime_Image());
            values.put(CommonString.KEY_OUT_TIME, coverageBean.getOutTime());
            values.put(CommonString.KEY_COVERAGE_STATUS, coverageBean.getStatus());

            return db.update(CommonString.TABLE_COVERAGE_DATA, values,
                    CommonString.KEY_STORE_CD + "='" + coverageBean.getStoreId() + "' and " + CommonString.KEY_USER_ID + " = '" + coverageBean.getUserId() + "'", null);

        } catch (Exception e) {
            return 0;
        }
    }

    public long updateJcpStatusData(CoverageBean coverageBean) {

        try {
            ContentValues values = new ContentValues();
            values.put("UPLOAD_STATUS", coverageBean.getStatus());
            return db.update("JOURNEY_PLAN", values,
                    CommonString.KEY_STORE_CD + "='" + coverageBean.getStoreId() + "' and " + "USERID" + " = '" + coverageBean.getUserId() + "'", null);

        } catch (Exception e) {
            return 0;
        }
    }


    public void InsertVisitorLogindata(ArrayList<VisitorDetailGetterSetter> visitorLoginGetterSetter) {

        db.delete(CommonString.TABLE_VISITOR_LOGIN, null, null);

        ContentValues values = new ContentValues();

        try {

            for (int i = 0; i < visitorLoginGetterSetter.size(); i++) {

                values.put(CommonString.KEY_EMP_CD, visitorLoginGetterSetter.get(i).getEmpId());
                values.put(CommonString.KEY_NAME, visitorLoginGetterSetter.get(i).getName());
                values.put(CommonString.KEY_DESIGNATION, visitorLoginGetterSetter.get(i).getDesignation());
                values.put(CommonString.KEY_IN_TIME_IMAGE, visitorLoginGetterSetter.get(i).getIn_time_img());
                values.put(CommonString.KEY_OUT_TIME_IMAGE, visitorLoginGetterSetter.get(i).getOut_time_img());
                values.put(CommonString.KEY_EMP_CODE, visitorLoginGetterSetter.get(i).getEmp_code());
                values.put(CommonString.KEY_VISIT_DATE, visitorLoginGetterSetter.get(i).getVisit_date());
                values.put(CommonString.KEY_IN_TIME, visitorLoginGetterSetter.get(i).getIn_time());
                values.put(CommonString.KEY_OUT_TIME, visitorLoginGetterSetter.get(i).getOut_time());
                values.put(CommonString.KEY_UPLOADSTATUS, visitorLoginGetterSetter.get(i).getUpload_status());

                db.insert(CommonString.TABLE_VISITOR_LOGIN, null, values);

            }

        } catch (Exception ex) {

        }

    }


    public boolean isVistorDataExists(String emp_id) {

        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from TABLE_VISITOR_LOGIN where EMP_CD = '" + emp_id + "'"
                    , null);
            int count = 0;
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {

                    count++;

                    dbcursor.moveToNext();
                }
                dbcursor.close();

                if (count > 0) {
                    return true;
                } else {
                    return false;
                }

            }

        } catch (Exception e) {

            return false;
        }

        return false;

    }


    public ArrayList<VisitorDetailGetterSetter> getVisitorLoginData(String visitdate) {

        ArrayList<VisitorDetailGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from TABLE_VISITOR_LOGIN where VISIT_DATE = '" + visitdate + "'"
                    , null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    VisitorDetailGetterSetter sb = new VisitorDetailGetterSetter();
                    sb.setEmpId(Integer.valueOf(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_EMP_CD))));
                    sb.setName(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_NAME)));
                    sb.setDesignation(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_DESIGNATION)));
                    sb.setIn_time_img(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IN_TIME_IMAGE)));
                    sb.setOut_time_img(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_OUT_TIME_IMAGE)));
                    sb.setEmp_code(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_EMP_CODE)));
                    sb.setVisit_date(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setIn_time(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOut_time(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setUpload_status(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_UPLOADSTATUS)));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {

        }
        return list;

    }

    public void updateVisitorUploadData(String empid) {

        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_UPLOADSTATUS, "U");

            db.update(CommonString.TABLE_VISITOR_LOGIN, values,
                    CommonString.KEY_EMP_CD + "='" + empid + "'", null);
        } catch (Exception e) {


        }
    }

    public ArrayList<MyPerformanceMer> getMyPerformanceData() {

        ArrayList<MyPerformanceMer> list = new ArrayList<MyPerformanceMer>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from " + "My_performance_Mer", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    MyPerformanceMer sb = new MyPerformanceMer();
                    sb.setMerchandise(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Merchandise"))));
                    sb.setAttendance(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Attendance"))));
                    sb.setPeriod(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Period")));
                    sb.setEmpId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Emp_Id"))));
                    sb.setPSS(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("PSS"))));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            return list;
        }
        return list;
    }

    public ArrayList<MyPerformanceRoutewiseMer> getRouteData() {
        ArrayList<MyPerformanceRoutewiseMer> list = new ArrayList<MyPerformanceRoutewiseMer>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from " + "My_performance_Routewise_Mer", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    MyPerformanceRoutewiseMer sb = new MyPerformanceRoutewiseMer();
                    sb.setMerchandise(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Merchandise"))));
                    sb.setRoute(dbcursor.getString(Integer.valueOf(dbcursor.getColumnIndexOrThrow("Route"))));
                    sb.setPSS(Integer.valueOf(dbcursor.getString(Integer.valueOf(dbcursor.getColumnIndexOrThrow("PSS")))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {

            return list;
        }

        return list;
    }


}
