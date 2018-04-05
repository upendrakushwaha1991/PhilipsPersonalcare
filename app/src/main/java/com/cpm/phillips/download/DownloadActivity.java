package com.cpm.phillips.download;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.phillips.R;
import com.cpm.phillips.bean.TableBean;
import com.cpm.phillips.constant.AlertandMessages;
import com.cpm.phillips.constant.CommonString;
import com.cpm.phillips.database.PhilipsAttendanceDB;
import com.cpm.phillips.getterSetter.AnswersGetterSetter;
import com.cpm.phillips.getterSetter.JCPMasterGetterSetter;
import com.cpm.phillips.getterSetter.NonWorkingReasonGetterSetter;
import com.cpm.phillips.getterSetter.QuestionsGetterSetter;
import com.cpm.phillips.getterSetter.SkuGetterSetter;
import com.cpm.phillips.getterSetter.VisitorLoginGetterSetter;
import com.cpm.phillips.xmlHandler.XMLHandlers;
import com.crashlytics.android.Crashlytics;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

public class DownloadActivity extends AppCompatActivity {

    PhilipsAttendanceDB db;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    Context context;
    int downloadindex = 0;
    String userId, date;
    private Dialog dialog;
    private ProgressBar pb;
    private Data data;
    int eventType;
    private TextView percentage, message;
    JCPMasterGetterSetter jcpMasterGetterSetter;
    NonWorkingReasonGetterSetter nonWorkingReasonGetterSetter;
    VisitorLoginGetterSetter visitorLoginGetterSetter;
    QuestionsGetterSetter questionsGetterSetter;
    AnswersGetterSetter answersGetterSetter;
    SkuGetterSetter skuGetterSetter;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        declaration();
        new BackgroundTask().execute();
        value = getIntent().getStringExtra("flag_status");
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing() || !DownloadActivity.this.isFinishing() && dialog.isShowing()  ) {
            dialog.dismiss();
        }
    }


    private class BackgroundTask extends AsyncTask<Void, Data, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom);
            //dialog.setTitle("Download Files");
            dialog.setCancelable(false);
            dialog.show();

            pb =  dialog.findViewById(R.id.progressBar1);
            percentage =  dialog.findViewById(R.id.percentage);
            message = dialog.findViewById(R.id.message);
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultHttp = "";
            try {
                data = new Data();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                SoapObject request;
                SoapSerializationEnvelope envelope;
                HttpTransportSE androidHttpTransport;

                //region JOURNEY_PLAN
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", userId);
                request.addProperty("Type", "JOURNEY_PLAN");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, CommonString.TIMEOUT);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);

                Object resultJcp = (Object) envelope.getResponse();

                if (resultJcp.toString() != null) {
                    xpp.setInput(new StringReader(resultJcp.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    jcpMasterGetterSetter = XMLHandlers.JCPMasterXMLHandler(xpp, eventType);

                    String jcpMasterTable = jcpMasterGetterSetter.getTable_JOURNEY_PLAN();
                    TableBean.setTable_JCPMaster(jcpMasterTable);

                    if (jcpMasterGetterSetter.getSTORE_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                    } else {
                        return "JOURNEY_PLAN";
                    }
                    data.value = 5;
                    data.name = "JOURNEY_PLAN Downloading";
                }
                publishProgress(data);
                //endregion


                //region NON_WORKING_REASON
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", userId);
                request.addProperty("Type", "NON_WORKING_REASON");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, CommonString.TIMEOUT);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);

                Object result = (Object) envelope.getResponse();

                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    nonWorkingReasonGetterSetter = XMLHandlers.nonWorkinReasonXML(xpp, eventType);

                    if (nonWorkingReasonGetterSetter.getReason_cd().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String nonWorkingTable = nonWorkingReasonGetterSetter.getNonworking_table();
                        TableBean.setNonworkingtable(nonWorkingTable);
                    } else {
                        return "Non Working Data";
                    }

                    data.value = 10;
                    data.name = "Non Working Data Downloading";
                }
                publishProgress(data);
                //endregion




             /*   //region VISITOR_DETAIL
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", userId);
                request.addProperty("Type", "VISITOR_DETAIL");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, CommonString.TIMEOUT);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);

                result = (Object) envelope.getResponse();

                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    visitorLoginGetterSetter = XMLHandlers.visitorLoginXMLHandler(xpp, eventType);

                    if (visitorLoginGetterSetter.getEMP_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String visitorLoginTable = visitorLoginGetterSetter.getTable_VisitorLogin();
                        TableBean.setVisitorLogintable(visitorLoginTable);
                    } else {
                        return "Visitor Login Data";
                    }

                    data.value = 10;
                    data.name = "Visitor Login Data";
                }
                publishProgress(data);*/
                //endregion

                //region QUESTIONS
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", userId);
                request.addProperty("Type", "QUESTIONS");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, CommonString.TIMEOUT);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);

                result = (Object) envelope.getResponse();

                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    questionsGetterSetter = XMLHandlers.QUESTIONSXMLHandler(xpp, eventType);

                    if (questionsGetterSetter.getQUESTION_ID().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String questionsTable = questionsGetterSetter.getTable_QUESTIONS();
                        TableBean.setQuestionsTable(questionsTable);
                    } else {
                        return "QUESTIONS Data";
                    }
                    data.value = 25;
                    data.name = "QUESTIONS Data";
                }
                publishProgress(data);
                //endregion


                //region ANSWERS
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", userId);
                request.addProperty("Type", "ANSWERS");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, CommonString.TIMEOUT);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);

                result = (Object) envelope.getResponse();

                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    answersGetterSetter = XMLHandlers.ANSWERSXMLHandler(xpp, eventType);

                    if (answersGetterSetter.getQUESTION_ID().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String answersTable = answersGetterSetter.getTable_ANSWERS();
                        TableBean.setAnswersTable(answersTable);

                    } else {
                        return "ANSWERS Data";
                    }

                    data.value = 50;
                    data.name = "ANSWERS Data";
                }
                publishProgress(data);


                //region ANSWERS
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", userId);
                request.addProperty("Type", "SKU_MASTER");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, CommonString.TIMEOUT);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);

                result = (Object) envelope.getResponse();

                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    skuGetterSetter = XMLHandlers.SKUXMLHandler(xpp, eventType);

                    if (skuGetterSetter.getSKU_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String skuMasterTable = skuGetterSetter.getTable_SKU_MASTER();
                        TableBean.setSkuMasterTable(skuMasterTable);

                    } else {
                        return "SKU Data";
                    }

                    data.value = 70;
                    data.name = "SKU Data";
                }
                publishProgress(data);



                //endregion

                db = new PhilipsAttendanceDB(context);
                db.open();

                if (jcpMasterGetterSetter.getSTORE_CD().size() > 0) {
                    db.insertJCPMasterData(jcpMasterGetterSetter);
                }

                if (nonWorkingReasonGetterSetter.getReason_cd().size() > 0) {
                    db.insertNonWorkingData(nonWorkingReasonGetterSetter);
                }

                if (questionsGetterSetter.getQUESTION_ID().size() > 0) {
                    db.insertQuestionsData(questionsGetterSetter);
                }
                if (answersGetterSetter.getQUESTION_ID().size() > 0) {
                    db.insertAnswersData(answersGetterSetter);
                }

                if (skuGetterSetter.getSKU_CD().size() > 0) {
                    db.insertSKUMasterData(skuGetterSetter);
                }

                editor = preferences.edit();
                editor.putBoolean(CommonString.KEY_ISDATADOWNLOADED, true);
                editor.commit();

                data.value = 100;
                data.name = "Finishing";
                publishProgress(data);

                return resultHttp;
            } catch (MalformedURLException e) {
                return CommonString.MESSAGE_EXCEPTION;
            } catch (IOException e) {
                return CommonString.MESSAGE_SOCKETEXCEPTION;
            } catch (Exception e) {
                Crashlytics.logException(e);
                return CommonString.MESSAGE_EXCEPTION;
            }
        }

        @Override
        protected void onProgressUpdate(Data... values) {
            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if (result.equals(CommonString.KEY_SUCCESS)) {
                AlertandMessages.showAlert2((Activity) context, CommonString.MESSAGE_DOWNLOAD, true,value);
            } else {
                AlertandMessages.showAlert2((Activity) context, CommonString.MESSAGE_JCP_FALSE + " " + result, true,value);
            }
        }
    }

    class Data {
        int value;
        String name;
    }

    void declaration() {
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        date = preferences.getString(CommonString.KEY_DATE, "");
        downloadindex = preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
        getSupportActionBar().setTitle("Download - " + date);

    }

}
