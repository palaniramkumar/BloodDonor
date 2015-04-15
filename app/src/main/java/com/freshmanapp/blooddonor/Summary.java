package com.freshmanapp.blooddonor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.freshmanapp.blooddonor.adapter.CustomListAdapter;
import com.freshmanapp.blooddonor.controller.AppController;
import com.freshmanapp.blooddonor.model.Donor;
import com.freshmanapp.blooddonor.service.GPSTracker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


/**
 * Created by Ramkumar on 10/04/15.
 */
public class Summary extends Fragment {

    private ProgressDialog pDialog;
    private List<Donor> donorList = new ArrayList<Donor>();
    private ListView listView;
    private CustomListAdapter adapter;
    double lat, lon;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Button btn_buzzAll = (Button) view.findViewById(R.id.btn_buzz);
        Button btn_search = (Button) view.findViewById(R.id.btn_search);

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

        GPSTracker gpsTracker = new GPSTracker(getActivity());
        lat = gpsTracker.getLatitude();
        lon = gpsTracker.getLongitude();

        final String url = getResources().getString(R.string.host);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            Document document;
                            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(response.getBytes()));
                            document.getDocumentElement().normalize();

                            Log.d("root:", document.getDocumentElement().getNodeName());
                            NodeList nodelist;
                            nodelist = document.getElementsByTagName("person");
                            Log.d("person length:", (new StringBuilder()).append(nodelist.getLength()).append("").toString());

                            for (int temp = 0; temp < nodelist.getLength(); temp++) {

                                Node node = nodelist.item(temp);
                                Element element = (Element) node;
                                Donor donor = new Donor();
                                donor.setName(element.getElementsByTagName("message").item(0).getTextContent());
                                donor.setThumbnailUrl(url + "?uid=" + element.getElementsByTagName("userid").item(0).getTextContent() + "&action=GET_PROFILE_PIC");
                                donor.setCaption(element.getElementsByTagName("ts").item(0).getTextContent());
                                donor.setSubline1(element.getElementsByTagName("name").item(0).getTextContent());
                                //donor.setSubline2(element.getElementsByTagName("distance").item(0).getTextContent());
                                donor.setSubline2("Blood Group " + element.getElementsByTagName("blood").item(0).getTextContent());
                                String user_lat = element.getElementsByTagName("lat").item(0).getTextContent();
                                String user_lon = element.getElementsByTagName("lon").item(0).getTextContent();
                                donor.setGeotag(user_lat + "," + user_lon);
                                donor.setMobile(element.getElementsByTagName("mobile").item(0).getTextContent());
                                Log.d("Debug xml", user_lat + "," + user_lon);

                                // adding donor to donor array
                                donorList.add(donor);
                                adapter.notifyDataSetChanged();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("Response", response);
                        hidePDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        // error
                        new AlertDialogWrapper.Builder(getActivity())
                                .setTitle("Sorry!")
                                .setMessage("We are unable to connect with the server")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        error.printStackTrace();
                                        dialog.dismiss();
                                        getActivity().finish();
                                    }
                                }).show();

                        hidePDialog();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences preferences = getActivity().getSharedPreferences("REGISTER_ID", Context.MODE_PRIVATE);
                String id = preferences.getString("rid", "");


                params.put("action", "GET_MSG");
                params.put("uid", id);
                params.put("lat", Double.toString(lat));
                params.put("lon", Double.toString(lon));

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(postRequest);


        return view;
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    void search() {
        new MaterialDialog.Builder(getActivity())
                .title("Blood Group")
                .items(R.array.blood_array)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        Fragment donor = new SearchDonor();
                        Bundle data = new Bundle();
                        data.putString("blood",text.toString());
                        donor.setArguments(data);
                        ((MaterialNavigationDrawer)getActivity()).setFragmentChild(donor,"Resluts for "+text);
                        return true;
                    }
                })
                .positiveText("Choose")
                .show();
    }

    void buzz() {
        new MaterialDialog.Builder(getActivity())
                .title("Buzz Message")
                .content("Comment")
                .cancelable(true)
                .input(R.string.input_hint, 0, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        Toast.makeText(getActivity(), "Sending....", Toast.LENGTH_SHORT).show();
                        final String url = getResources().getString(R.string.host);

                        final String buzzMsg = input.toString();
                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // response
                                        try {
                                            Document document;
                                            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(response.getBytes()));
                                            document.getDocumentElement().normalize();

                                            Log.d("root:", document.getDocumentElement().getNodeName());
                                            NodeList nodelist;
                                            nodelist = document.getElementsByTagName("person");
                                            Log.d("person length:", (new StringBuilder()).append(nodelist.getLength()).append("").toString());

                                            for (int temp = 0; temp < nodelist.getLength(); temp++) {

                                                Node node = nodelist.item(temp);
                                                Element element = (Element) node;
                                                Donor donor = new Donor();
                                                donor.setName(element.getElementsByTagName("message").item(0).getTextContent());
                                                donor.setThumbnailUrl(url + "?uid=" + element.getElementsByTagName("userid").item(0).getTextContent() + "&action=GET_PROFILE_PIC");
                                                donor.setCaption(element.getElementsByTagName("ts").item(0).getTextContent());
                                                //donor.setSubline1(element.getElementsByTagName("distance").item(0).getTextContent());
                                                donor.setSubline2(element.getElementsByTagName("name").item(0).getTextContent());
                                                donor.setSubline2("Blood Group" + element.getElementsByTagName("blood").item(0).getTextContent());
                                                String user_lat = element.getElementsByTagName("lat").item(0).getTextContent();
                                                String user_lon = element.getElementsByTagName("lon").item(0).getTextContent();
                                                donor.setGeotag(user_lat+","+user_lon);
                                                donor.setMobile(element.getElementsByTagName("mobile").item(0).getTextContent());
                                                Log.d("Debug xml",user_lat+","+user_lon);
                                                // adding donor to donor array
                                                donorList.add(donor);
                                                adapter.notifyDataSetChanged();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Log.d("Response", response);
                                        hidePDialog();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // error
                                        Log.d("Error.Response", error.toString());
                                        Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
                                        //hidePDialog();
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                SharedPreferences preferences = getActivity().getSharedPreferences("REGISTER_ID", Context.MODE_PRIVATE);
                                String id = preferences.getString("rid", "");


                                params.put("action", "BROADCAST");
                                params.put("uid", id);
                                params.put("blood", buzzMsg);
                                params.put("lat", Double.toString(lat));
                                params.put("lon", Double.toString(lon));

                                return params;
                            }
                        };
                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(postRequest);


                    }
                }).show();


    }
}
