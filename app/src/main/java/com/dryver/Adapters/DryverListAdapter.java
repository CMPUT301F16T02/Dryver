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
import android.widget.Button;
import android.widget.TextView;

import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.R;
import com.dryver.Utility.ICallBack;

import java.util.ArrayList;

/**
 * Custom adapter to display driver list when viewed by a rider
 *
 * @see ArrayAdapter
 * @see RequestSingleton
 * @see UserController
 */
public class DryverListAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private UserController userController = UserController.getInstance();
    private Button acceptDriverButton;
    private String driverID;

    public DryverListAdapter(Context context, ArrayList<String> driverArrayList) {
        super(context, 0, driverArrayList);
        this.mContext = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        driverID = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request_driver_item, null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: TEST THIS MFs
                // Not sure if this works or not, It should though
                UserController.getInstance().viewUserProfile(ElasticSearchController.getInstance().getUserByString(getItem(position)), mContext);
            }

        });

        acceptDriverButton = (Button) convertView.findViewById(R.id.request_driver_accept_button);
        TextView driverIdTextView = (TextView) convertView.findViewById(R.id.driver_item_driverid);

        isAccepted();

        acceptDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestSingleton.selectDriverFromTempRequest(userController.getActiveUser().getId(), new ICallBack() {
                    @Override
                    public void execute() {
                        isAccepted();
                    }
                });
            }
        });
        driverIdTextView.setText(driverID);
        return convertView;
    }

    private void isAccepted() {
        if (requestSingleton.getTempRequest().getAcceptedDriverID() != null) {
            if (requestSingleton.getTempRequest().isAcceptedDriver(driverID)) {
                acceptDriverButton.setText("Accepted");
            } else {
                acceptDriverButton.setText("Rejected");
            }
            acceptDriverButton.setEnabled(false);
        }
    }
}
