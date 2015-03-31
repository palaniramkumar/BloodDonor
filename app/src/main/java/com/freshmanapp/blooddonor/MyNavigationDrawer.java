package com.freshmanapp.blooddonor;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;


public class MyNavigationDrawer extends MaterialNavigationDrawer {
    MaterialAccount account;
    MaterialSection target;
    @Override
    public void init(Bundle savedInstanceState) {
        account = new MaterialAccount(this.getResources(),"NeoKree","neokree@gmail.com",R.drawable.photo, R.drawable.bamboo);
        this.addAccount(account);

        MaterialAccount account2 = new MaterialAccount(this.getResources(),"Hatsune Miky","hatsune.miku@example.com",R.drawable.photo2,R.drawable.mat2);
        this.addAccount(account2);

        MaterialAccount account3 = new MaterialAccount(this.getResources(),"Example","example@example.com",R.drawable.photo,R.drawable.mat3);
        this.addAccount(account3);

        // create sections
        this.addSection(newSection("Section 1", new TestFragment()));
        this.addSection(newSection("Section 2",new FragmentList()));
        target = newSection("Section 3", R.drawable.ic_mic_white_24dp,new TestFragment()).setSectionColor(Color.parseColor("#9c27b0"));
        this.addSection(target);
        this.addSection(newSection("Section",R.drawable.ic_hotel_grey600_24dp,new TestFragment()).setSectionColor(Color.parseColor("#03a9f4")));

        enableToolbarElevation();

        thread.start();
    }

    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(4000);
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

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_navigation_drawer);
    }*/

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
}
