package com.freshmanapp.blooddonor;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Ramkumar on 01/04/15.
 */
public class TestFragment2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_fragment_click,container,false);

        Bundle data = this.getArguments();

        ((Button)view.findViewById(R.id.next_section)).setText(data.getString("Test"));
        ((Button)view.findViewById(R.id.next_section)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }



}
