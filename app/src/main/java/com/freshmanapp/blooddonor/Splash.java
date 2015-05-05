package com.freshmanapp.blooddonor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.freshmanapp.blooddonor.util.GCM;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ramkumar on 14/04/15.
 */
public class Splash extends Activity {
    private static int SPLASH_TIME_OUT = 1000;



    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regid;


    protected void onCreate(Bundle bundle)
    {
        startService(new Intent(this, GcmIntentService.class));

        super.onCreate(bundle);
        setContentView(R.layout.splash);

        GCM gcmOp = new GCM(getBaseContext());

        if (gcmOp.checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            regid = gcmOp.getRegistrationId(getApplicationContext());

            Log.i("Registered Id ",regid);
            if (regid.isEmpty()) {
                new RegisterApp(getApplicationContext(), gcm, gcmOp.getAppVersion(getApplicationContext())).execute();
                //Toast.makeText(getApplicationContext(), "Device Registered Now", Toast.LENGTH_SHORT).show();
                Log.d("Device Registered Now" , regid+"");
            }else{
                //Toast.makeText(getApplicationContext(), "Device already Registered ("+regid+")", Toast.LENGTH_SHORT).show();
                Log.d("already Registered" , regid+"");
            }

        }

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
