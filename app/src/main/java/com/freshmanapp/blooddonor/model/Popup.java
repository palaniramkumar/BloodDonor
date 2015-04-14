package com.freshmanapp.blooddonor.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

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
                            String s2 = "8056026731";
                            Intent intent2 = new Intent("android.intent.action.DIAL", Uri.parse((new StringBuilder()).append("tel:").append(s2).toString()));
                            mContext.startActivity(intent2);
                            return;
                        }
                        else if (text.toString().equalsIgnoreCase("Send a SMS"))
                        {
                            String s1 = "8056026731";
                            String blood = "A1+";
                            Intent intent1 = new Intent("android.intent.action.VIEW");
                            intent1.putExtra("address", s1);
                            intent1.putExtra("sms_body", (new StringBuilder()).append("Urgent Help! Blood Required for").append(blood).append(" .").toString());
                            intent1.setType("vnd.android-dir/mms-sms");
                            mContext.startActivity(intent1);
                            return;
                        }
                        else if (text.toString().equalsIgnoreCase("Locate"))
                        {
                            String geotag ="1,1";
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
