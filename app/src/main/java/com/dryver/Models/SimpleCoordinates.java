package com.dryver.Models;

import android.location.Location;

import java.io.Serializable;
import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleCoordinates that = (SimpleCoordinates) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(location, that.location)) return false;
        return locationName != null ? locationName.equals(that.locationName) : that.locationName == null;

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(location);
        result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
        return result;
    }
}
