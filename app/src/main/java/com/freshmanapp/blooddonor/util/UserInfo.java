package com.freshmanapp.blooddonor.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by Ramkumar on 06/04/15.
 */
public class UserInfo {
    Context mContext;
    public UserInfo(Context context){
        mContext = context;
    }
    public void details(){
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(mContext).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
            }
        }
    }
}
