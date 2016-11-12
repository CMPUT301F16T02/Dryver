package com.ubertapp.Controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ubertapp.Models.Request;
import com.ubertapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Jiawei on 11/10/2016.
 */
public class RequestListAdapter extends ArrayAdapter<Request> {

    private SimpleDateFormat sdf;
    private Context mContext;

    public RequestListAdapter(Context context, ArrayList<Request> requestArrayList) {
        super(context, 0, requestArrayList);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Request request = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request_item, null);
        }


        return convertView;
    }
}
