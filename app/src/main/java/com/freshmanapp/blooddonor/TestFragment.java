package com.freshmanapp.blooddonor;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by Ramkumar on 01/04/15.
 */
public class TestFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_fragment_click,container,false);

        ((Button)view.findViewById(R.id.next_section)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSection();
            }
        });

        return view;
    }


    public void nextSection() {
        Fragment fragment = new TestFragment2();
        Bundle data = new Bundle();
        data.putString("Test","Banana");
        fragment.setArguments(data);

        ((MaterialNavigationDrawer)this.getActivity()).setFragmentChild(fragment,"Test page 2");
    }
}
