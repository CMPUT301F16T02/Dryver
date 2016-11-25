package com.dryver.Controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dryver.Models.Request;
import com.dryver.R;

import java.util.ArrayList;



public class DryverMainAdapter extends ArrayAdapter<Request> {
    private Context mContext;

    public DryverMainAdapter(Context context, ArrayList<Request> requestsArrayList) {
        super(context, 0, requestsArrayList);
        this.mContext = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Request request = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dryver_main_item, null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016-11-24 implement view driver profile here.
//                RequestSingleton RS = RequestSingleton.getInstance();
//
//                RS.setViewedRequest(request);
//                Intent intent = new Intent(mContext, ActivityDriverSelection.class);
//                intent.putExtra("position", position);
//                mContext.startActivity(intent);
            }

        });

        TextView driverIdTextView = (TextView) convertView.findViewById(R.id.driver_item_driverid);

//        driverIdTextView.setText(driverId);
        return convertView;
    }
}