package com.freshmanapp.blooddonor;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Button;


/**
 * Created by Ramkumar on 10/04/15.
 */
public class Summary  extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);
        Button btn_buzzAll = (Button)view.findViewById(R.id.btn_buzz);
        Button btn_search = (Button)view.findViewById(R.id.btn_search);

        btn_buzzAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buzz();
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        return view;
    }

    void search(){
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                Toast.makeText(fragment.getDialog().getContext(), "You have selected " + getSelectedValue() + " group.", Toast.LENGTH_SHORT).show();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                Toast.makeText(fragment.getDialog().getContext(), "Cancelled" , Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        };

        ((SimpleDialog.Builder)builder).items(new String[]{"A+", "A-", "B-", "B+", "AB+", "AB-", "B+", "B-"}, 0)
                .title("Choose Blood Group")
                .positiveAction("OK")
                .negativeAction("CANCEL");

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }
    void buzz(){
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){ //R.style.Material_TextAppearance_SimpleDialog_Light

            @Override
            protected Dialog onBuild(Context context, int styleId) {
                Dialog dialog = super.onBuild(context, styleId);
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                return dialog;
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                Toast.makeText(fragment.getDialog().getContext(), "Connected", Toast.LENGTH_SHORT).show();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                Toast.makeText(fragment.getDialog().getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.title("Broadcast Message")
                .positiveAction("SEND")
                .negativeAction("CANCEL")
                .contentView(R.layout.dialog_buzz);
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);

    }
}
