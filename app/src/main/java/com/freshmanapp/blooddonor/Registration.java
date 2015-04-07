package com.freshmanapp.blooddonor;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.freshmanapp.blooddonor.service.GPSTracker;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.widget.Spinner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramkumar on 07/04/15.
 */
public class Registration extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        Log.d(this.getLocalClassName(), "The onCreate() event");

        final Dialog.Builder builder = new DatePickerDialog.Builder() {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                String date = dialog.getFormattedDate(SimpleDateFormat.getDateInstance());
                ((com.rey.material.widget.EditText)findViewById(R.id.txt_dob)).setText(date);
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                Toast.makeText(fragment.getDialog().getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        }.positiveAction("OK")
                .negativeAction("CANCEL");





        Spinner spn_label = (Spinner) findViewById(R.id.spinner_blood_group);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_spn_dropdown, new String[]{"A1+", "A1-", "B+","B-", "O+", "O-","AB+", "AB-", "B+","A1+", "A1-", "B+"});
        //adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
        spn_label.setAdapter(adapter);


        com.rey.material.widget.EditText txt_dob = (com.rey.material.widget.EditText) findViewById(R.id.txt_dob);
        com.rey.material.widget.EditText txt_address = (com.rey.material.widget.EditText) findViewById(R.id.txt_address);


        GPSTracker gpsTracker = new GPSTracker(this);
        String city = gpsTracker.getAddressLine(this);
        txt_address.setText(city);

        txt_dob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), null);
            }
        });

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    //newly added from the existing code, we shall ignore this for now


    private StringBuilder readContacts()
    {
        ContentResolver cr =getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        String displayName="", emailAddress="", phoneNumber="";
        StringBuilder str=new StringBuilder();
        /*Cursor cursor1 =  managedQuery(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        int count = cursor1.getCount();
        cursor1.close();*/
        int i=0;
        while (cursor.moveToNext())
        {
            i++;

            displayName="";emailAddress=""; phoneNumber="";
            displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor emails = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);


            while (emails.moveToNext())
            {
                emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                break;
            }
            emails.close();
            if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
            {
                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{id}, null);
                while (pCur.moveToNext())
                {
                    phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    break;
                }
                pCur.close();
            }
            Log.d("Contacts","DisplayName: " + displayName + ", PhoneNumber: " +phoneNumber.replaceAll("[^\\p{L}\\p{Nd}]", "") + ", EmailAddress: " + emailAddress);
            phoneNumber=phoneNumber.replaceAll("[^\\p{L}\\p{Nd}]", "");
            if(!phoneNumber.trim().equalsIgnoreCase("")){

                str.append(phoneNumber);
                str.append(",");
            }
        }
        cursor.close();
        return str;
    }


    private class RegisterAccountTask extends AsyncTask<String, Void, String> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */

        String data ="";
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(Registration.this);
        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //Start Progress Dialog (Message)

            Dialog.setMessage("Registering..");
            Dialog.show();
            //url pattern:name=Ram&lat=50&lon=8blood_type=0-&email=palaniramkumar@live.com&mobile=8056026731&location=Virudhunagar&dob=18/04/1986&weight=73&contactinfo=
            try{
                // Set Request parameter
                data +="&" + URLEncoder.encode("data", "UTF-8") + "=music";

                //commenting temp
                //data+="&name="+txt_fname.getText()+"&lat="+lat+"&lon="+lon+"&blood_type="+ URLEncoder.encode(spinner.getSelectedItem().toString(),"UTF-8")+"&email="+txt_email.getText()+"&mobile="+txt_mobile.getText()+"&location="+txt_address.getText()+"&dob="+txt_dob.getText()+"&weight="+txt_weight.getText()+"&contactinfo=";

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
        private StringBuilder inputStreamToString(InputStream is) throws IOException{
            String line = "";
            StringBuilder total = new StringBuilder();
            // Wrap a BufferedReader around the InputStream
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            // Read response until the end
            try {
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Return full string
            return total;
        }
        protected String doInBackground(String... urls) {

            /*****Get Contacts *****/
            StringBuilder contactStr=readContacts();

            /************ Make Post Call To Web Server ***********/
            BufferedReader reader=null;
            String str="";
            // Send data
            try
            {



                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urls[0]);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                //commenting temp
                /*
                nameValuePairs.add(new BasicNameValuePair("action", "NEW_ACCOUNT"));
                nameValuePairs.add(new BasicNameValuePair("name", txt_fname.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("lat", Double.toString(lat)));
                nameValuePairs.add(new BasicNameValuePair("lon", Double.toString(lon)));
                nameValuePairs.add(new BasicNameValuePair("location", txt_address.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("dob", txt_dob.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("weight", txt_weight.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("mobile", txt_mobile.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("email", txt_email.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("blood_type",spinner.getSelectedItem().toString() ));
                nameValuePairs.add(new BasicNameValuePair("contacts",contactStr.toString()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
*/
                HttpResponse response = httpclient.execute(httppost);

                str = inputStreamToString(response.getEntity().getContent()).toString();
                str=httppost.getRequestLine().toString();

            }
            catch(Exception ex)
            {
                Error = ex.getMessage();
            }
            finally
            {
                try
                {

                    reader.close();
                }

                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }

            /*****************************************************/

            return str;
        }
        protected void onPostExecute(String result) {
            // Perform action on click
            SharedPreferences preferences =  getSharedPreferences("REGISTER_ID",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("rid",result);
            editor.commit();
            Dialog.dismiss();
            Toast.makeText(Registration.this, "Account Synced for id "+result, Toast.LENGTH_LONG).show();
            Intent i = new Intent(Registration.this, MyNavigationDrawer.class);//MainActivity
            startActivity(i);
            finish();
        }
    }
}
