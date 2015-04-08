package com.freshmanapp.blooddonor.service;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.freshmanapp.blooddonor.helper.NotificationHelper;
import com.freshmanapp.blooddonor.util.HTMLOperations;
import com.freshmanapp.blooddonor.util.Phone;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramkumar on 09/04/15.
 */
public class LinkAccountTask extends AsyncTask<String, Integer, String> { //<do param arg,proress,do param retun
    /**
     * The system calls this to perform work in a worker thread and
     * delivers it the parameters given to AsyncTask.execute()
     */

    String data = "";
    private String Content;
    private String Error = null;
    private ProgressDialog Dialog;
    private Context mContext;
    private NotificationHelper mNotificationHelper;

    public LinkAccountTask(Context context) {
        mNotificationHelper = new NotificationHelper(context);
        Dialog = new ProgressDialog(context);
        mContext = context;
    }

    protected void onPreExecute() {

        mNotificationHelper.createNotification("Syncing", "Checking Contacts");

    }

    protected String doInBackground(String... urls) {

        /*****Get Contacts *****/
        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        final int totalContacts = cursor.getCount();
        String displayName = "", phoneNumber = "", rsp = "";
        StringBuilder str = new StringBuilder();
        int i = 0;
        while (cursor.moveToNext()) {

            publishProgress((int) ((i / (float) totalContacts) * 100));
            i++;

            phoneNumber = "";
            displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                while (pCur.moveToNext()) {
                    phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    break;
                }
                pCur.close();
            }
            Phone ph = new Phone(mContext, phoneNumber.replaceAll("[^\\p{L}\\p{Nd}]", ""));
            if (!ph.getMobileNo().equalsIgnoreCase("")) {
                Log.d("Contacts", "DisplayName: " + displayName + ", PhoneNumber (" + phoneNumber + "): " + ph.getCountryCode() + "," + ph.getMobileNo());// + phoneNumber.replaceAll("[^\\p{L}\\p{Nd}]", ""));
                str.append(ph.getCountryCode() + ph.getMobileNo());
                str.append(",");
            }
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        cursor.close();

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urls[0]);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("action", "LINK_FRIENDS"));
            nameValuePairs.add(new BasicNameValuePair("uid", "1"));
            nameValuePairs.add(new BasicNameValuePair("contacts", str.toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);

            rsp = HTMLOperations.inputStreamToString(response.getEntity().getContent()).toString();
            rsp = httppost.getRequestLine().toString();
        } catch (Exception ex) {
            Error = ex.getMessage();
            Log.e("ERROR",Error);
        }


        return rsp;
    }

    protected void onProgressUpdate(Integer... progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
        mNotificationHelper.progressUpdate(progress[0]);
    }

    protected void onPostExecute(String result) {
        mNotificationHelper.completed();
    }
}