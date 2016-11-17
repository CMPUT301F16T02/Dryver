package com.dryver.Models;

import android.location.Location;

import java.io.Serializable;

import io.searchbox.indices.template.TemplateAction;

/**
 * Created by drei on 2016-11-16.
 */

public class SimpleCoordinates implements Serializable {
    private Double[] location = new Double[2];
    private String locationName;


    SimpleCoordinates(Double latitude, Double longitude, String locationName) {
        this.location[0] = latitude;
        this.location[1] = longitude;
        this.locationName = locationName;
    }

    public void setLocation(Double latitude, Double longitude) {
        this.location[0] = latitude;
        this.location[1] = longitude;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLatitude() {
        return this.location[0];
    }

    public Double getLongitude() {
        return this.location[1];
    }

    public Location getLocation() {
        Location tempLocation = new Location(this.locationName);
        tempLocation.setLatitude(getLatitude());
        tempLocation.setLongitude(getLongitude());
        return tempLocation;
    }
}
