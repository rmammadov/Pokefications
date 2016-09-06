package com.pogofications.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.pogofications.constants.Config;
import com.pogofications.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Call setup views
        setupView();

        //Call splash handler
        splashHandler();
    }

    /**
     * Setup View components
     */
    private void setupView() {
        Animation logoMoveAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
//        logoIV.startAnimation(logoMoveAnimation);
    }

    /**
     * Setup splash handler
     */
    private void splashHandler() {
        if(isOnline()) {
            new Handler().postDelayed(new Runnable() {
                // Showing splash screen with a timer.
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }, Config.SPLASH_TIME_OUT);
        }else {
            Toast.makeText(this,R.string.no_internet,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check network connection
     * @return
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
