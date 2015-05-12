package com.freshmanapp.blooddonor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


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
        final CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager().findPreference("prefVisibility");

        checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    Boolean boolVal = (Boolean) newValue;
                    //disable the visibility status in the cloud
                    SharedPreferences preferences = getSharedPreferences("REGISTER_ID", Context.MODE_PRIVATE);
                    String id = preferences.getString("rid", "");
                    setVisibility(id,(boolVal) ? 0 : 1);

                }
                return true;
            }
        });

    }

    private void showNotification(String eventtext) {

        //We get a reference to the NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String MyText = "Update!";
        Notification mNotification = new Notification(R.drawable.ic_friends, MyText, System.currentTimeMillis() );
        //The three parameters are: 1. an icon, 2. a title, 3. time when the notification appears

        String MyNotificationTitle = "Donor's Cloud";
        String MyNotificationText  = eventtext;

        Intent MyIntent = new Intent(Intent.ACTION_VIEW);
        PendingIntent StartIntent = PendingIntent.getActivity(getApplicationContext(),0,MyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //A PendingIntent will be fired when the notification is clicked. The FLAG_CANCEL_CURRENT flag cancels the pendingintent

        mNotification.setLatestEventInfo(getApplicationContext(), MyNotificationTitle, MyNotificationText, StartIntent);

        int NOTIFICATION_ID = 1;
        notificationManager.notify(NOTIFICATION_ID, mNotification);
        //We are passing the notification to the NotificationManager with a unique id.
    }

    void setVisibility(String id,int status) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.host)+"?uid="+id+"&status="+status+"&action=UPDATE_VISIBILITY";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //response message
                        showNotification("Successfully Updated your visibility");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //response Error Message
                showNotification("Failed to Updated your visibility");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public static Boolean getPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, true);
    }

}