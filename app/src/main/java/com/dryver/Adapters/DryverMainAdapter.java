package com.dryver.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dryver.Activities.ActivityDryverSelection;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Models.Request;
import com.dryver.R;
import com.dryver.Utility.HelpMe;

import java.util.ArrayList;


public class DryverMainAdapter extends ArrayAdapter<Request> {
    private Context mContext;
    RequestSingleton requestSingleton = RequestSingleton.getInstance();

    public DryverMainAdapter(Context context, ArrayList<Request> requestArrayList) {
        super(context, 0, requestArrayList);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Request request = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dryver_main_item, null);
        }

        TextView riderText = (TextView) convertView.findViewById(R.id.dryverItemRiderID);
        TextView destinationText = (TextView) convertView.findViewById(R.id.dryverItemDestination);
        TextView dateText = (TextView) convertView.findViewById(R.id.dryverItemDate);
        TextView costText = (TextView) convertView.findViewById(R.id.dryverItemCost);

        riderText.setText("Rider: "+ request.getRiderId());
        destinationText.setText("Destination: " + request.getToLocation());
        dateText.setText("Date: "+ HelpMe.getDateString(request.getDate()));
        costText.setText("Cost: " + HelpMe.formatCurrency(request.getCost()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.viewRequest(mContext, request);
            }
        });

        riderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2016-11-26 clickable username.
            }
        });

        return convertView;
    }
}