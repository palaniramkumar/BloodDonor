package com.freshmanapp.blooddonor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
 * Created by Ramkumar on 01/04/15.
 */
public class SearchDonor extends android.support.v4.app.Fragment {
    private List<Donor> donorList = new ArrayList<Donor>();
    private ListView listView;
    private CustomListAdapter adapter;
    private ProgressDialog pDialog;
    double lat,lon;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donor_search,container,false);

        Bundle data = this.getArguments();

        final String blood = data.getString("blood");

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


       /* ((Button)view.findViewById(R.id.next_section)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });*/

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

                                Log.d("distance:", element.getElementsByTagName("distance").item(0).getTextContent());
                                Log.d("name:", element.getElementsByTagName("name").item(0).getTextContent());
                                Log.d("userid:", element.getElementsByTagName("userid").item(0).getTextContent());
                                Log.d("blood:", element.getElementsByTagName("blood").item(0).getTextContent());
                                Log.d("distance:", element.getElementsByTagName("distance").item(0).getTextContent());

                                donor.setName(element.getElementsByTagName("name").item(0).getTextContent());
                                donor.setThumbnailUrl(url + "?uid=" + element.getElementsByTagName("userid").item(0).getTextContent() + "&action=GET_PROFILE_PIC");
                                donor.setCaption(element.getElementsByTagName("distance").item(0).getTextContent());
                                donor.setSubline2( element.getElementsByTagName("blood").item(0).getTextContent());
                                String user_lat = element.getElementsByTagName("lat").item(0).getTextContent();
                                String user_lon = element.getElementsByTagName("lon").item(0).getTextContent();
                                donor.setGeotag(user_lat + "," + user_lon);
                                donor.setMobile(element.getElementsByTagName("mobile").item(0).getTextContent());
                                Log.d("Debug xml", user_lat + "," + user_lon);

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


                params.put("action", "GET_USER");
                params.put("uid", id);
                params.put("blood", blood);
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

}
