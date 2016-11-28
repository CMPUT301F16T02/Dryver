/*
 * Copyright (C) 2016
 *  Created by: usenka, jwu5, cdmacken, jvogel, asanche
 *  This program is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE
 *  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.dryver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.Models.Request;
import com.dryver.R;
import com.dryver.Utility.HelpMe;

import java.util.ArrayList;

/**
 * Custom adapter to display driver related request list
 *
 * @see ArrayAdapter
 * @see RequestSingleton
 */
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
        TextView rateText = (TextView) convertView.findViewById(R.id.dryverItemRate);

        riderText.setText("Rider: "+ request.getRiderId());
        destinationText.setText(HelpMe.formatLocation(request));
        dateText.setText("Date: "+ HelpMe.getDateString(request.getDate()));
        costText.setText("Cost: " + HelpMe.formatCurrencyToString(request.getCost()));
        rateText.setText("Rate: " + HelpMe.formatCurrencyToString(request.getRate()));

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