package com.freshmanapp.blooddonor.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.freshmanapp.blooddonor.MyNavigationDrawer;
import com.freshmanapp.blooddonor.R;
import com.freshmanapp.blooddonor.helper.NotificationHelper;
import com.freshmanapp.blooddonor.model.User;
import com.freshmanapp.blooddonor.util.AccountUtils;
import com.freshmanapp.blooddonor.util.HTMLOperations;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramkumar on 09/04/15.
 */
public class RegisterAccountTask extends AsyncTask<String, Void, String>{ //param arg,proress,do param retun
    private NotificationHelper mNotificationHelper;
    private ProgressDialog Dialog;
    private Context mContext;
    private String Error = null;
    private User user;

    public RegisterAccountTask(Context context,User user){
        mNotificationHelper = new NotificationHelper(context);
        Dialog = new ProgressDialog(context);
        mContext = context;
        this.user = user;
    }
    protected void onPreExecute() {

        mNotificationHelper.createNotification("Donors Cloud", "Creating Account");

    }
    protected String doInBackground(String... urls) {

        /************ Make Post Call To Web Server ***********/

        String str = "";
        // Send data
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urls[0]);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("action", "NEW_ACCOUNT"));
            nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
            nameValuePairs.add(new BasicNameValuePair("lat", user.getLat()));
            nameValuePairs.add(new BasicNameValuePair("lon", user.getLon()));
            nameValuePairs.add(new BasicNameValuePair("location", user.getLocation()));
            nameValuePairs.add(new BasicNameValuePair("dob", user.getDOB()));
            nameValuePairs.add(new BasicNameValuePair("weight", user.getWeight()));
            nameValuePairs.add(new BasicNameValuePair("mobile", user.getMobile()));
            nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
            nameValuePairs.add(new BasicNameValuePair("blood_type", "A+"));
            nameValuePairs.add(new BasicNameValuePair("profile_pic", user.getProfilePic()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            Log.d("url", urls[0]);
            Log.d("action", "NEW_ACCOUNT");
            Log.d("name", user.getName());
            Log.d("lat", user.getLat());
            Log.d("lon", user.getLon());
            Log.d("location",user.getLocation());
            Log.d("dob", user.getDOB());
            Log.d("weight", user.getWeight());
            Log.d("mobile", user.getMobile());
            Log.d("email", user.getEmail());
            Log.d("blood_type", user.getBloodType());
            Log.d("profile_pic", user.getProfilePic());


            HttpResponse response = httpclient.execute(httppost);

            str = HTMLOperations.inputStreamToString(response.getEntity().getContent()).toString();
            str = httppost.getRequestLine().toString();

        } catch (Exception ex) {
            Error = ex.getMessage();
        }

        /*****************************************************/

        return str;
    }


    protected void onPostExecute(String result) {
        SharedPreferences preferences =  mContext.getSharedPreferences("REGISTER_ID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("rid", result);
        editor.commit();
        Dialog.dismiss();
        Toast.makeText(mContext, "Account Synced for id "+result, Toast.LENGTH_LONG).show();
        Intent i = new Intent(mContext, MyNavigationDrawer.class);//MainActivity
        mContext.startActivity(i);
        ((Activity)mContext).finish();
       // new LinkAccountTask(mContext).execute(mContext.getResources().getString(R.string.host));
    }
}
