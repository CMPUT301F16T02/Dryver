package com.dryver.Activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dryver.Adapters.DryverListAdapter;
import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.Driver;
import com.dryver.R;
import com.dryver.Utility.ICallBack;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity for the rider to view all drivers that's associated with a particular request
 *
 * @see ActivityLoggedInActionBar
 * @see RequestSingleton
 * @see ElasticSearchController
 * @see UserController
 */
public class ActivityRequestDriverList extends ActivityLoggedInActionBar {

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private ElasticSearchController ES = ElasticSearchController.getInstance();
    private UserController userController = UserController.getInstance();

    private ListView driversListView;
    private ArrayList<String> drivers;
    private ArrayAdapter<String> adapter;
    private SwipeRefreshLayout swipeContainer;

    private Timer timer;

    /**
     * OnCreate to generate all UI related elements
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver_list);

        drivers = requestSingleton.getTempRequest().getDrivers();
        driversListView = (ListView) findViewById(R.id.drivers_list);

        adapter = new DryverListAdapter(this, drivers);
        driversListView.setAdapter(adapter);

        assignElements();
        setListeners();
    }

    /**
     * Overrding {@link android.app.Activity} onResume to refresh the driver list and reset timer
     */
    @Override
    public void onResume () {
        super.onResume();
        refreshDriverList();
        setTimer();
    }

    /**
     * Overrding {@link android.app.Activity} onPause to cancel timer
     */
    @Override
    public void onPause(){
        Log.i("trace", "ActivityDryverMain.onPause()");
        super.onPause();
        timer.cancel();
    }

    /**
     * Assigns swipe element
     */
    private void assignElements(){
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainerDriver);
    }

    /**
     * Sets listener for swiping
     */
    private void setListeners(){
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                beginRefresh();
            }
        });
    }

    /**
     * Begins the refreshing of the driver list
     */
    public void beginRefresh() {
         requestSingleton.updateTempRequest(new ICallBack() {
            @Override
            public void execute() {
                refreshDriverList();
            }
        });
    }

    /**
     * Called after data in driver list has changed
     */
    private void refreshDriverList() {
        Log.i("trace", "ActivityRiderMain.refreshRequestList()");
        swipeContainer.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    /**
     * Sets the timer for polling
     */
    private void setTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        beginRefresh();
                    }
                });
            }
        }, 0, 30000);//put here time 1000 milliseconds=1 second
    }

}
