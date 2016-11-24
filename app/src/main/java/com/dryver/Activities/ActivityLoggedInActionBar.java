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
