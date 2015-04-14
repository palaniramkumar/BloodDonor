package com.freshmanapp.blooddonor.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.freshmanapp.blooddonor.R;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Locale;

/**
 * Created by Ramkumar on 15/04/15.
 */
public class Popup {
    Context mContext;
    View v;
    public Popup(View v){
        this.mContext = v.getContext();
        this.v = v;
    }
    public void show(){
        String[] myStringArray = {"Make a Call","Send a SMS","Locate"};
        MaterialDialog d = new MaterialDialog.Builder(mContext)
        .items(myStringArray)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (text.toString().equalsIgnoreCase("Make a Call"))
                        {
                            String mobile = ((TextView)v.findViewById(R.id.txt_mobile)).getText().toString();
                            Intent intent2 = new Intent("android.intent.action.DIAL", Uri.parse((new StringBuilder()).append("tel:").append(mobile).toString()));
                            mContext.startActivity(intent2);
                            return;
                        }
                        else if (text.toString().equalsIgnoreCase("Send a SMS"))
                        {
                            String mobile = ((TextView)v.findViewById(R.id.txt_mobile)).getText().toString();
                            String blood = ((TextView)v.findViewById(R.id.txt_subline2)).getText().toString();
                            Intent intent1 = new Intent("android.intent.action.VIEW");
                            intent1.putExtra("address", mobile);
                            intent1.putExtra("sms_body", (new StringBuilder()).append("Urgent Help! Required for ").append(blood).append(" .").toString());
                            intent1.setType("vnd.android-dir/mms-sms");
                            mContext.startActivity(intent1);
                            return;
                        }
                        else if (text.toString().equalsIgnoreCase("Locate"))
                        {
                            String geotag = ((TextView)v.findViewById(R.id.txt_geotag)).getText().toString();
                            if(geotag.trim().equals("")) return;
                            double d = Double.parseDouble(geotag.split(",")[0]);
                            double d1 = Double.parseDouble(geotag.split(",")[1]);
                            Locale locale = Locale.ENGLISH;
                            Object aobj[] = new Object[2];
                            aobj[0] = Double.valueOf(d);
                            aobj[1] = Double.valueOf(d1);
                            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(String.format(locale, "geo:%f,%f", aobj)));
                            mContext.startActivity(intent);
                            return;
                        }
                    }
                })
                .show();
    }

}
