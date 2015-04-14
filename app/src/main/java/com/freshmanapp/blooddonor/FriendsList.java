package com.freshmanapp.blooddonor;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

/**
 * Created by Ramkumar on 07/04/15.
 * http://www.androidhive.info/2014/07/android-custom-listview-with-image-and-text-using-volley/
 */
public class FriendsList  extends Fragment {
    // Log tag
    private static final String TAG = MyNavigationDrawer.class.getSimpleName();

    // Movies json url
    private static final String url = "http://api.androidhive.info/json/movies.json";
    private ProgressDialog pDialog;
    private List<Donor> donorList = new ArrayList<Donor>();
    private ListView listView;
    private CustomListAdapter adapter;
    double lat,lon;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friendslist,container,false);

        listView = (ListView) view.findViewById(R.id.list);
        adapter = new CustomListAdapter(getActivity(), donorList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        GPSTracker gpsTracker = new GPSTracker(getActivity());
        lat = gpsTracker.getLatitude();
        lon=gpsTracker.getLongitude();

        // Creating volley request obj

        final String url = getResources().getString(R.string.host);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
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
                                donor.setName(element.getElementsByTagName("name").item(0).getTextContent());
                                donor.setThumbnailUrl(url + "?uid=" + element.getElementsByTagName("userid").item(0).getTextContent() + "&action=GET_PROFILE_PIC");
                                donor.setCaption(element.getElementsByTagName("ts").item(0).getTextContent());
                                donor.setSubline1(element.getElementsByTagName("blood").item(0).getTextContent());
                                donor.setSubline2(element.getElementsByTagName("distance").item(0).getTextContent());
                                // adding donor to donor array
                                donorList.add(donor);
                                adapter.notifyDataSetChanged();
                            }

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.d("Response", response);
                        hidePDialog();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        hidePDialog();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                SharedPreferences preferences = getActivity().getSharedPreferences("REGISTER_ID", Context.MODE_PRIVATE);
                String id = preferences.getString("rid", "");


                params.put("action", "MYFRIENDS");
                params.put("uid", id);
                params.put("lat",Double.toString(lat));
                params.put("lon", Double.toString(lon));

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(postRequest);

        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        view.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

}
