package com.dryver.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.Driver;
import com.dryver.Models.Request;
import com.dryver.R;

import java.util.ArrayList;

public class ActivityDriverList extends Activity{

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private ElasticSearchController ES = ElasticSearchController.getInstance();
    private UserController userController = UserController.getInstance();

    private ListView driverListView;
    private Request request;
    private ArrayList<String> driverIds;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_list);

        request = requestSingleton.getViewedRequest();
        driverIds = request.getDrivers();

        driverListView = (ListView)findViewById(R.id.driver_list);
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, driverIds);
        driverListView.setAdapter(adapter);

        registerForContextMenu(driverListView);
        setDriverClickEvents();
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
                requestSingleton.selectDriver(request, (String)driverListView.getItemAtPosition(position));
                return true;
            case R.id.viewTheirProfile:
                String selectedDriver = (String)driverListView.getItemAtPosition(position);
                Driver driver = (new Driver(ES.getUserByString(selectedDriver)));
                userController.viewUserProfile(driver, ActivityDriverList.this);
                return true;
            default:
                return false;
        }
    }

    private void setDriverClickEvents() {
        driverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                openContextMenu(v);
            }
        });
    }
}
