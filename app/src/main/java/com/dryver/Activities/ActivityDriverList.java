package com.dryver.Activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dryver.Controllers.DriverListAdapter;
import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.Driver;
import com.dryver.Models.Request;
import com.dryver.R;
import com.dryver.Utility.ICallBack;

import java.util.ArrayList;

public class ActivityDriverList extends ActivityLoggedInActionBar {

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private ElasticSearchController ES = ElasticSearchController.getInstance();
    private UserController userController = UserController.getInstance();

    private ListView driversListView;
    private ArrayList<String> drivers;
    private ArrayAdapter<String> adapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_list);

        drivers = requestSingleton.getTempRequest().getDrivers();

        driversListView = (ListView) findViewById(R.id.drivers_list);
        adapter = new DriverListAdapter(this, drivers);
        driversListView.setAdapter(adapter);

        registerForContextMenu(driversListView);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainerDriver);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                beginRefresh();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.driver_list_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        switch (item.getItemId()) {
            case R.id.chooseDriver:
                requestSingleton.selectDriver(requestSingleton.getTempRequest(), (String) driversListView.getItemAtPosition(position));
                return true;
            case R.id.viewTheirProfile:
                String selectedDriver = (String)driversListView.getItemAtPosition(position);
                Driver driver = (new Driver(ES.getUserByString(selectedDriver)));
                userController.viewUserProfile(driver, ActivityDriverList.this);
                return true;
            default:
                return false;
        }
    }

    /**
     * Begins the refreshing of the driver list
     */
    public void beginRefresh() {
    }

}
