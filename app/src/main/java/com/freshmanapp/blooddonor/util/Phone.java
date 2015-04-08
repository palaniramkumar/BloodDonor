package com.freshmanapp.blooddonor.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.freshmanapp.blooddonor.R;

/**
 * Created by Ramkumar on 09/04/15.
 */
public class Phone{
    String countryCode="";
    String mobileNo="";
    public Phone(Context context,String number){
        if(number.length() >=10) {
            mobileNo = number.substring(number.length() - 10);
            countryCode = number.substring(0,number.length() - 10);
            if(countryCode.trim().equals("0") || countryCode.trim().equals("")) {
                TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                countryCode =manager.getNetworkCountryIso().toUpperCase();
                String[] rl=context.getResources().getStringArray(R.array.CountryCodes);
                for(int i=0;i<rl.length;i++){
                    String[] g=rl[i].split(",");
                    if(g[1].trim().equals(countryCode.trim())){
                        countryCode=g[0];
                        break;
                    }
                }
            }
        }
    }
    public String getCountryCode(){
        return countryCode;
    }
    public String getMobileNo(){
        return mobileNo;
    }
}