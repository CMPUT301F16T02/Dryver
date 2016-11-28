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
 * A custom Array Adapter for listing requests as strings properly.
 *
 * @see ArrayAdapter
 * @see RequestSingleton
 */
public class RyderMainAdapter extends ArrayAdapter<Request> {
    private Context mContext;
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    public RyderMainAdapter(Context context, ArrayList<Request> requestArrayList) {
        super(context, 0, requestArrayList);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Request request = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ryder_main_item, null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.viewRequest(mContext, request);
            }

        });

        TextView locationTextView = (TextView) convertView.findViewById(R.id.requestItemLocation);
        TextView requestStatus = (TextView) convertView.findViewById(R.id.requestItemStatus);
        TextView requestDate = (TextView) convertView.findViewById(R.id.requestItemDate);
        TextView requestCost = (TextView) convertView.findViewById(R.id.requestItemCost);
        TextView requestRate = (TextView) convertView.findViewById(R.id.requestItemRate);

        locationTextView.setText(HelpMe.formatLocation(request));

        requestStatus.setText("Status: " + request.statusCodeToString());
        requestDate.setText("Date: " + HelpMe.getDateString(request.getDate()));
        requestCost.setText("Total Cost: " + HelpMe.formatCurrencyToString(request.getCost()));
        requestRate.setText("Rate: " + HelpMe.formatCurrencyToString(request.getRate()) + "/km");

        return convertView;
    }
}
