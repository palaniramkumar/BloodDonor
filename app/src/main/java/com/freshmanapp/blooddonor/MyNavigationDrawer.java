package com.freshmanapp.blooddonor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

import com.freshmanapp.blooddonor.model.Popup;
import com.freshmanapp.blooddonor.util.AccountUtils;


public class MyNavigationDrawer extends MaterialNavigationDrawer {
    MaterialAccount account;
    MaterialSection target;
    @Override
    public void init(Bundle savedInstanceState) {
        AccountUtils.UserProfile profile  =  AccountUtils.getUserProfile(MyNavigationDrawer.this);
        Bitmap bitmap = null;

        /* getting user image */
        try {

            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profile.possiblePhoto());
        }
        catch (Exception e){}

        account = new MaterialAccount(this.getResources(),profile.possibleNames().get(0) ,profile.possibleEmails().get(0),bitmap, R.drawable.bamboo);
        this.addAccount(account);

        // create sections
        target = newSection("Summary", R.drawable.ic_home,new Summary()).setSectionColor(Color.RED);
        this.addSection(target);
        this.addSection(newSection("Friends", R.drawable.ic_friends, new FriendsList()).setSectionColor(Color.RED));
       // this.addSection(newSection("Preferences",R.drawable.ic_hotel_grey600_24dp,new TestFragment()).setSectionColor(Color.parseColor("#0689e4")));

        enableToolbarElevation();

        thread.start();
    }

    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    removeAccount(account);
//                    notifyAccountDataChanged();
//                    removeSection(target);
                    setSection(target);
                }
            });
        }
    });


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void popup(View v){
        Popup p = new Popup(v);
        p.show();
    }
}
