package com.cpm.phillipspc;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashScreenActivity extends AppCompatActivity {
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cpm.phillipspc.R.layout.activity_splash_screen);
        context = this;
        StartAnimations();
        {
            int SPLASH_TIME_OUT = 3000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity// close this activity
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(com.cpm.phillipspc.R.anim.activity_in, com.cpm.phillipspc.R.anim.activity_out);

                    finish();
                }
            }, SPLASH_TIME_OUT);
        }


    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, com.cpm.phillipspc.R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(com.cpm.phillipspc.R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);
        anim = AnimationUtils.loadAnimation(this, com.cpm.phillipspc.R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(com.cpm.phillipspc.R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);
    }


}
