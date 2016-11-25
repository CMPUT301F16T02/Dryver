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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A custom Array Adapter for listing requests as strings properly.
 * @see Request
 */
public class RequestListAdapter extends ArrayAdapter<Request> {
    private Context mContext;
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    public RequestListAdapter(Context context, ArrayList<Request> requestArrayList) {
        super(context, 0, requestArrayList);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Request request = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request_item, null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.setMakeRequest(request);
                Intent intent = new Intent(mContext, ActivityRequestSelection.class);
                mContext.startActivity(intent);
            }

        });

        TextView requestName = (TextView) convertView.findViewById(R.id.requestItemName);
        TextView requestDestination = (TextView) convertView.findViewById(R.id.requestItemDestination);
        TextView requestStatus = (TextView) convertView.findViewById(R.id.requestItemStatus);
        TextView requestDate = (TextView) convertView.findViewById(R.id.requestItemDate);
        TextView requestCost = (TextView) convertView.findViewById(R.id.requestItemCost);

        requestName.setText("Ride Request");
        requestDestination.setText("Destination: " +request.getToLocation());
        requestStatus.setText("Status: " + request.statusCodeToString());
        requestDate.setText("Date: "+ HelpMe.getStringDate(request.getDate()));
        requestCost.setText("Cost: $" + request.getCost());


        return convertView;
    }
}
