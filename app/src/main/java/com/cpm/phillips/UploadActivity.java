package com.cpm.phillips;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.phillips.constant.CommonString;
import com.cpm.phillips.database.PhilipsAttendanceDB;
import com.cpm.phillips.getterSetter.CoverageBean;
import com.cpm.phillips.getterSetter.CustomerInfoGetterSetter;
import com.cpm.phillips.getterSetter.JCPMasterGetterSetter;
import com.cpm.phillips.getterSetter.SkuGetterSetter;
import com.cpm.phillips.getterSetter.VisitorDetailGetterSetter;
import com.cpm.phillips.upload.Retrofit_method.UploadImageWithRetrofit;
import com.crashlytics.android.Crashlytics;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity {

    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
    PhilipsAttendanceDB database;
    boolean upload_status, isError = false, up_success_flag = true;
    private ArrayList<SkuGetterSetter> skuData = new ArrayList<>();
    private ArrayList<CustomerInfoGetterSetter> cusInfoList = new ArrayList<>();
    Data data;
    private Dialog dialog;
    private ProgressBar pb;
    String dialogvalue = "Uploading Data";
    private SharedPreferences preferences;
    private TextView percentage, message, tv_title;
    private String date, user_name, app_ver, datacheck = "", resultFinal, exceptionMessage = "",validity;
    private CoverageBean coverageBean;
    private int factor;
    String[] words;
    int mid;
    ArrayList<VisitorDetailGetterSetter> visitorLoginData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        user_name = preferences.getString(CommonString.KEY_USERNAME, "");
        date = preferences.getString(CommonString.KEY_DATE, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        database = new PhilipsAttendanceDB(this);
        database.open();

        new UploadTask(this).execute();
    }

    public class UploadTask extends AsyncTask<Void,Data,String>{
        private Context context;
        public UploadTask(UploadActivity uploadActivity) {
            this.context = uploadActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("Uploading Data");
            dialog.setCancelable(false);
            dialog.show();
            pb =  dialog.findViewById(R.id.progressBar1);
            percentage = dialog.findViewById(R.id.percentage);
            message =  dialog.findViewById(R.id.message);
            tv_title =  dialog.findViewById(R.id.tv_title);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                data = new Data();
                coverageBeanlist = database.getCoverageData(date, user_name);

                if (coverageBeanlist.size() > 0) {
                    if (coverageBeanlist.size() == 1) {
                        factor = 50;
                    } else {
                        factor = 100 / (coverageBeanlist.size());
                    }
                }

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                //PJP DEVIATION
                String final_xml = "";
                String onXML = "";
                String onXML2="";
                String final_xml2 = "";
                database.open();

                for (int i = 0; i < coverageBeanlist.size(); i++) {
                    database.open();

                    if (!coverageBeanlist.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                        onXML = "";
                        onXML =
                                "[DATA][USER_DATA]"
                                        + "[STORE_CD]" + coverageBeanlist.get(i).getStoreId() + "[/STORE_CD]"
                                        + "[VISIT_DATE]" + coverageBeanlist.get(i).getVisitDate() + "[/VISIT_DATE]"
                                        + "[LATITUDE]" + coverageBeanlist.get(i).getLatitude() + "[/LATITUDE]"
                                        + "[APP_VERSION]" + app_ver + "[/APP_VERSION]"
                                        + "[LONGITUDE]" + coverageBeanlist.get(i).getLongitude() + "[/LONGITUDE]"
                                        + "[IN_TIME]" + coverageBeanlist.get(i).getInTime() + "[/IN_TIME]"
                                        + "[OUT_TIME]" + coverageBeanlist.get(i).getOutTime() + "[/OUT_TIME]"
                                        + "[UPLOAD_STATUS]" + "P" + "[/UPLOAD_STATUS]"
                                        + "[USER_ID]" + coverageBeanlist.get(i).getUserId() + "[/USER_ID]"
                                        + "[INTIME_IMAGE]" + coverageBeanlist.get(i).getIntime_Image() + "[/INTIME_IMAGE]"
                                        + "[OUTTIME_IMAGE]" + coverageBeanlist.get(i).getOuttime_Image() + "[/OUTTIME_IMAGE]"
                                        + "[REASON_ID]" + coverageBeanlist.get(i).getReasonid() + "[/REASON_ID]"
                                        + "[/USER_DATA][/DATA]";

                        SoapObject request = new SoapObject(CommonString.NAMESPACE,
                                CommonString.METHOD_COVERAGE_UPLOAD);
                        request.addProperty("onXML", onXML);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                        androidHttpTransport.call(CommonString.SOAP_ACTION +
                                CommonString.METHOD_COVERAGE_UPLOAD, envelope);
                        Object result = (Object) envelope.getResponse();
                        datacheck = result.toString();
                        datacheck = datacheck.replace("\"", "");
                        words = datacheck.split("\\;");
                        validity = (words[0]);


                        if (validity.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                          //  database.updateCoverageStatus(coverageBeanlist.get(i).getStoreId(), CommonString.KEY_U);
                            database.updateJCPStatus(coverageBeanlist.get(i).getStoreId(), CommonString.KEY_U);
                        } else {
                            isError = true;
                            continue;
                        }

                        mid = Integer.parseInt((words[1]));
                        data.value = 10;
                        data.name = "Coverage uploaded";
                        data.dialogname = dialogvalue;
                        publishProgress(data);


                        //Sale Data
                        final_xml = "";
                        onXML = "";
                        database.open();
                        skuData =  database.getSkuMasterDataUpload(coverageBeanlist.get(i).getStoreId());

                        if(skuData.size() >0) {
                            for (int j = 0; j < skuData.size(); j++) {
                                onXML = "[SALES_DATA]"
                                        + "[MID]" + mid + "[/MID]"
                                        + "[CREATED_BY]" + user_name + "[/CREATED_BY]"
                                        + "[SKU_CD]" + skuData.get(j).getSKU_CD().get(0) + "[/SKU_CD]"
                                        + "[OPENING_STOCK_QUY]" + skuData.get(j).getOPENING_STOCK_SKU_QUY() + "[/OPENING_STOCK_QUY]"
                                        + "[MID_DAY_STOCK_QUY]" + skuData.get(j).getMID_DAT_STOCK_SKU_QUY() + "[/MID_DAY_STOCK_QUY]"
                                        + "[CLOSING_STOCK_QUY]" + skuData.get(j).getCLOSING_STOCK_SKU_QUY() + "[/CLOSING_STOCK_QUY]"
                                        + "[VISITED_DATE]" + skuData.get(j).getVISITED_DATE() + "[/VISITED_DATE]"
                                        + "[/SALES_DATA]";

                                final_xml = final_xml + onXML;
                            }


                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", sos_xml);
                            request.addProperty("KEYS", "STOCK_DATA");
                            request.addProperty("USERNAME", user_name);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();

                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                isError = true;
                            }
                            data.value = 30;
                            data.name = "Sales Data";
                            data.dialogname = dialogvalue;
                            publishProgress(data);
                        }


                        //Customer Info Data
                        final_xml = "";
                        final_xml2 = "";
                        onXML = "";
                        onXML2="";
                        database.open();

                        cusInfoList = database.getCustomerInfoUploadData(coverageBeanlist.get(i).getStoreId());

                        if(cusInfoList.size() >0) {
                            for (int j = 0; j < cusInfoList.size(); j++) {

                                for(int k=0;k<cusInfoList.get(j).getList().size();k++){
                                    onXML2 = "[QUESTION]"
                                            + "[CUSTOMER_ID]" + cusInfoList.get(j).getList().get(k).getCustomer_Id() + "[/CUSTOMER_ID]"
                                            + "[QUESTION_ID]" + cusInfoList.get(j).getList().get(k).getQuestion_CD() + "[/QUESTION_ID]"
                                            + "[ANSWER_ID]" + cusInfoList.get(j).getList().get(k).getAnswer_CD() + "[/ANSWER_ID]"
                                            + "[/QUESTION]";

                                    final_xml2 = final_xml2 + onXML2;

                                }

                                onXML = "[CUSTOMER_INFO_DATA]"
                                        + "[MID]" + mid + "[/MID]"
                                        + "[CREATED_BY]" + user_name + "[/CREATED_BY]"
                                        + "[CUSTOMER_ID]" + cusInfoList.get(j).getUSER_ID() + "[/CUSTOMER_ID]"
                                        + "[USER_NAME]" + cusInfoList.get(j).getUSER_NAME() + "[/USER_NAME]"
                                        + "[USER_EMAIL]" + cusInfoList.get(j).getUSER_EMAIL() + "[/USER_EMAIL]"
                                        + "[USER_MOBILE]" + cusInfoList.get(j).getUSER_MOBILE() + "[/USER_MOBILE]"
                                        + "[VISITED_DATE]" + cusInfoList.get(j).getVISITED_DATE() + "[/VISITED_DATE]"
                                        + "[CUSTOMER_FEEDBACK]" + final_xml2 + "[/CUSTOMER_FEEDBACK]"
                                        + "[/CUSTOMER_INFO_DATA]";

                                    final_xml2 = "";
                                    final_xml = final_xml + onXML;
                            }


                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", sos_xml);
                            request.addProperty("KEYS", "CUSTOMER_INFO_DATA");
                            request.addProperty("USERNAME", user_name);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();

                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                isError = true;
                            }
                            data.value = 60;
                            data.name = "Customer Data";
                            data.dialogname = dialogvalue;
                            publishProgress(data);
                        }



                        visitorLoginData = database.getVisitorLoginData(date);
                        UploadImageWithRetrofit uploadRetro = new UploadImageWithRetrofit(context);

                        if(visitorLoginData.size()>0) {
                            for(int k=0;k<visitorLoginData.size();k++) {
                                if (!visitorLoginData.get(i).getUpload_status().equalsIgnoreCase(CommonString.KEY_U)) {
                                    String visitdata = "[USER_DATA][CREATED_BY]"
                                            + user_name
                                            + "[/CREATED_BY][EMP_ID]"
                                            + visitorLoginData.get(i).getEmp_code()
                                            + "[/EMP_ID][VISIT_DATE]"
                                            + date
                                            + "[/VISIT_DATE][IN_TIME_IMAGE]"
                                            + visitorLoginData.get(i).getIn_time_img()
                                            + "[/IN_TIME_IMAGE][OUT_TIME_IMAGE]"
                                            + visitorLoginData.get(i).getOut_time_img()
                                            + "[/OUT_TIME_IMAGE][IN_TIME]"
                                            + visitorLoginData.get(i).getIn_time()
                                            + "[/IN_TIME][OUT_TIME]"
                                            + visitorLoginData.get(i).getOut_time()
                                            + "[/OUT_TIME]"
                                            + "[/USER_DATA]";


                                    factory = XmlPullParserFactory
                                            .newInstance();
                                    factory.setNamespaceAware(true);
                                    xpp = factory.newPullParser();

                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                    request.addProperty("XMLDATA", visitdata);
                                    request.addProperty("KEYS", "MER_VISITOR_LOGIN");
                                    request.addProperty("USERNAME", user_name);
                                    request.addProperty("MID", 0);

                                    envelope = new SoapSerializationEnvelope(
                                            SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);

                                    androidHttpTransport = new HttpTransportSE(
                                            CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);

                                    result = (Object) envelope.getResponse();

                                    if (!result
                                            .toString()
                                            .equalsIgnoreCase(
                                                    CommonString.KEY_SUCCESS)) {

                                        return "Failure";

                                    }

                                    if (visitorLoginData.get(i).getIn_time_img() != null
                                            && !visitorLoginData.get(i).getIn_time_img()
                                            .equals("")) {

                                        if (new File(CommonString.FILE_PATH + visitorLoginData.get(i).getIn_time_img()).exists()) {
                                            //result = UploadImage(addNewStoreList.get(j).getImg_Camera(), "StoreImages");
                                            result = uploadRetro.UploadImage2(visitorLoginData.get(i).getIn_time_img(), "Visitor", CommonString.FILE_PATH);
                                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                                    return "Visitor Images";
                                                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                                    return "Visitor Images";
                                                } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                                                    throw new IOException();
                                                }
                                            }
                                            runOnUiThread(new Runnable() {

                                                public void run() {
                                                    // message.setText("Visitor Images Uploaded");
                                                }
                                            });

                                        }
                                    }

                                    if (visitorLoginData.get(i).getOut_time_img() != null
                                            && !visitorLoginData.get(i).getOut_time_img()
                                            .equals("")) {

                                        if (new File(CommonString.FILE_PATH + visitorLoginData.get(i).getOut_time_img()).exists()) {
                                            //result = UploadImage(addNewStoreList.get(j).getImg_Camera(), "StoreImages");
                                            result = uploadRetro.UploadImage2(visitorLoginData.get(i).getOut_time_img(), "Visitor", CommonString.FILE_PATH);
                                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                                    return "Visitor Images";
                                                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                                    return "Visitor Images";
                                                } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                                                    throw new IOException();
                                                }
                                            }

                                            runOnUiThread(new Runnable() {

                                                public void run() {
                                                    // message.setText("Visitor Images Uploaded");
                                                }
                                            });

                                        }
                                    }

                                    data.value = 80;
                                    data.name = "Visitor Login Data";
                                    data.dialogname = dialogvalue;
                                    publishProgress(data);
                                }
                            }
                        }


                        data.value = factor * (i + 1);
                        data.name = "Uploading";
                        data.dialogname = dialogvalue;
                        publishProgress(data);

                        // SET COVERAGE STATUS
                        if (!isError) {
                            String final_xml1 = "", onXML1 = "";
                            onXML1 = "[COVERAGE_STATUS]"
                                    + "[STORE_ID]" + coverageBeanlist.get(i).getStoreId() + "[/STORE_ID]"
                                    + "[VISIT_DATE]" + coverageBeanlist.get(i).getVisitDate() + "[/VISIT_DATE]"
                                    + "[USER_ID]" + user_name + "[/USER_ID]"
                                    + "[STATUS]" + CommonString.KEY_U + "[/STATUS]"
                                    + "[/COVERAGE_STATUS]";

                            final_xml1 = final_xml1 + onXML1;

                            final String sos_xml = "[DATA]" + final_xml1 + "[/DATA]";
                            SoapObject request1 = new SoapObject(CommonString.NAMESPACE, CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS);
                            request1.addProperty("onXML", sos_xml);
                            SoapSerializationEnvelope envelope1 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope1.dotNet = true;
                            envelope1.setOutputSoapObject(request1);
                            HttpTransportSE androidHttpTransport1 = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport1.call(CommonString.SOAP_ACTION +
                                    CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS, envelope1);
                            Object result1 = (Object) envelope1.getResponse();
                            if (result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                database.open();
                                database.deleteSpecificTables(coverageBeanlist.get(i).getStoreId());
                                database.updateJCPStatus(coverageBeanlist.get(i).getStoreId(), CommonString.KEY_U);

                            }
                            if (!result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                isError = true;
                                continue;
                            }
                            data.value = 100;
                            data.name = "Coverage Status";
                            data.dialogname = dialogvalue;
                            publishProgress(data);
                            resultFinal = result1.toString();
                        }else{
                            up_success_flag = false;
                        }
                    }
                }

            }catch (MalformedURLException e) {
                up_success_flag = false;
                exceptionMessage = e.toString();

            } catch (IOException e) {
                up_success_flag = false;
                exceptionMessage = e.toString();

            } catch (Exception e) {
                Crashlytics.logException(e);
                up_success_flag = false;
                exceptionMessage = e.toString();
            }

            if (up_success_flag) {
                return resultFinal;
            } else {
                return exceptionMessage;
            }
        }


        @Override
        protected void onProgressUpdate(Data... values) {
            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);
            tv_title.setText(values[0].dialogname);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (isError) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                builder.setTitle("Parinaam");
                builder.setMessage("Uploaded successfully with some problem . Please try again").setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //temporary code
                                Intent i = new Intent(getBaseContext(), MainMenuActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                if (result.equals(CommonString.KEY_SUCCESS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                    builder.setTitle("Parinaam");
                    builder.setMessage(CommonString.MESSAGE_UPLOAD_DATA).setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getBaseContext(), MainMenuActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else if (result.equals(CommonString.KEY_FAILURE) || !result.equals("")) {
                    message(CommonString.ERROR + result);
                }
            }
        }
    }

    class Data {
        int value;
        String name;
        String dialogname;
    }


    private void message(String str) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Parinaam").setMessage(str);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(UploadActivity.this, MainMenuActivity.class));
                finish();
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
