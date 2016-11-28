package com.dryver.Models;

import android.location.Location;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Simplified coordinate system for cleaner serialization
 */

public class SimpleCoordinates implements Serializable {
    private Double[] location = new Double[2];
    private String locationName;

    public SimpleCoordinates(Location location) {
        this.location[0] = location.getLatitude();
        this.location[1] = location.getLongitude();
        this.locationName = location.getProvider();
    }

    /**
     * initializes the coords
     * @param latitude
     * @param longitude
     * @param locationName
     */
    public SimpleCoordinates(Double latitude, Double longitude, String locationName) {
        this.location[0] = latitude;
        this.location[1] = longitude;
        this.locationName = locationName;
    }

    /**
     * Sets the location
     * @param latitude
     * @param longitude
     */
    public void setLocation(Double latitude, Double longitude) {
        this.location[0] = latitude;
        this.location[1] = longitude;
    }

    /**
     * Sets the location name
     * @param locationName
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * Gets the latitiude
     * @return Double
     */
    public Double getLatitude() {
        return this.location[0];
    }

    /**
     * Gets the logitude
     * @return Double
     */
    public Double getLongitude() {
        return this.location[1];
    }

    public Location getLocation() {
        Location tempLocation = new Location(this.locationName);
        tempLocation.setLatitude(getLatitude());
        tempLocation.setLongitude(getLongitude());
        return tempLocation;
    }

    public String getLocationName() {
        return locationName;
    }

    public Double[] getDoubleLocation(){
        return location;
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
