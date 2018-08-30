package com.cpm.phillipspc.xmlHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.cpm.phillipspc.LoginActivity;
import com.cpm.phillipspc.MainMenuActivity;
import com.cpm.phillipspc.constant.AlertandMessages;
import com.cpm.phillipspc.constant.CommonString;
import com.cpm.phillipspc.dailyEntry.StoreEntryActivity;
import com.cpm.phillipspc.database.PhilipsAttendanceDB;
import com.cpm.phillipspc.getterSetter.CoverageBean;
import com.cpm.phillipspc.upload.Retrofit_method.UploadImageWithRetrofit;
import com.crashlytics.android.Crashlytics;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/*
 * Created by deepakp on 12/27/2017.
 */

public class UploadCoverageClass extends AsyncTask<Void, Data, String> {

    Context context;
    ArrayList<CoverageBean> coverageBeanlist;
    ProgressDialog dialog;
    PhilipsAttendanceDB db;
    String msg;
    CoverageBean cdata;
    String app_ver;

    public UploadCoverageClass(Context context, String msg, CoverageBean cdata) {
        this.context = context;
        this.msg = msg;
        this.cdata = cdata;
        db = new PhilipsAttendanceDB(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setTitle(msg);
        dialog.setMessage("Wait...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            //String result = "";
            coverageBeanlist = new ArrayList<>();
            coverageBeanlist.add(cdata);
            //coverageBeanlist = db.getCoverageData(visit_date);
            UploadImageWithRetrofit uploadRetro = new UploadImageWithRetrofit(context);
            String resulthttp = "";

            if (coverageBeanlist.size() > 0) {
                for (int i = 0; i < coverageBeanlist.size(); i++) {

                    String onXML = "[DATA][USER_DATA]"
                            + "[STORE_CD]" + coverageBeanlist.get(i).getStoreId() + "[/STORE_CD]"
                            + "[VISIT_DATE]" + coverageBeanlist.get(i).getVisitDate() + "[/VISIT_DATE]"
                            + "[LATITUDE]" + coverageBeanlist.get(i).getLatitude() + "[/LATITUDE]"
                            + "[APP_VERSION]" + app_ver + "[/APP_VERSION]"
                            + "[LONGITUDE]" + coverageBeanlist.get(i).getLongitude() + "[/LONGITUDE]"
                            + "[IN_TIME]" + coverageBeanlist.get(i).getInTime() + "[/IN_TIME]"
                            + "[OUT_TIME]" + coverageBeanlist.get(i).getOutTime() + "[/OUT_TIME]"
                            + "[UPLOAD_STATUS]" + coverageBeanlist.get(i).getStatus() + "[/UPLOAD_STATUS]"
                            + "[USER_ID]" + coverageBeanlist.get(i).getUserId() + "[/USER_ID]"
                            + "[INTIME_IMAGE]" + coverageBeanlist.get(i).getIntime_Image() + "[/INTIME_IMAGE]"
                            + "[OUTTIME_IMAGE]" + coverageBeanlist.get(i).getOuttime_Image() + "[/OUTTIME_IMAGE]"
                            + "[REASON_ID]" + coverageBeanlist.get(i).getReasonid() + "[/REASON_ID]"
                            + "[/USER_DATA][/DATA]";


                    SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_COVERAGE_UPLOAD);
                    request.addProperty("onXML", onXML);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_COVERAGE_UPLOAD, envelope);
                    Object result = (Object) envelope.getResponse();

                    if (result.toString().contains(CommonString.KEY_SUCCESS)) {
                        //if (true) {
                        resulthttp = CommonString.KEY_SUCCESS;
                    } else {

                        if (!result.toString().contains(CommonString.KEY_SUCCESS)) {
                            resulthttp = result.toString();
                        }
                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                            resulthttp = result + "in uploading coverage";
                        }
                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                            resulthttp = result + "in uploading coverage";
                        }

                    }
                }

                if (coverageBeanlist.size() > 0) {
                    for (int i = 0; i < coverageBeanlist.size(); i++) {

                        if (coverageBeanlist.get(i).getIntime_Image() != null
                                && !coverageBeanlist.get(i).getIntime_Image()
                                .equals("")) {

                            if (new File(CommonString.FILE_PATH + coverageBeanlist.get(i).getIntime_Image()).exists()) {
                                //result = UploadImage(addNewStoreList.get(j).getImg_Camera(), "StoreImages");
                                String result = uploadRetro.UploadImage2(coverageBeanlist.get(i).getIntime_Image(), "StoreImages", CommonString.FILE_PATH);
                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                        resulthttp = "intime Images not uploaded";
                                    } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                        resulthttp = "intime Images not uploaded";
                                    } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                                        throw new IOException();
                                    }
                                }

                            }
                        }

                      /*  if (coverageBeanlist.get(i).getOuttime_Image() != null
                                && !coverageBeanlist.get(i).getOuttime_Image()
                                .equals("")) {

                            if (new File(CommonString.FILE_PATH + coverageBeanlist.get(i).getOuttime_Image()).exists()) {
                                //result = UploadImage(addNewStoreList.get(j).getImg_Camera(), "StoreImages");
                                String result = uploadRetro.UploadImage2(coverageBeanlist.get(i).getOuttime_Image(), "StoreImages", CommonString.FILE_PATH);
                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                        resulthttp = "outtime Images not uploaded";
                                    } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                        resulthttp = "outtime Images not uploaded";
                                    } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                                        throw new IOException();
                                    }
                                }
                            }
                        }*/
                    }
                }

                return resulthttp;
            }
        } catch (MalformedURLException e) {
            return CommonString.MESSAGE_EXCEPTION;
        } catch (IOException e) {
            return CommonString.MESSAGE_SOCKETEXCEPTION;
        } catch (Exception e) {
            Crashlytics.logException(e);
            return CommonString.MESSAGE_EXCEPTION;
        }

        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        dismissProgressDialog();
        if (result.equals(CommonString.KEY_SUCCESS)) {
            if (msg.equalsIgnoreCase("InTimeData Uploading")) {
                db.open();

               // long id  = db.JcpUploadStatusData(cdata, CommonString.KEY_U);
                long id2 = db.InsertCoverageData(cdata);
                if (id2 > 0) {
                    if (msg.equalsIgnoreCase("InTimeData Uploading")) {
                        AlertandMessages.showToastMsg(context, "InTimeData Successfully Uploaded");
                    } else {
                        AlertandMessages.showToastMsg(context, "Non Working Data Successfully Uploaded");
                    }
                    Intent intent = new Intent(context,StoreEntryActivity.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();

                } else {
                    if (msg.equalsIgnoreCase("InTimeData Uploading")) {
                        AlertandMessages.showToastMsg(context, "InTimeData Not Uploaded");

                    } else {
                        AlertandMessages.showToastMsg(context, "Non Working Data Not Uploaded");
                    }
                    ((Activity) context).finish();
                }
            }else if(msg.equalsIgnoreCase("Non working Data Uploading")){
                db.open();

                long id  = db.JcpUploadStatusData(cdata, CommonString.KEY_U);
                //long id2 = db.InsertCoverageData(cdata);
                if (id > 0) {
                    if (msg.equalsIgnoreCase("InTimeData Uploading")) {
                        AlertandMessages.showToastMsg(context, "InTimeData Successfully Uploaded");
                    } else {
                        AlertandMessages.showToastMsg(context, "Non Working Data Successfully Uploaded");
                    }
                    Intent intent = new Intent(context,MainMenuActivity.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();

                } else {
                    if (msg.equalsIgnoreCase("InTimeData Uploading")) {
                        AlertandMessages.showToastMsg(context, "InTimeData Not Uploaded");

                    } else {
                        AlertandMessages.showToastMsg(context, "Non Working Data Not Uploaded");
                    }
                    ((Activity) context).finish();
                }
            }
        }
        else if (!result.equals("")) {
            AlertandMessages.showAlert((Activity) context, result, false);
        }
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

class Data {
    int value;
    String name;
}

