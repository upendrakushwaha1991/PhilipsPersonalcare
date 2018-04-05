package com.cpm.phillips.constant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.cpm.phillips.LoginActivity;
import com.cpm.phillips.MainMenuActivity;
import com.cpm.phillips.R;
import com.cpm.phillips.dailyEntry.CustomerInfoActivity;
import com.cpm.phillips.dailyEntry.StoreListActivity;
import com.cpm.phillips.database.PhilipsAttendanceDB;
import com.cpm.phillips.getterSetter.CustomerInfoGetterSetter;

import java.util.ArrayList;

/**
 * Created by deepakp on 8/10/2017.
 */

@SuppressWarnings("deprecation")
public class AlertandMessages {

    private String data, condition;
    private Activity activity;
    PhilipsAttendanceDB  db = null;
    SharedPreferences preferences;
    String visit_date, username,store_cd;

    public AlertandMessages(Activity activity, String data, String condition, Exception exception) {
        this.activity = activity;
        this.data = data;
        this.condition = condition;
    }

    public static void showSnackbarMsg(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackbarMsg(Context context, String message) {
        Snackbar.make(((Activity) context).getCurrentFocus(), message, Snackbar.LENGTH_SHORT).show();
    }


    public static void showToastMsg(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void editorDeleteAlert(String str, final Runnable task) {

        final AlertDialog builder = new AlertDialog.Builder(activity).create();
        builder.setTitle("Alert");
        builder.setMessage(str);
        builder.setCancelable(false);
        builder.setButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                task.run();

            }
        });
        builder.setButton2("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    public void backpressedAlertWithTaskRun(final Activity activity, String str, final Runnable task) {

        final AlertDialog builder = new AlertDialog.Builder(activity).create();
        builder.setTitle("Alert");
        builder.setMessage(str);
        builder.setCancelable(false);
        builder.setButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                task.run();
                activity.finish();
                activity.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }
        });
        builder.setButton2("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.dismiss();
            }
        });
        builder.show();
    }


    public void backpressedAlert() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to exit? Unsaved data will be lost").setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.finish();
                        activity.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void backpressedAlertWithMessage(String msg) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Alert");
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.finish();
                        activity.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void backpressedAlertforPosm(final Context context) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to exit? Filled data will be lost").setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      /*  Intent i = new Intent(context, POSMSubMenuActivity.class);
                        ((Activity) context).setResult(Activity.RESULT_CANCELED, i);
                        activity.finish();*/
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public static void showAlert(final Activity activity, String str, final Boolean activityFinish) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (activityFinish) {
                            activity.finish();
                        } else {
                            dialog.dismiss();
                        }

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



    public static void showAlert2(final Activity activity, String str, final Boolean activityFinish, final String value) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (activityFinish) {
                            if(value.equalsIgnoreCase("1")){
                                Intent intent = new Intent(activity, MainMenuActivity.class);
                                activity.startActivity(intent);
                            }else{
                                Intent intent = new Intent(activity, StoreListActivity.class);
                                activity.startActivity(intent);
                            }

                        } else {
                            dialog.dismiss();
                        }

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showAlertOldImageUpload(final Activity activity, String str, final Boolean activityFinish) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (activityFinish) {
                            activity.finish();
                        } else {
                            dialog.dismiss();
                            Intent i = new Intent(activity, LoginActivity.class);
                            activity.startActivity(i);
                            activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            activity.finish();
                        }

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void saveCustomerInfo(final CustomerInfoActivity customerInfoActivity, final ArrayList<CustomerInfoGetterSetter> customerList) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to Save data ?").setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        db = new PhilipsAttendanceDB(customerInfoActivity);
                        db.open();
                        preferences = PreferenceManager.getDefaultSharedPreferences(customerInfoActivity);
                        visit_date = preferences.getString(CommonString.KEY_DATE, null);
                        username = preferences.getString(CommonString.KEY_USERNAME, null);
                        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
                        db.insertCutomerData(customerList,visit_date,username,store_cd);
                      //  showSnackbarMsg(customerInfoActivity,"User Info saved successfully");
                        activity.finish();
                        activity.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
