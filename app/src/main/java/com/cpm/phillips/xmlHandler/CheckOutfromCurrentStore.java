package com.cpm.phillips.xmlHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.cpm.phillips.constant.AlertandMessages;
import com.cpm.phillips.constant.CommonString;
import com.cpm.phillips.database.PhilipsAttendanceDB;
import com.cpm.phillips.getterSetter.CoverageBean;
import com.cpm.phillips.upload.Retrofit_method.UploadImageWithRetrofit;
import com.crashlytics.android.Crashlytics;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by neerajg on 2/21/2018.
 */

public class CheckOutfromCurrentStore extends AsyncTask<Void,Void,String>{

    Context context;
    ArrayList<CoverageBean> coverageBeanlist;
    ProgressDialog dialog;
    PhilipsAttendanceDB db;
    String msg;
    CoverageBean cdata;

    public CheckOutfromCurrentStore(Context context, String msg2, CoverageBean cdata) {
        this.context = context;
        this.msg = msg2;
        this.cdata = cdata;
        db = new PhilipsAttendanceDB(context);
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

        try{
            coverageBeanlist = new ArrayList<>();
            coverageBeanlist.add(cdata);
            UploadImageWithRetrofit uploadRetro = new UploadImageWithRetrofit(context);
            String resulthttp = "";
            if (coverageBeanlist.size() > 0) {
                for (int i = 0; i < coverageBeanlist.size(); i++) {

                    String onXML = "[DATA]"
                            + "[STORE_ID]" + coverageBeanlist.get(i).getStoreId() + "[/STORE_ID]"
                            + "[CHECKOUT_DATE]" + coverageBeanlist.get(i).getVisitDate() + "[/CHECKOUT_DATE]"
                            + "[LATITUDE]" + coverageBeanlist.get(i).getLatitude() + "[/LATITUDE]"
                            + "[LOGITUDE]" + coverageBeanlist.get(i).getLongitude() + "[/LOGITUDE]"
                            + "[CHECK_INTIME]" + coverageBeanlist.get(i).getInTime() + "[/CHECK_INTIME]"
                            + "[CHECK_OUTTIME]" + coverageBeanlist.get(i).getOutTime() + "[/CHECK_OUTTIME]"
                            + "[IMAGE_URL]" + coverageBeanlist.get(i).getOuttime_Image() + "[/IMAGE_URL]"
                            + "[USER_ID]" + coverageBeanlist.get(i).getUserId() + "[/USER_ID]"
                            + "[CREATED_BY]" + coverageBeanlist.get(i).getUserId() + "[/CREATED_BY]"
                            + "[/DATA]";


                    SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_STORE_CHCEKOUT);
                    request.addProperty("onXML", onXML);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);

                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_STORE_CHCEKOUT, envelope);
                    Object result = (Object) envelope.getResponse();

                    if (result.toString().contains(CommonString.KEY_SUCCESS)) {
                        //if (true) {
                        resulthttp = CommonString.KEY_SUCCESS;
                    } else {

                        if (!result.toString().contains(CommonString.KEY_SUCCESS)) {
                            resulthttp = result.toString();
                        }
                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                            resulthttp = result + "in checkOut";
                        }
                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                            resulthttp = result + "in checkOut";
                        }

                    }
                }

                if (coverageBeanlist.size() > 0) {
                    for (int i = 0; i < coverageBeanlist.size(); i++) {
                        if (coverageBeanlist.get(i).getOuttime_Image() != null
                                && !coverageBeanlist.get(i).getOuttime_Image()
                                .equals("")) {

                            if (new File(CommonString.FILE_PATH + coverageBeanlist.get(i).getOuttime_Image()).exists()) {
                                //result = UploadImage(addNewStoreList.get(j).getImg_Camera(), "StoreImages");
                                String result = uploadRetro.UploadImage2(coverageBeanlist.get(i).getOuttime_Image(), "StoreImages", CommonString.FILE_PATH);
                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                        resulthttp = "Outtime Images not uploaded";
                                    } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                        resulthttp = "Outtime Images not uploaded";
                                    } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                                        throw new IOException();
                                    }
                                }
                            }
                        }
                    }
                }

                return resulthttp;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            return CommonString.MESSAGE_EXCEPTION;
        }
     return "";
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        dialog.dismiss();
        if (result.equals(CommonString.KEY_SUCCESS)) {
            db.open();
            if (db.updateCoverageData(cdata,CommonString.KEY_C) > 0) {
                db.updateJcpStatusData(cdata, CommonString.KEY_C);
                AlertandMessages.showToastMsg(context, "Store Checkout Successfully ");
            } else {
                AlertandMessages.showToastMsg(context, "Store Not Checkout");
            }
            ((Activity) context).finish();
        }else  if (!result.equals("")) {
            AlertandMessages.showAlert((Activity) context, result, false);
        }
    }
}
