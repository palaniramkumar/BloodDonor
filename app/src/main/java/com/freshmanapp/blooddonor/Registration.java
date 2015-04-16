package com.freshmanapp.blooddonor;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freshmanapp.blooddonor.model.User;
import com.freshmanapp.blooddonor.service.GPSTracker;
import com.freshmanapp.blooddonor.service.RegisterAccountTask;
import com.freshmanapp.blooddonor.util.AccountUtils;
import com.freshmanapp.blooddonor.util.BitMapOperations;
import com.freshmanapp.blooddonor.util.GCM;
import com.rengwuxian.materialedittext.MaterialEditText;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ramkumar on 07/04/15.
 */
public class Registration extends FragmentActivity {
    MaterialEditText txt_dob;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Log.d(this.getLocalClassName(), "The onCreate() event");



        /* componment init*/

        AccountUtils.UserProfile profile  =  AccountUtils.getUserProfile(this);
        TextView txt_name = ((TextView)findViewById(R.id.txt_header));
        TextView txt_email = ((TextView)findViewById(R.id.txt_emailid));
        ImageView profile_pic = ((ImageView)findViewById(R.id.profile_pic));
        txt_dob = ((MaterialEditText) findViewById(R.id.txt_dob));

        /* component setter */
        txt_email.setText(profile.possibleEmails().get(0));
        txt_name.setText(profile.possibleNames().get(0));
        Bitmap bitmap=null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profile.possiblePhoto());
            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bitmap!=null)
            profile_pic.setImageBitmap(BitMapOperations.getRoundedRectBitmap(bitmap,50));
        final String base64Bitmap = BitMapOperations.getBase64Image(bitmap);

        final MaterialEditText spn_label = (MaterialEditText) findViewById(R.id.spinner_blood_group);

        MaterialEditText txt_address = (MaterialEditText) findViewById(R.id.txt_address);
        Button btn_register = (Button) findViewById(R.id.btn_register);


        final GPSTracker gpsTracker = new GPSTracker(this);
        String city = gpsTracker.getAddressLine(this);
        txt_address.setText(city);

        txt_dob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDatePickerDialog(Calendar.getInstance());
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /* get component values */
                String name =((TextView)findViewById(R.id.txt_header)).getText().toString();
                String location = ((MaterialEditText)findViewById(R.id.txt_address)).getText().toString();
                String dob = ((MaterialEditText)findViewById(R.id.txt_dob)).getText().toString();
                String weight = ((MaterialEditText)findViewById(R.id.txt_weight)).getText().toString();
                String mobile = ((MaterialEditText)findViewById(R.id.txt_phno)).getText().toString();
                String email = ((TextView)findViewById(R.id.txt_emailid)).getText().toString();
                String blood_type = ((MaterialEditText)findViewById(R.id.spinner_blood_group)).getText().toString();
                String lat = gpsTracker.getLatitude()+"";
                String lon =  gpsTracker.getLongitude()+"";
                GCM gcm = new GCM();
                String token = gcm.getRegistrationId(getApplicationContext());

                // Use AsyncTask execute Method To Prevent ANR Problem
                User user = new User(name,lat,lon,location,dob,weight,mobile,email,blood_type,base64Bitmap,token);

                MaterialDialog diag =  new MaterialDialog.Builder(Registration.this)
                        .title("Signup")
                        .content("Creating Account ...")
                        .progress(true, 0)
                        .show();

               new RegisterAccountTask(Registration.this,user).execute(getResources().getString(R.string.host));

            }
        });
        spn_label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showBloodgroup(spn_label);
            }
        });

    }
    private void showDatePickerDialog(final Calendar c){
        final Date date=c.getTime();
        DatePickerDialog datePickerDialog=new DatePickerDialog(Registration.this,new DatePickerDialog.OnDateSetListener(){
            public void onDateSet(    DatePicker view,    int year,    int monthOfYear,    int dayOfMonth){
                Calendar cNew=Calendar.getInstance();
                cNew.set(year,monthOfYear,dayOfMonth);
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                txt_dob.setText(format.format(cNew.getTime()));
            }
        }
                ,1900 + date.getYear(),date.getMonth(), date.getDate());
        datePickerDialog.show();
    }
    private void showBloodgroup(final MaterialEditText spn_label) {
        new MaterialDialog.Builder(this)
                .title("Blood Group")
                .items(R.array.blood_array)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        spn_label.setText(text);
                    }
                })
                .positiveText(android.R.string.cancel)
                .show();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }







}
