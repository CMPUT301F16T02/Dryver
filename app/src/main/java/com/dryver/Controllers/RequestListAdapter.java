package com.dryver.Controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dryver.Models.Request;
import com.dryver.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A custom Array Adapter for listing requests as strings properly.
 * @see Request
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
        sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Mountain"));

        TextView requestName = (TextView) convertView.findViewById(R.id.requestItemName);
        TextView requestDestination = (TextView) convertView.findViewById(R.id.requestItemDestination);
        TextView requestStatus = (TextView) convertView.findViewById(R.id.requestItemStatus);
        TextView requestDate = (TextView) convertView.findViewById(R.id.requestItemDate);

        requestName.setText("Ride Request");
        requestDestination.setText("Destination: " +request.getToLocation());
        requestStatus.setText("Status: " + request.statusCodeToString());
        requestDate.setText("Date: "+ sdf.format(request.getDate().getTime()));


        return convertView;
    }
}
