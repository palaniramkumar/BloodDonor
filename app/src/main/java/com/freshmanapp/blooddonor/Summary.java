package com.freshmanapp.blooddonor;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.freshmanapp.blooddonor.adapter.CustomListAdapter;
import com.freshmanapp.blooddonor.controller.AppController;
import com.freshmanapp.blooddonor.model.Donor;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ramkumar on 10/04/15.
 */
public class Summary  extends Fragment {
    private static final String url = "http://api.androidhive.info/json/movies.json";
    private ProgressDialog pDialog;
    private List<Donor> donorList = new ArrayList<Donor>();
    private ListView listView;
    private CustomListAdapter adapter;
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

        //loading notification message
        listView = (ListView) view.findViewById(R.id.list);
        adapter = new CustomListAdapter(getActivity(), donorList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(getClass().getName(), response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Donor donor = new Donor();
                                donor.setName(obj.getString("title"));
                                donor.setThumbnailUrl(obj.getString("image"));
                                donor.setCaption(obj.getString("rating"));
                                donor.setSubline1(obj.getString("releaseYear"));

                                // adding donor to movies array
                                donorList.add(donor);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(getClass().getName(), "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);




        return view;
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
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
