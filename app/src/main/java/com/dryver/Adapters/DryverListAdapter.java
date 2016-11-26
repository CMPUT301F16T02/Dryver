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
