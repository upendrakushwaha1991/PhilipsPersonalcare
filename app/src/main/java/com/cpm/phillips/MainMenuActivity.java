package com.cpm.phillips;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.cpm.phillips.constant.AlertandMessages;
import com.cpm.phillips.constant.CommonFunctions;
import com.cpm.phillips.constant.CommonString;
import com.cpm.phillips.dailyEntry.ClientFeedbackActivity;
import com.cpm.phillips.dailyEntry.StoreListActivity;
import com.cpm.phillips.dailyEntry.StoreListForCampaignActivity;
import com.cpm.phillips.dailyEntry.VisitorLoginActivity;
import com.cpm.phillips.database.PhilipsAttendanceDB;
import com.cpm.phillips.download.DownloadActivity;
import com.cpm.phillips.getterSetter.CoverageBean;
import com.cpm.phillips.getterSetter.JCPMasterGetterSetter;
import com.cpm.phillips.upload.Retrofit_method.UploadImageWithRetrofit;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    private WebView webView;
    private ImageView imageView;
    private SharedPreferences preferences;
    Intent  visitorIntent,exitIntent,storeListIntent;
    String visit_date_formatted, user_name,date,url;
    PhilipsAttendanceDB database;

    ArrayList<JCPMasterGetterSetter> jcplist;
    ArrayList<CoverageBean> coverageBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cpm.phillips.R.layout.activity_main_menu);
        declaration();
        exitIntent = new Intent(context, LoginActivity.class);
        visitorIntent = new Intent(context, VisitorLoginActivity.class);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);


        database = new PhilipsAttendanceDB(this);
        database.open();

        if (!url.equals("")) {
            webView.loadUrl(url);
        }
        File file = new File(Environment.getExternalStorageDirectory(), CommonString.IMAGES_FOLDER_NAME);
        if (!file.isDirectory()) {
            file.mkdir();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(com.cpm.phillips.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            overridePendingTransition(com.cpm.phillips.R.anim.activity_back_in, com.cpm.phillips.R.anim.activity_back_out);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.cpm.phillips.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == com.cpm.phillips.R.id.dailyEntry) {
            startActivity(storeListIntent);
            overridePendingTransition(com.cpm.phillips.R.anim.activity_in, com.cpm.phillips.R.anim.activity_out);
        } else if (id == com.cpm.phillips.R.id.downloadData) {
            if (CommonFunctions.checkNetIsAvailable(context)) {
                if (database.isCoverageDataFilled(date)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);
                    builder.setTitle("Parinaam");
                    builder.setMessage("Please Upload Previous Data First")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent startUpload = new Intent(MainMenuActivity.this, CheckoutNUploadActivity.class);
                                    startActivity(startUpload);
                                    finish();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    try {
                        Intent startDownload = new Intent(context, DownloadActivity.class);
                        startDownload.putExtra("flag_status","1");
                        startActivity(startDownload);

                        overridePendingTransition(com.cpm.phillips.R.anim.activity_in, com.cpm.phillips.R.anim.activity_out);
                        finish();
                      //  database.open();
                       // database.deletePreviousUploadedData(visit_date);
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                    }

                }
            } else {
                AlertandMessages.showSnackbarMsg(context, CommonString.MESSAGE_INTERNET_NOT_AVAILABLE);
            }
        }else if (id == com.cpm.phillips.R.id.visitorLogin) {
            if (preferences.getBoolean(CommonString.KEY_ISDATADOWNLOADED, false)) {
                startActivity(visitorIntent);
                overridePendingTransition(com.cpm.phillips.R.anim.activity_in, com.cpm.phillips.R.anim.activity_out);
            } else {
                AlertandMessages.showSnackbarMsg(context, "Please download data");
            }
        }
        else if (id == com.cpm.phillips.R.id.exit) {
            exitIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(exitIntent);
            overridePendingTransition(com.cpm.phillips.R.anim.activity_back_in, com.cpm.phillips.R.anim.activity_back_out);
            finish();
        }else if (id == R.id.uploadData) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Parinaam");
            builder1.setMessage("Do you want to upload data")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (CommonFunctions.checkNetIsAvailable(context))
                            {
                                jcplist = database.getStoreData(date,user_name);
                                //it works even there is no jcp
                                database.open();
                                if (jcplist.size() > 0)
                                {
                                    coverageBeans = database.getCoverageData(date,user_name);

                                    if(isStoreInvalid(coverageBeans)){
                                        AlertandMessages.showSnackbarMsg(context, "First checkout of current store");
                                    }else{
                                        if(coverageBeans.size() == 0){
                                            AlertandMessages.showSnackbarMsg(context, CommonString.MESSAGE_NO_DATA);
                                        }else{
                                            //  uploadBackupUsingRetro();
                                            Intent i = new Intent(getBaseContext(), UploadActivity.class);
                                          //  i.putExtra("UploadAll", false);
                                            startActivity(i);
                                        }
                                    }
                                } else {
                                    AlertandMessages.showSnackbarMsg(context, "Please Download Data First");
                                }
                            } else {
                                AlertandMessages.showSnackbarMsg(context, "No Network Available");
                            }

                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();

                                }
                            });
            AlertDialog alert = builder1.create();
            alert.show();
        }
        else if (id == com.cpm.phillips.R.id.nav_export_database) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Are you sure you want to take the backup of your data")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @SuppressWarnings("resource")
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                File file = new File(Environment.getExternalStorageDirectory(), "philips_backup");
                                if (!file.isDirectory()) {
                                    file.mkdir();
                                }

                                File sd = Environment.getExternalStorageDirectory();
                                File data = Environment.getDataDirectory();

                                if (sd.canWrite()) {
                                    long date = System.currentTimeMillis();

                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yy");
                                    String dateString = sdf.format(date);

                                    String currentDBPath = "//data//com.cpm.phillips//databases//" + PhilipsAttendanceDB.DATABASE_NAME;
                                    //String backupDBPath = "PngAttendance_backup" + dateString.replace('/', '-');
                                    String backupDBPath = "Philips_" + user_name.replace(".", "") + "_backup-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS().replace(":", "") + ".db";

                                    String path = Environment.getExternalStorageDirectory().getPath() + "/philips_backup";

                                    File currentDB = new File(data, currentDBPath);
                                    File backupDB = new File(path, backupDBPath);

                                    AlertandMessages.showSnackbarMsg(context, "Database Exported Successfully");

                                    if (currentDB.exists()) {
                                        @SuppressWarnings("resource")
                                        FileChannel src = new FileInputStream(currentDB).getChannel();
                                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                        dst.transferFrom(src, 0, src.size());
                                        src.close();
                                        dst.close();
                                    }

                                  new uploadbackup(backupDBPath).execute();

                                }
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                                System.out.println(e.getMessage());

                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert1 = builder1.create();
            alert1.show();
        }


        DrawerLayout drawer = findViewById(com.cpm.phillips.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public boolean isStoreInvalid(ArrayList<CoverageBean> coverage) {
        boolean flag_is_invalid = false;
        for (int i = 0; i < coverage.size(); i++) {
            if (coverage.get(i).getStatus().equals(CommonString.KEY_VALID) || coverage.get(i).getStatus().equals(CommonString.KEY_CHECK_IN)) {
                flag_is_invalid = true;
                break;
            }
        }
        return flag_is_invalid;
    }


    private class uploadbackup extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String backupName;

        uploadbackup(String backupName) {
            this.backupName = backupName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Uploading Backup");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String path = Environment.getExternalStorageDirectory().getPath()+"/philips_backup";
            File backupfile = new File(path + "/" + backupName);
            if (backupfile.exists()) {
                UploadImageWithRetrofit uploadRetro = new UploadImageWithRetrofit(context);
                uploadRetro.uploadBackupWithRetrofit(backupfile, "DBBackup", path, pd);
            }
            return "";
        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            imageView.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
            view.clearCache(true);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    void declaration() {
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar =  findViewById(com.cpm.phillips.R.id.toolbar);
        setSupportActionBar(toolbar);
        url = preferences.getString(CommonString.KEY_NOTICE_BOARD_LINK, "");
        user_name = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date_formatted = preferences.getString(CommonString.KEY_YYYYMMDD_DATE, "");
        date = preferences.getString(CommonString.KEY_DATE, null);
        storeListIntent = new Intent(context, StoreListActivity.class);
       // campaignIntent = new Intent(context, StoreListForCampaignActivity.class);
      //  clientFeedbackIntent = new Intent(context, ClientFeedbackActivity.class);
        imageView =  findViewById(com.cpm.phillips.R.id.img_main);
        webView =  findViewById(com.cpm.phillips.R.id.webview);


        DrawerLayout drawer = (DrawerLayout) findViewById(com.cpm.phillips.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.cpm.phillips.R.string.navigation_drawer_open, com.cpm.phillips.R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(com.cpm.phillips.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //((TextView)findViewById(R.id.textview)).setText(user_name);
    }
}
