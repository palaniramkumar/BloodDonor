package com.freshmanapp.blooddonor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.freshmanapp.blooddonor.model.User;
import com.freshmanapp.blooddonor.service.GPSTracker;
import com.freshmanapp.blooddonor.service.LinkAccountTask;
import com.freshmanapp.blooddonor.service.RegisterAccountTask;
import com.freshmanapp.blooddonor.util.AccountUtils;
import com.freshmanapp.blooddonor.util.BitMapOperations;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.widget.Spinner;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Ramkumar on 07/04/15.
 */
public class Registration extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
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


        /* componment init*/

        AccountUtils.UserProfile profile  =  AccountUtils.getUserProfile(this);
        TextView txt_name = ((TextView)findViewById(R.id.txt_name));
        TextView txt_email = ((TextView)findViewById(R.id.txt_emailid));
        ImageView profile_pic = ((ImageView)findViewById(R.id.profile_pic));

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

        Spinner spn_label = (Spinner) findViewById(R.id.spinner_blood_group);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_spn_dropdown, new String[]{"A1+", "A1-", "B+","B-", "O+", "O-","AB+", "AB-", "B+","A1+", "A1-", "B+"});
        //adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
        spn_label.setAdapter(adapter);


        com.rey.material.widget.EditText txt_dob = (com.rey.material.widget.EditText) findViewById(R.id.txt_dob);
        com.rey.material.widget.EditText txt_address = (com.rey.material.widget.EditText) findViewById(R.id.txt_address);
        com.rey.material.widget.Button btn_register = (com.rey.material.widget.Button) findViewById(R.id.btn_register);


        final GPSTracker gpsTracker = new GPSTracker(this);
        String city = gpsTracker.getAddressLine(this);
        txt_address.setText(city);

        txt_dob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), null);
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // new LinkAccountTask(getApplicationContext()).execute(1);

                /* get component values */
                String name =((TextView)findViewById(R.id.txt_name)).getText().toString();
                String location = ((com.rey.material.widget.EditText)findViewById(R.id.txt_address)).getText().toString();
                String dob = ((com.rey.material.widget.EditText)findViewById(R.id.txt_dob)).getText().toString();
                String weight = ((com.rey.material.widget.EditText)findViewById(R.id.txt_weight)).getText().toString();
                String mobile = ((com.rey.material.widget.EditText)findViewById(R.id.txt_phno)).getText().toString();
                String email = ((TextView)findViewById(R.id.txt_emailid)).getText().toString();
                com.rey.material.widget.Spinner group = (com.rey.material.widget.Spinner)findViewById(R.id.spinner_blood_group);
                String blood_type = group.getSelectedItemPosition()+"";
                String lat = gpsTracker.getLatitude()+"";
                String lon =  gpsTracker.getLongitude()+"";

                // Use AsyncTask execute Method To Prevent ANR Problem
                User user = new User(name,lat,lon,location,dob,weight,mobile,email,blood_type,base64Bitmap);

               new RegisterAccountTask(Registration.this,user).execute(getResources().getString(R.string.host));

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







}
