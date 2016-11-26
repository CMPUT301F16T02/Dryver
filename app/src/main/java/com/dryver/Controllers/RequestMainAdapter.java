package com.dryver.Controllers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dryver.Activities.ActivityRequestSelection;
import com.dryver.Models.Request;
import com.dryver.R;
import com.dryver.Utility.HelpMe;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A custom Array Adapter for listing requests as strings properly.
 * @see Request
 */
public class RequestMainAdapter extends ArrayAdapter<Request> {
    private Context mContext;
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    public RequestMainAdapter(Context context, ArrayList<Request> requestArrayList) {
        super(context, 0, requestArrayList);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        DecimalFormat formater = new DecimalFormat("0.00");

        final Request request = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request_main_item, null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.viewRequest(mContext, request);
            }

        });

        TextView requestName = (TextView) convertView.findViewById(R.id.requestItemName);
        TextView requestPickup = (TextView) convertView.findViewById(R.id.requestItemPickup);
        TextView requestDestination = (TextView) convertView.findViewById(R.id.requestItemDestination);
        TextView requestStatus = (TextView) convertView.findViewById(R.id.requestItemStatus);
        TextView requestDate = (TextView) convertView.findViewById(R.id.requestItemDate);
        TextView requestCost = (TextView) convertView.findViewById(R.id.requestItemCost);
        TextView requestRate = (TextView) convertView.findViewById(R.id.requestItemRate);

        requestName.setText("Ride Request");
        requestDestination.setText("Destination: " + formater.format(request.getToLocation().getLatitude()) + ", " + formater.format(request.getToLocation().getLongitude()));
        requestPickup.setText("Pickup At: " + formater.format(request.getToLocation().getLatitude()) + ", " + formater.format(request.getToLocation().getLongitude()));
        requestStatus.setText("Status: " + request.statusCodeToString());
        requestDate.setText("Date: "+ HelpMe.getDateString(request.getDate()));
        requestCost.setText("Total Cost: $" + request.getCost());
        requestRate.setText("Rate: $" + request.getRate() + "/km");


        return convertView;
    }
}
