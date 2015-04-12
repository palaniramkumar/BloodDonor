package com.freshmanapp.blooddonor.adapter;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.freshmanapp.blooddonor.controller.AppController;
import com.freshmanapp.blooddonor.model.Donor;
import com.freshmanapp.blooddonor.R;
/**
 * Created by Ramkumar on 06/04/15.
 */
public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Donor> donorList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Donor> donorList) {
        this.activity = activity;
        this.donorList = donorList;
    }

    @Override
    public int getCount() {
        return donorList.size();
    }

    @Override
    public Object getItem(int location) {
        return donorList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.img_usr_pic);
        TextView header = (TextView) convertView.findViewById(R.id.txt_header);
        TextView subline1 = (TextView) convertView.findViewById(R.id.txt_subline1);
        TextView subline2 = (TextView) convertView.findViewById(R.id.txt_subline2);
        TextView caption = (TextView) convertView.findViewById(R.id.txt_caption);

        // getting movie data for the row
        Donor m = donorList.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        header.setText(m.getHeader());

        subline1.setText(m.getSubline1());

        subline2.setText(m.getSubline2());

        // release year
        caption.setText(String.valueOf(m.getCaption()));

        return convertView;
    }

}
