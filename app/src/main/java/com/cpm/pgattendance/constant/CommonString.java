package com.cpm.pgattendance.constant;

import android.os.Environment;

/**
 * Created by deepakp on 12/20/2017.
 */

public class CommonString {


    public static final String IMAGES_FOLDER_NAME = ".PNG_Attendance_Images";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String MESSAGE_SOCKETEXCEPTION = "Network Communication Failure. Please Check Your Network Connection";
    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_NO_DATA = "NoData";
    public static final String MESSAGE_NO_RESPONSE_SERVER = "Server Not Responding.Please try again.";
    public static final String KEY_FAILURE = "Failure";
    public static final String KEY_PATH = "path";
    public static final String MESSAGE_INVALID_JSON = "Problem Occured while parsing Json : invalid json data";
    public static final String MESSAGE_INVALID_XML = "Problem Occured while parsing XML : invalid data";
    public static final String KEY_VERSION = "APP_VERSION";
    public static final String KEY_DATE = "date";
    public static final String KEY_YYYYMMDD_DATE = "yyyymmddDate";
    public static final String MESSAGE_EXCEPTION = "Problem Occured : Report The Problem To Parinaam ";
    public static final String MESSAGE_CHANGED = "Invalid UserId Or Password";
    public static final int LOGIN_SERVICE = 1;
    //public static String URL = "http://gskgtm.parinaam.in/Webservice/Gskwebservice.svc/";
    public static String URL = "http://pgmer.parinaam.in/PGService.asmx";
    public static String URL2 = "http://pgmer.parinaam.in/PGService.asmx/";
    public static final String KEY_NOTICE_BOARD_LINK = "NOTICE_BOARD_LINK";
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/" + IMAGES_FOLDER_NAME + "/";
    public static final String KEY_CHECK_IN = "I";
    public static final String TABLE_ATTENDANCE = "ATTENDANCE";
    public static final String KEY_Y = "Y";
    public static final String KEY_STORE_CD = "STORE_CD";
    public static final String KEY_FROMSTORE = "FROMSTORE";
    public static final String KEY_DOWNLOAD_INDEX = "download_Index";
    public static final String KEY_ISDATADOWNLOADED = "isdatadownloaded";
    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String METHOD_NAME_UNIVERSAL_DOWNLOAD = "Download_Universal";
    public static final String METHOD_NAME_IMAGE = "GetImageWithFolderName";
    public static final String SOAP_ACTION_UNIVERSAL = "http://tempuri.org/" + METHOD_NAME_UNIVERSAL_DOWNLOAD;
    public static final String MESSAGE_DOWNLOAD = "Data Downloaded Successfully";
    public static final String MESSAGE_JCP_FALSE = "Data is not found in ";
    public static final String MESSAGE_INTERNET_NOT_AVAILABLE = "No Internet Connection.Please Check Your Network Connection";
    public static final String METHOD_LOGIN = "UserLoginDetail";
    public static final String SOAP_ACTION_LOGIN = "http://tempuri.org/" + METHOD_LOGIN;
    public static final String METHOD_COVERAGE_UPLOAD = "UPLOAD_COVERAGE1";
    public static final String SOAP_ACTION = "http://tempuri.org/";
    public static final String KEY_CHANGED = "Changed";
    public static final String KEY_FALSE = "False";
    public static final String TABLE_COVERAGE_DATA = "COVERAGE_DATA";
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_IN_TIME = "IN_TIME";
    public static final String KEY_OUT_TIME = "OUT_TIME";
    public static final String KEY_VISIT_DATE = "VISIT_DATE";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_COVERAGE_STATUS = "Coverage_status";
    public static final String KEY_REASON = "REASON";
    public static final String KEY_REASON_ID = "REASON_ID";
    public static final String KEY_U = "U";
    public static final String KEY_C = "C";
    public static final String KEY_L = "L";
    public static final String KEY_P = "P";
    public static final String KEY_INVALID = "InValid";
    public static final String KEY_VALID = "Valid";
    public static final String TAG_OBJECT = "object";
    public static final String TAG_FOR = "for";
    public static final String DATA_DELETE_ALERT_MESSAGE = "Saved data will be lost - Do you want to continue?";
    public static final String TABLE_VISITOR_LOGIN = "TABLE_VISITOR_LOGIN";
    public static final String KEY_DESIGNATION = "DESIGNATION";
    public static final String KEY_EMP_CODE = "EMP_CODE";
    public static final String KEY_IN_TIME_IMAGE = "IN_TIME_IMAGE";
    public static final String KEY_OUT_TIME_IMAGE = "OUT_TIME_IMAGE";
    public static final String KEY_EMP_CD = "EMP_CD";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_UPLOADSTATUS = "UPLOADSTATUS";
    public static final String METHOD_UPLOAD_XML = "DrUploadXml";
    public static final String KEY_IS_QUIZ_DONE = "is_quiz_done";
    public static final String KEY_QUESTION_CD = "question_cd";
    public static final String KEY_ANSWER_CD = "answer_cd";
    public static final int TIMEOUT = 20000;


    public static final String CREATE_TABLE_ATTENDANCE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ATTENDANCE +
            " (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
            "USERNAME VARCHAR," +
            "VISITDATE VARCHAR," +
            "REASON_CD INTEGER," +
            "ENTRY_ALLOW INTEGER," +
            "ATTENDANCE_STATUS VARCHAR" +
            ")";

    public static final String CREATE_TABLE_COVERAGE_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_COVERAGE_DATA + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_STORE_CD + " INTEGER,"
            + KEY_USER_ID + " VARCHAR,"
            + KEY_IN_TIME + " VARCHAR,"
            + KEY_OUT_TIME + " VARCHAR,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_LATITUDE + " VARCHAR,"
            + KEY_LONGITUDE + " VARCHAR,"
            + KEY_COVERAGE_STATUS + " VARCHAR,"
            + KEY_IN_TIME_IMAGE + " VARCHAR,"
            + KEY_OUT_TIME_IMAGE + " VARCHAR,"
            + KEY_REASON_ID + " INTEGER,"
            + KEY_REASON + " VARCHAR)";


    public static final String CREATE_TABLE_VISITOR_LOGIN = "CREATE TABLE "
            + TABLE_VISITOR_LOGIN + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_EMP_CD + " VARCHAR,"
            + KEY_EMP_CODE + " VARCHAR,"
            + KEY_NAME + " VARCHAR,"
            + KEY_DESIGNATION + " VARCHAR,"
            + KEY_UPLOADSTATUS + " VARCHAR,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_IN_TIME + " VARCHAR,"
            + KEY_OUT_TIME + " VARCHAR,"
            + KEY_IN_TIME_IMAGE + " VARCHAR,"
            + KEY_OUT_TIME_IMAGE + " VARCHAR)";


}
