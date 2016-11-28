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

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dryver.Controllers.UserController;
import com.dryver.R;

/**
 * Created by Adam on 11/14/2016.
 */

public class ActivityLoggedInActionBar extends Activity {
    protected UserController userController = UserController.getInstance();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hamburgler_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.viewMyProfile:
                userController.editUserProfile(this);
                return true;
            case R.id.logout:
                userController.logout();
                Intent intent = new Intent(ActivityLoggedInActionBar.this, ActivityOpeningPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ActivityLoggedInActionBar.this.startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
