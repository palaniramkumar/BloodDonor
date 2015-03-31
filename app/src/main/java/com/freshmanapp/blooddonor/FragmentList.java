package com.freshmanapp.blooddonor;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Ramkumar on 01/04/15.
 */
public class FragmentList extends ListFragment{

    ArrayList<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        list = new ArrayList<>();

        for (int i = 0;i< 50 ;i++) {
            list.add("Item "+ (i+1));
        }
        setListAdapter(new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, list));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
