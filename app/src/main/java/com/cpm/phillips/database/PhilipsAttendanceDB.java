package com.cpm.phillips.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cpm.phillips.bean.TableBean;
import com.cpm.phillips.constant.CommonString;
import com.cpm.phillips.getterSetter.AnswersGetterSetter;
import com.cpm.phillips.getterSetter.CampaignEntryGetterSetter;
import com.cpm.phillips.getterSetter.CoverageBean;
import com.cpm.phillips.getterSetter.CustomerInfoGetterSetter;
import com.cpm.phillips.getterSetter.JCPMasterGetterSetter;
import com.cpm.phillips.getterSetter.MyPerformanceMer;
import com.cpm.phillips.getterSetter.MyPerformanceRoutewiseMer;
import com.cpm.phillips.getterSetter.NonWorkingReasonGetterSetter;
import com.cpm.phillips.getterSetter.QuestionnaireGetterSetter;
import com.cpm.phillips.getterSetter.QuestionsGetterSetter;
import com.cpm.phillips.getterSetter.SkuGetterSetter;
import com.cpm.phillips.getterSetter.SpecialActivityGetterSetter;
import com.cpm.phillips.getterSetter.VisitorDetailGetterSetter;
import com.cpm.phillips.getterSetter.VisitorLoginGetterSetter;
import com.cpm.phillips.gsonGetterSetter.FeedbackQuestionData;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

/**
 * Created by deepakp on 12/20/2017.
 */

public class PhilipsAttendanceDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Philips_DB1";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    Context context;

    public PhilipsAttendanceDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(CommonString.CREATE_TABLE_ATTENDANCE);
            db.execSQL(CommonString.CREATE_TABLE_COVERAGE_DATA);
            db.execSQL(CommonString.CREATE_TABLE_CUSTOMER_DETAIL);
            db.execSQL(CommonString.CREATE_TABLE_SKU_QUYANTITY);
            db.execSQL(CommonString.CREATE_TABLE_CUSTOMER_SKU_DETAILS);
            db.execSQL(CommonString.CREATE_TABLE_VISITOR_LOGIN);
            db.execSQL(TableBean.getTable_JCPMaster());
            db.execSQL(TableBean.getNonworkingtable());
            db.execSQL(TableBean.getQuestionsTable());
            db.execSQL(TableBean.getAnswersTable());
            db.execSQL(TableBean.getSkuMasterTable());

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteAllTables() {
        // DELETING TABLES
        db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
        db.delete("SKU_DETAILS", null, null);
        db.delete("CUSTOMER_DETAILS", null, null);
        db.delete("CUSTOMER_SKU_DETAILS", null, null);

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
            Crashlytics.logException(ex);
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

                dbcursor = db
                        .rawQuery(
                                "SELECT -1 AS REASON_CD,'Select' as REASON,'-1' as  FOR_ATT,'-1' AS FOR_STORE,'-1' AS ENTRY_ALLOW,'-1' AS IMAGE_ALLOW  union SELECT * FROM NON_WORKING_REASON WHERE ENTRY_ALLOW ='1'"
                                , null);

            } else {
                dbcursor = db
                        .rawQuery(
                                "SELECT -1 AS REASON_CD,'Select' as REASON,'-1' as FOR_ATT,'-1' AS FOR_STORE,'-1' AS ENTRY_ALLOW,'-1' AS IMAGE_ALLOW  union SELECT * FROM NON_WORKING_REASON"
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
                    sb.setIMAGE_ALLOW(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("IMAGE_ALLOW")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
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
                values.put("REGION_CD", Integer.parseInt(data.getREGION_CD().get(i)));

                db.insert("JOURNEY_PLAN", null, values);
            }

        } catch (Exception ex) {
            Crashlytics.logException(ex);
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
                values.put("IMAGE_ALLOW",data.getIMAGE_ALLOW().get(i));

                db.insert("NON_WORKING_REASON", null, values);

            }

        } catch (Exception ex) {
            Crashlytics.logException(ex);
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
            Crashlytics.logException(ex);
            ex.printStackTrace();
        }

    }

    public ArrayList<JCPMasterGetterSetter> getStoreData(String date, String userid) {
        ArrayList<JCPMasterGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from JOURNEY_PLAN  " +
                    "where VISIT_DATE ='" + date + "' and USERID = '" + userid + "' order by STORENAME asc ", null);

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
                    sb.setREGION_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("REGION_CD")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }


    public ArrayList<JCPMasterGetterSetter> getStoreListCampaignData(String date, String userid) {
        ArrayList<JCPMasterGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_STORELIST_CAMPAIGN + "  " +
                    "where " + CommonString.KEY_VISIT_DATE + " ='" + date + "' and " + CommonString.KEY_USER_ID + " = '" + userid + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JCPMasterGetterSetter sb = new JCPMasterGetterSetter();

                    sb.setSTORE_CD((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_STORE_CD))));
                    sb.setEMP_CD((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_EMP_CD))));
                    sb.setUSERNAME(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVISIT_DATE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setSTORENAME(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_STORENAME)));
                    sb.setREGION_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REGION_CD)));
                    sb.setUPLOAD_STATUS(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_STATUS)));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
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
            Crashlytics.logException(ex);
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
            Crashlytics.logException(e);
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
            Crashlytics.logException(e);
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;
    }


    public void deleteSpecificTables(String store_id) {
        // DELETING TABLES
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_STORE_CD + "= '" + store_id + "'", null);
        db.delete("SKU_DETAILS", CommonString.KEY_STORE_CD + "= '" + store_id + "'", null);
        db.delete("CUSTOMER_DETAILS", CommonString.KEY_STORE_CD + "= '" + store_id + "'", null);
        db.delete("CUSTOMER_SKU_DETAILS", CommonString.KEY_STORE_CD + "= '" + store_id + "'", null);
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
            Crashlytics.logException(e);
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
            Crashlytics.logException(e);
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
            Crashlytics.logException(e);
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
            Crashlytics.logException(e);

        }
    }

    public long updateCoverageData(CoverageBean coverageBean, String checkOut) {

        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_OUT_TIME_IMAGE, coverageBean.getOuttime_Image());
            values.put(CommonString.KEY_OUT_TIME, coverageBean.getOutTime());
            values.put(CommonString.KEY_COVERAGE_STATUS, checkOut);

           /* values.put(CommonString.KEY_COVERAGE_STATUS, keyC);*/

            return db.update(CommonString.TABLE_COVERAGE_DATA, values,
                    CommonString.KEY_STORE_CD + "='" + coverageBean.getStoreId() + "' and " + CommonString.KEY_USER_ID + " = '" + coverageBean.getUserId() + "'", null);

        } catch (Exception e) {
            Crashlytics.logException(e);
            return 0;
        }
    }



    public long updateJcpStatusData(CoverageBean coverageBean, String keyC) {

        try {
            ContentValues values = new ContentValues();
            values.put("CHECKOUT_STATUS", keyC);
            return db.update("JOURNEY_PLAN", values,
                    CommonString.KEY_STORE_CD + "='" + coverageBean.getStoreId() + "' and " + "USERID" + " = '" + coverageBean.getUserId() + "'", null);

        } catch (Exception e) {
            Crashlytics.logException(e);
            return 0;
        }
    }

    public long JcpUploadStatusData(CoverageBean coverageBean, String keyU) {

        try {
            ContentValues values = new ContentValues();
            values.put("UPLOAD_STATUS", keyU);
            return db.update("JOURNEY_PLAN", values,
                    CommonString.KEY_STORE_CD + "='" + coverageBean.getStoreId() + "' and " + "USERID" + " = '" + coverageBean.getUserId() + "'", null);

        } catch (Exception e) {
            Crashlytics.logException(e);
            return 0;
        }
    }



    public long updateCampaignStatusData(JCPMasterGetterSetter jcpGetSet, String status) {

        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_STATUS, status);
            return db.update(CommonString.TABLE_STORELIST_CAMPAIGN, values,
                    CommonString.KEY_STORE_CD + "='" + jcpGetSet.getSTORE_CD().get(0) + "' and " + CommonString.KEY_EMP_CD + " = '" + jcpGetSet.getEMP_CD().get(0) + "'", null);

        } catch (Exception e) {
            Crashlytics.logException(e);
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
            Crashlytics.logException(ex);

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
            Crashlytics.logException(e);

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
            Crashlytics.logException(e);

        }
        return list;

    }



    public ArrayList<VisitorDetailGetterSetter> getPreviousDayVisitorLoginData(String visitdate) {

        ArrayList<VisitorDetailGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from TABLE_VISITOR_LOGIN where VISIT_DATE = '<>" + visitdate + "'"
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
            Crashlytics.logException(e);

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
            Crashlytics.logException(e);

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
            Crashlytics.logException(e);
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
            Crashlytics.logException(e);
            return list;
        }

        return list;
    }

    public void insertQuestionnaireData(QuestionnaireGetterSetter data) {

        db.delete("QUESTIONNAIRE", null, null);
        ContentValues values = new ContentValues();

        try {

            for (int i = 0; i < data.getQUESTION_ID().size(); i++) {

                values.put("QUESTION_ID", Integer.parseInt(data.getQUESTION_ID().get(i)));
                values.put("QUESTION", data.getQUESTION().get(i));
                values.put("ANSWER_ID", Integer.parseInt(data.getANSWER_ID().get(i)));
                values.put("ANSWER", data.getANSWER().get(i));
                values.put("QUESTION_GROUP_ID", Integer.parseInt(data.getQUESTION_GROUP_ID().get(i)));
                values.put("QUESTION_GROUP", data.getQUESTION_GROUP().get(i));
                values.put("QUESTION_TYPE", data.getQUESTION_TYPE().get(i));

                db.insert("QUESTIONNAIRE", null, values);
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
        }

    }

    public void insertSpecialActivityData(SpecialActivityGetterSetter data) {

        db.delete("SPECIAL_ACTIVITY", null, null);
        ContentValues values = new ContentValues();

        try {
            for (int i = 0; i < data.getREGION_CD().size(); i++) {

                values.put("REGION_CD", Integer.parseInt(data.getREGION_CD().get(i)));
                values.put("ACTIVITY_CD", Integer.parseInt(data.getACTIVITY_CD().get(i)));
                values.put("ACTIVITY", data.getACTIVITY().get(i));

                db.insert("SPECIAL_ACTIVITY", null, values);
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
        }

    }

    public ArrayList<SpecialActivityGetterSetter> getSpecialActivityData(String regionCd) {
        ArrayList<SpecialActivityGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from SPECIAL_ACTIVITY  " +
                    "where REGION_CD ='" + regionCd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    SpecialActivityGetterSetter sb = new SpecialActivityGetterSetter();

                    sb.setACTIVITY_CD((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("ACTIVITY_CD"))));
                    sb.setACTIVITY((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("ACTIVITY"))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }

    public long saveSpecialActivityData(CampaignEntryGetterSetter data) {

        db.delete(CommonString.TABLE_SPECIAL_ACTIVITY_SAVED_DATA, null, null);
        ContentValues values = new ContentValues();

        try {
            if (data != null) {

                values.put(CommonString.KEY_STORE_CD, Integer.parseInt(data.getStoreCd()));
                values.put(CommonString.KEY_USER_ID, (data.getUserid()));
                values.put(CommonString.KEY_ACTIVITY_CD, data.getActivityCd());
                values.put(CommonString.KEY_VISIT_DATE, data.getVisitDate());
                values.put(CommonString.KEY_IMAGE, data.getImage1());
                values.put(CommonString.KEY_IMAGE2, data.getImage2());
                values.put(CommonString.KEY_IMAGE3, data.getImage3());
                values.put(CommonString.KEY_REMARK, data.getRemark());

                return db.insert(CommonString.TABLE_SPECIAL_ACTIVITY_SAVED_DATA, null, values);
            } else {
                return 0;
            }

        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            return 0;
        }

    }

    public ArrayList<CampaignEntryGetterSetter> getSavedSpecialActivityData(String storeCd, String activityId, String visitdate, String username) {
        ArrayList<CampaignEntryGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_SPECIAL_ACTIVITY_SAVED_DATA + "" +
                    " where " + CommonString.KEY_STORE_CD + " ='" + storeCd + "' and " + CommonString.KEY_ACTIVITY_CD + " ='" + activityId + "' and " + CommonString.KEY_VISIT_DATE + " ='" + visitdate + "' and " + CommonString.KEY_USER_ID + " ='" + username + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CampaignEntryGetterSetter sb = new CampaignEntryGetterSetter();

                    sb.setImage1((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IMAGE))));
                    sb.setImage2((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IMAGE2))));
                    sb.setImage3((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IMAGE3))));
                    sb.setRemark((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REMARK))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }

    public void insertStoreListCampaignData(JCPMasterGetterSetter data) {

        db.delete(CommonString.TABLE_STORELIST_CAMPAIGN, null, null);
        ContentValues values = new ContentValues();

        try {
            for (int i = 0; i < data.getSTORE_CD().size(); i++) {
                values.put(CommonString.KEY_STORE_CD, Integer.parseInt(data.getSTORE_CD().get(i)));
                values.put(CommonString.KEY_EMP_CD, Integer.parseInt(data.getEMP_CD().get(i)));
                values.put(CommonString.KEY_USER_ID, (data.getUSERNAME().get(i)));
                values.put(CommonString.KEY_VISIT_DATE, (data.getVISIT_DATE().get(i)));
                values.put(CommonString.KEY_STORENAME, (data.getSTORENAME().get(i)));
                values.put(CommonString.KEY_REGION_CD, Integer.parseInt(data.getREGION_CD().get(i)));
                values.put(CommonString.KEY_STATUS, (data.getUPLOAD_STATUS().get(i)));

                db.insert(CommonString.TABLE_STORELIST_CAMPAIGN, null, values);
            }

        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
        }

    }

    public ArrayList<CampaignEntryGetterSetter> getSavedSpecialActivityFromStoreCdData(String storeCd, String visitdate, String username) {
        ArrayList<CampaignEntryGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT activity_cd,IFNULL(image,'') as image,IFNULL(image2,'') as image2,IFNULL(image3,'') as image3,IFNULL(remark1,'') as remark1 from " + CommonString.TABLE_SPECIAL_ACTIVITY_SAVED_DATA + "" +
                    " where " + CommonString.KEY_STORE_CD + " ='" + storeCd + "' and " + CommonString.KEY_VISIT_DATE + " ='" + visitdate + "' and " + CommonString.KEY_USER_ID + " ='" + username + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CampaignEntryGetterSetter sb = new CampaignEntryGetterSetter();

                    sb.setActivityCd((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_ACTIVITY_CD))));
                    sb.setImage1((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IMAGE))));
                    sb.setImage2((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IMAGE2))));
                    sb.setImage3((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IMAGE3))));
                    sb.setRemark((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REMARK))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }

    public ArrayList<QuestionnaireGetterSetter> getQuestionnaireData(String questionGroupid) {
        ArrayList<QuestionnaireGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
           /* dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_SPECIAL_ACTIVITY_SAVED_DATA + "" +
                    " where " + CommonString.KEY_STORE_CD + " ='" + storeCd + "' and " + CommonString.KEY_VISIT_DATE + " ='" + visitdate + "' and " + CommonString.KEY_USER_ID + " ='" + username + "'", null);*/

            dbcursor = db.rawQuery("SELECT distinct QUESTION_ID,QUESTION,QUESTION_TYPE from " + CommonString.TABLE_QUESTIONS + " where " + CommonString.KEY_QUESTION_GROUP_ID + " = " + questionGroupid + "", null);


            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    QuestionnaireGetterSetter sb = new QuestionnaireGetterSetter();

                    sb.setQUESTION_ID((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_QUESTION_ID))));
                    sb.setQUESTION((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_QUESTION))));
                    sb.setQUESTION_TYPE((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_QUESTION_TYPE))));
                /*    sb.setANSWER_ID((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_ANSWER_ID))));
                    sb.setANSWER((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_ANSWER))));
                    sb.setQUESTION_GROUP_ID((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_QUESTION_GROUP_ID))));
                    sb.setQUESTION_GROUP((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_QUESTION_GROUP))));
                   */

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }

    public ArrayList<QuestionnaireGetterSetter> getQuestionnaireAnswerData(QuestionnaireGetterSetter quesGetSet) {
        Log.d("Fetching", "Storedata--------------->Start<------------");

        ArrayList<QuestionnaireGetterSetter> list = new ArrayList<>();
        QuestionnaireGetterSetter sb1 = new QuestionnaireGetterSetter();
        sb1.setANSWER_ID("0");
        sb1.setANSWER("Select");
        list.add(0, sb1);

        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("Select * from " + CommonString.TABLE_ANSWERS + "" +
                    " where QUESTION_ID='" + quesGetSet.getQUESTION_ID().get(0) + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    QuestionnaireGetterSetter sb = new QuestionnaireGetterSetter();

                    if (quesGetSet.getQUESTION_TYPE().get(0).equalsIgnoreCase("Dropdown")) {
                        sb.setANSWER_ID(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ANSWER_ID)));
                        sb.setANSWER(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ANSWER)));
                    } else {
                        sb.setANSWER_ID("0");
                        sb.setANSWER("");
                    }

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception", " when fetching opening stock!!!!!!!!!!! " + e.toString());
            return list;
        }
        Log.d("Fetching", " opening stock---------------------->Stop<-----------");
        return list;
    }

    public long saveQuestionnaireData(ArrayList<QuestionnaireGetterSetter> listDataHeader, String username, String visit_date) {
        long id = 0;
        db.delete(CommonString.TABLE_QUESTIONNAIRE_DATA, "", null);
        try {
            ContentValues values = new ContentValues();
            for (int i = 0; i < listDataHeader.size(); i++) {
                values.put(CommonString.KEY_USERNAME, username);
                values.put(CommonString.KEY_VISIT_DATE, visit_date);
                values.put(CommonString.KEY_QUESTION_ID, listDataHeader.get(i).getQUESTION_ID().get(0));
                values.put(CommonString.KEY_ANSWER, listDataHeader.get(i).getSp_Answer());
                id = db.insert(CommonString.TABLE_QUESTIONNAIRE_DATA, null, values);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
        return id;
    }

    public void insertQuestionsData(QuestionsGetterSetter data) {

        db.delete("QUESTIONS", null, null);
        ContentValues values = new ContentValues();

        try {
            for (int i = 0; i < data.getQUESTION_ID().size(); i++) {

                values.put("QUESTION_ID", Integer.parseInt(data.getQUESTION_ID().get(i)));
                values.put("QUESTION", data.getQUESTION().get(i));
                values.put("QUESTION_GROUP_ID", data.getQUESTION_GROUP_ID().get(i));
                values.put("QUESTION_GROUP", data.getQUESTION_GROUP().get(i));
                values.put("QUESTION_TYPE", data.getQUESTION_TYPE().get(i));

                db.insert("QUESTIONS", null, values);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }

    }

    public void insertAnswersData(AnswersGetterSetter data) {

        db.delete("ANSWERS", null, null);
        ContentValues values = new ContentValues();

        try {
            for (int i = 0; i < data.getQUESTION_ID().size(); i++) {

                values.put("QUESTION_ID", Integer.parseInt(data.getQUESTION_ID().get(i)));
                values.put("ANSWER_ID", Integer.parseInt(data.getANSWER_ID().get(i)));
                values.put("ANSWER", (data.getANSWER().get(i)));

                db.insert("ANSWERS", null, values);
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
        }

    }

    public long saveClientFeedBackData(ArrayList<QuestionnaireGetterSetter> listDataHeader, String username, String visit_date) {
        long id = 0;
        db.delete(CommonString.TABLE_CLIENT_FEEDBACK_DATA, "", null);
        try {
            ContentValues values = new ContentValues();
            for (int i = 0; i < listDataHeader.size(); i++) {
                values.put(CommonString.KEY_USERNAME, username);
                values.put(CommonString.KEY_VISIT_DATE, visit_date);
                values.put(CommonString.KEY_QUESTION_ID, listDataHeader.get(i).getQUESTION_ID().get(0));
                values.put(CommonString.KEY_ANSWER, listDataHeader.get(i).getSp_Answer());

                id = db.insert(CommonString.TABLE_CLIENT_FEEDBACK_DATA, null, values);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void insertSKUMasterData(SkuGetterSetter data) {

        db.delete("SKU_MASTER", null, null);
        ContentValues values = new ContentValues();

        try {
            for (int i = 0; i < data.getSKU_CD().size(); i++) {

                values.put("SKU_CD", Integer.parseInt(data.getSKU_CD().get(i)));
                values.put("SKU", data.getSKU().get(i));

                db.insert("SKU_MASTER", null, values);
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
        }

    }

    public ArrayList<SkuGetterSetter> getSkuMasterData() {
        ArrayList<SkuGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from SKU_MASTER" , null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    SkuGetterSetter sb = new SkuGetterSetter();
                    sb.setSKU(dbcursor.getString(dbcursor.getColumnIndexOrThrow("SKU")));
                    sb.setSKU_CD((dbcursor.getString(dbcursor.getColumnIndexOrThrow("SKU_CD"))));
                    sb.setOPENING_STOCK_SKU_QUY("");
                    sb.setMID_DAT_STOCK_SKU_QUY("");
                    sb.setCLOSING_STOCK_SKU_QUY("");
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;
    }


   // inserting customer information in the table
    public long  insertCutomerData(ArrayList<CustomerInfoGetterSetter> customerList, String visit_date, String username, String store_cd) {
        long id = 0;
        db.delete("CUSTOMER_DETAILS", null, null);
        db.delete("CUSTOMER_SKU_DETAILS", null, null);
        try {
            ContentValues values = new ContentValues();
            ContentValues values2 = new ContentValues();

            for (int i = 0; i < customerList.size(); i++) {
                values.put(CommonString.KEY_VISIT_DATE, visit_date);
                values.put(CommonString.KEY_USER_NAME,username );
                values.put(CommonString.KEY_STORE_CD, store_cd);
                values.put(CommonString.KEY_CUSTOMER_NAME, customerList.get(i).getUSER_NAME());
                values.put(CommonString.KEY_USER_EMAIL, customerList.get(i).getUSER_EMAIL());
                values.put(CommonString.KEY_USER_MOBILE, customerList.get(i).getUSER_MOBILE());
                id = db.insert("CUSTOMER_DETAILS", null, values);

                    for (int j = 0; j < customerList.get(i).getList().size(); j++) {
                        values2.put(CommonString.KEY_STORE_CD, store_cd);
                        values2.put(CommonString.KEY_CUSTOMER_ID, id);
                        values2.put(CommonString.KEY_QUESTION_CD, customerList.get(i).getList().get(j).getQuestion_CD());
                        values2.put(CommonString.KEY_ANSWER_CD, customerList.get(i).getList().get(j).getAnswer_CD());
                        db.insert("CUSTOMER_SKU_DETAILS", null, values2);

                }
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
        }
        return id;
    }

    // inserting sku details in table
    public long insertSkuDetails(ArrayList<SkuGetterSetter> skuGetterSetter, String visit_date, String username, String store_cd) {
        long id = 0;
        db.delete("SKU_DETAILS", null, null);
        try {
            ContentValues values = new ContentValues();
            for (int i = 0; i < skuGetterSetter.size(); i++) {
                values.put(CommonString.KEY_VISIT_DATE, visit_date);
                values.put(CommonString.KEY_USER_NAME,username );
                values.put(CommonString.KEY_STORE_CD, store_cd);
                values.put(CommonString.KEY_SKU, skuGetterSetter.get(i).getSKU().get(0));
                values.put(CommonString.KEY_SKU_CD, skuGetterSetter.get(i).getSKU_CD().get(0));
                values.put(CommonString.KEY_OPENING_STOCK_QUY, skuGetterSetter.get(i).getOPENING_STOCK_SKU_QUY());
                values.put(CommonString.KEY_MID_DAY_STOCK_QUY, "");
                values.put(CommonString.KEY_CLOSING_STOCK_QUY, "");
                id = db.insert("SKU_DETAILS", null, values);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
        }
        return id;
    }

    // getting sku details on basis of date and store Id
    public ArrayList<SkuGetterSetter> getSkuData(String date, String store_cd) {
        ArrayList<SkuGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("SELECT  * from SKU_DETAILS  " +
                    "where VISIT_DATE ='" + date + "' and STORE_CD = '" +  store_cd + "'", null);

         //   dbcursor = db.rawQuery("SELECT  * from SKU_DETAILS ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    SkuGetterSetter sb = new SkuGetterSetter();

                    sb.setSKU(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SKU)));
                    sb.setSKU_CD((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SKU_CD))));
                    sb.setOPENING_STOCK_SKU_QUY((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OPENING_STOCK_QUY))));
                    sb.setMID_DAT_STOCK_SKU_QUY((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_MID_DAY_STOCK_QUY))));
                    sb.setCLOSING_STOCK_SKU_QUY((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CLOSING_STOCK_QUY))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;
    }


    // getting customer sku data

    public ArrayList<CustomerInfoGetterSetter> getCustomerInfoDetails(String date, String store_cd) {
        ArrayList<CustomerInfoGetterSetter> list = new ArrayList<>();
        ArrayList<FeedbackQuestionData> list2 = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("SELECT  * from CUSTOMER_DETAILS  " +
                    "where VISIT_DATE ='" + date + "' and STORE_CD = '" +  store_cd + "'", null);


            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CustomerInfoGetterSetter sb = new CustomerInfoGetterSetter();
                    sb.setUSER_NAME(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CUSTOMER_NAME)));
                    sb.setUSER_EMAIL((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_EMAIL))));
                    sb.setUSER_MOBILE((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_MOBILE))));

                    list2 = getFeedBackQuestionList(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setList(list2);
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;
    }



    // getting customer sku data

    public ArrayList<CustomerInfoGetterSetter> getSelectdCustomerInfoDetails(String date, String store_cd, int position) {
        ArrayList<CustomerInfoGetterSetter> list = new ArrayList<>();
        ArrayList<FeedbackQuestionData> list2 = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("SELECT  * from CUSTOMER_DETAILS  " +
                    "where VISIT_DATE ='" + date + "' and STORE_CD = '" +  store_cd + "' and USER_ID = '" +  position + "'", null);


            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CustomerInfoGetterSetter sb = new CustomerInfoGetterSetter();
                    sb.setUSER_NAME(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CUSTOMER_NAME)));
                    sb.setUSER_EMAIL((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_EMAIL))));
                    sb.setUSER_MOBILE((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_MOBILE))));

                    list2 = getFeedBackQuestionList(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setList(list2);
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;
    }

    private  ArrayList<FeedbackQuestionData>  getFeedBackQuestionList(String ID) {
        ArrayList<FeedbackQuestionData> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from CUSTOMER_SKU_DETAILS " +
                    "where CUSTOMER_ID ='" + ID + "' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    FeedbackQuestionData data = new FeedbackQuestionData();
                    data.setCustomer_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CUSTOMER_ID)));
                    data.setQuestion_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_QUESTION_CD)));
                    data.setAnswer_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ANSWER_CD)));
                    list.add(data);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;
    }


    public  ArrayList<SkuGetterSetter> getSkuMasterDataUpload(String storeId) {
        ArrayList<SkuGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

       try {

            dbcursor = db.rawQuery("SELECT  * from SKU_DETAILS  " +
                    "where STORE_CD = '" +  storeId + "'", null);

            //   dbcursor = db.rawQuery("SELECT  * from SKU_DETAILS ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    SkuGetterSetter sb = new SkuGetterSetter();
                    sb.setVISITED_DATE(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setSKU(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SKU)));
                    sb.setSKU_CD((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SKU_CD))));
                    sb.setOPENING_STOCK_SKU_QUY((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OPENING_STOCK_QUY))));
                    sb.setMID_DAT_STOCK_SKU_QUY((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_MID_DAY_STOCK_QUY))));
                    sb.setCLOSING_STOCK_SKU_QUY((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CLOSING_STOCK_QUY))));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
           Crashlytics.logException(e);
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;

    }

    // customer upload data
    public ArrayList<CustomerInfoGetterSetter> getCustomerInfoUploadData(String storeId) {
        ArrayList<CustomerInfoGetterSetter> list = new ArrayList<>();
        ArrayList<FeedbackQuestionData> list2 = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("SELECT  * from CUSTOMER_DETAILS  " +
                    "where  STORE_CD = '" +  storeId + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CustomerInfoGetterSetter sb = new CustomerInfoGetterSetter();
                    sb.setUSER_ID(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVISITED_DATE(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setUSER_NAME(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CUSTOMER_NAME)));
                    sb.setUSER_EMAIL((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_EMAIL))));
                    sb.setUSER_MOBILE((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_MOBILE))));

                    list2 = getFeedBackQuestionList(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setList(list2);
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;

    }

    public Long updateCoverageStatus(String storeId, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            l = db.update(CommonString.TABLE_COVERAGE_DATA, values,
                    CommonString.KEY_STORE_CD + "=" + storeId, null);
        } catch (Exception e) {
            Crashlytics.logException(e);

        }
        return l;
    }


    public long updateCoverageValidData(String  status,String store_id,String visit_date) {
        long l=0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            l= db.update(CommonString.TABLE_COVERAGE_DATA, values,
                    CommonString.KEY_STORE_CD + "='" + store_id + "' and " + CommonString.KEY_VISIT_DATE + " = '" + visit_date + "'", null);

        } catch (Exception e) {
            Crashlytics.logException(e);
            return 0;
        }
        return l;
    }

    public Long updateJCPStatus(String storeId, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("UPLOAD_STATUS", status);
            l = db.update("JOURNEY_PLAN", values,
                    CommonString.KEY_STORE_CD + "=" + storeId, null);
        } catch (Exception e) {
            Crashlytics.logException(e);

        }
        return l;
    }


    public void deleteSpecificTablesAfterUpload() {
        // DELETING TABLES
        db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
        db.delete("SKU_DETAILS", null, null);
        db.delete("CUSTOMER_DETAILS", null, null);
        db.delete("CUSTOMER_SKU_DETAILS", null, null);
    }




    public boolean isCoverageDataFilled(String date) {
        boolean filled = false;
        Cursor dbcursor = null;

        try {

            dbcursor = db.rawQuery("SELECT  * from COVERAGE_DATA WHERE " + CommonString.KEY_VISIT_DATE + " <>'" + date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception ", "when fetching Records!!!!!!!!!!!!!!!!!!!!!" + e.toString());
            return filled;
        }
        return filled;
    }

    public ArrayList<CoverageBean> getCoveragePreviousData(String date, String user_name) {

        ArrayList<CoverageBean> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "<>'" + date + "' and USER_ID = '" + user_name + "'", null);

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
            Crashlytics.logException(e);
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;
    }

    public Long updateMidDayStockQuy(ArrayList<SkuGetterSetter> skuGetterSetter, String visit_date, String username, String store_cd) {
        long id = 0;
        try {
            ContentValues values = new ContentValues();
                for(int i=0;i<skuGetterSetter.size();i++){
                    values.put(CommonString.KEY_MID_DAY_STOCK_QUY, skuGetterSetter.get(i).getMID_DAT_STOCK_SKU_QUY());
                   id = db.update("SKU_DETAILS", values,
                            CommonString.KEY_SKU_CD + "='" + skuGetterSetter.get(i).getSKU_CD().get(0) + "' and " + CommonString.KEY_VISIT_DATE + " = '" + visit_date + "' and " + CommonString.KEY_USER_NAME + " = '" + username + "' and " + CommonString.KEY_STORE_CD + " = '" + store_cd + "'", null);
            }
            return id;
        } catch (Exception e) {
            Crashlytics.logException(e);
            return id;
        }
    }

    public long updateClosingStockQuy(ArrayList<SkuGetterSetter> skuList, String visit_date, String username, String store_cd) {
        long id = 0;
        try {
            ContentValues values = new ContentValues();
            for(int i=0;i<skuList.size();i++){
                values.put(CommonString.KEY_CLOSING_STOCK_QUY, skuList.get(i).getCLOSING_STOCK_SKU_QUY());
                id = db.update("SKU_DETAILS", values,
                        CommonString.KEY_SKU_CD + "='" + skuList.get(i).getSKU_CD().get(0) + "' and " + CommonString.KEY_VISIT_DATE + " = '" + visit_date + "' and " + CommonString.KEY_USER_NAME + " = '" + username + "' and " + CommonString.KEY_STORE_CD + " = '" + store_cd + "'", null);
            }
            return id;
        } catch (Exception e) {
            Crashlytics.logException(e);
            return id;
        }
    }
}
