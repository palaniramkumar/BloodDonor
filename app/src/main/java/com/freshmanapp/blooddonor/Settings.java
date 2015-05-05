package com.freshmanapp.blooddonor;

import android.os.Bundle;
import android.preference.PreferenceActivity;


/**
 * Created by Ramkumar on 06/05/15.
 */
public class Settings extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref);
        //Initialize the "up" button
   //     getActionBar().setDisplayHomeAsUpEnabled(true);

    }
}