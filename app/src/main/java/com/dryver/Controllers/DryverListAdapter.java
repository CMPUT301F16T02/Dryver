package com.dryver.Controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dryver.R;

import java.util.ArrayList;


public class DryverListAdapter extends ArrayAdapter<String> {
    private Context mContext;

    public DryverListAdapter(Context context, ArrayList<String> driverArrayList) {
        super(context, 0, driverArrayList);
        this.mContext = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String driverId = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_item, null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: TEST THIS MFs
                // Not sure if this works or not, It should though
                UserController.getInstance().viewUserProfile(ElasticSearchController.getInstance().getUserByString(getItem(position)), mContext);
            }

        });

        TextView driverIdTextView = (TextView) convertView.findViewById(R.id.driver_item_driverid);

        driverIdTextView.setText(driverId);
        return convertView;
    }
}
