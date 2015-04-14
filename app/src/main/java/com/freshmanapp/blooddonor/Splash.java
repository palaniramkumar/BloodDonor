package com.freshmanapp.blooddonor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;

/**
 * Created by Ramkumar on 14/04/15.
 */
public class Splash extends Activity {
    private static int SPLASH_TIME_OUT = 1000;
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.splash);
        (new Handler()).postDelayed(new Runnable() {

        public void run() {
            if (getSharedPreferences("REGISTER_ID", 0).getString("rid", "").equalsIgnoreCase("")) {
                Intent intent = new Intent(Splash.this, Registration.class);
                startActivity(intent);
                finish();
                return;
            } else {
                Intent intent = new Intent(Splash.this, MyNavigationDrawer.class);
                startActivity(intent);
                finish();
                return;
            }
        }
    }, SPLASH_TIME_OUT);
    }
}
