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

package com.dryver.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dryver.R;

/**
 * Activity provides the user with a choice to choose if he'd like to be a rider or a driver.
 */
public class ActivityRydeOrDryve extends ActivityLoggedInActionBar {
    private Button dryveButton;
    private Button rydeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ryde_or_dryve);

        dryveButton = (Button) findViewById(R.id.driver_button);
        rydeButton = (Button) findViewById(R.id.request_button);

        setListeners();
    }

    /**
     * This sets the listeners for the driver button's click and the rider button's click
     */
    public void setListeners() {
        dryveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityRydeOrDryve.this, ActivityDryverMain.class);
                startActivity(intent);
            }
        });

        rydeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivityRydeOrDryve.this, ActivityRyderMain.class);
                startActivity(intent);
            }
        });
    }
}
