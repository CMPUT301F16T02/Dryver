/*
 * Copyright (C) 2016
 * Created by: usenka, jwu5, cdmacken, jvogel, asanche
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.dryver.Models;

/**
 * The Request class represents a request that was sent out by the rider. A Driver that is in the
 * drivers list can then offer a ride to the Rider who made the request. It is essentially a class
 * who's sole purpose is to commpunicate b/w the Driver and Rider class whild holding important
 * information such as ride cost etc...
 */

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

/**
 * The type Request.
 */
public class Request implements Serializable {
    private String id;
    private final String riderId;
    // TODO: 2016-11-16 rework to only get the riderid and driver id and store a collection of ids, instead of the full classes.
    private Collection<String> drivers;
    private String acceptedDriverID;
    private String description;
    private Calendar date;
    //Status: 0 for pending, 1 for accepted, 2 for cancelled
    private int status;

    // [Latitude, Longitude]
    private SimpleCoordinates fromCoordinates;
    private SimpleCoordinates toCoordinates;

    private double cost;
    private double rate;

    /**
     * Instantiates a new Request.
     *
     * @param date         date the request was created
     * @param fromLocation location of the rider
     * @param toLocation   destination of the rider
     * @param rate         the rate
     */
    public Request(String riderId, Calendar date, Location fromLocation, Location toLocation, double rate) {
        this.riderId = riderId;
        this.date = date;
        this.fromCoordinates = new SimpleCoordinates(fromLocation.getLatitude(), fromLocation.getLongitude(), fromLocation.getProvider());
        this.toCoordinates = new SimpleCoordinates(toLocation.getLatitude(), toLocation.getLongitude(), toLocation.getProvider());
        this.drivers = new ArrayList<String>();
        this.acceptedDriverID = null;
        this.status = 0;
        this.rate = rate;
        this.id = null;
    }

    /**
     * Gets elastic search user id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets elastic search user id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets description for a ride.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description for a ride.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets drivers who accepted the ride.
     *
     * @return the drivers
     */
    public Collection<String> getDrivers() {
        return this.drivers;
    }

    /**
     * Gets rider userId.
     *
     * @return the rider id
     */
    public String getRiderId() {
        return this.riderId;
    }


    /**
     * Adds a driver to the list of drivers.
     *
     * @param driver the driver
     */
    public void addDriver(String driver) {
        drivers.add(driver);
    }

    public String getAcceptedDriverID() {
        return this.acceptedDriverID;
    }

    public void acceptOffer(String driverID) {
        if (drivers.contains(driverID)) {
            this.acceptedDriverID = driverID;
        } else {
            this.acceptedDriverID = null;
        }
    }

    /**
     * Cancel rider's offer to driver.
     */
    public void cancelOffer() {
        this.acceptedDriverID = null;
    }

    /**
     * Gets user's to-location.
     *
     * @return the to-location
     */
    public Location getToLocation() {
        return toCoordinates.getLocation();
    }

    /**
     * Gets user's from-location.
     *
     * @return the from-location
     */
    public Location getFromLocation() {
        return fromCoordinates.getLocation();
    }

    /**
     * Sets user's from-location.
     *
     * @param fromLocation the from location
     */
    public void setFromLocation(Location fromLocation) {
        this.fromCoordinates.setLocation(fromLocation.getLatitude(), fromLocation.getLongitude());
        this.fromCoordinates.setLocationName(fromLocation.getProvider());
    }

    /**
     * Sets user's to-location.
     *
     * @param toLocation the to location
     */
    public void setToLocation(Location toLocation) {
        this.toCoordinates.setLocation(toLocation.getLatitude(), toLocation.getLongitude());
        this.toCoordinates.setLocationName(toLocation.getProvider());
    }

    /**
     * Get the status of the request
     *
     * @return int status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set the request status
     *
     * @param status the status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Get the request's date of creation
     *
     * @return Calendar date
     */
    public Calendar getDate() {
        return this.date;
    }

    /**
     * Get the request's rate
     *
     * @return double rate
     */
    public double getRate() {
        return rate;
    }

    /**
     * Set's the request's rate
     *
     * @param rate the rate
     */
    public void setRate(double rate) {
        this.rate = rate;
    }


    public double getCost() {
        return cost;
    }

    /**
     * Generates the cost of the request using the rate
     *
     * @param rate the rate
     */
    private void generateCost(double rate) {
        // TODO: 2016-11-16 generate cost somehow.
//        Location start = new Location("start");
//        Location destination = new Location("Destination");
//
//        start.setLatitude(fromLocation.getLatitude());
//        start.setLongitude(fromLocation.getLongitude());
//
//        destination.setLatitude(toLocation.getLatitude());
//        destination.setLongitude(toLocation.getLongitude());
//
//        double distance = start.distanceTo(destination);
//        this.cost = rate * distance;
    }

    /**
     * Converts the status of the request to a string
     * 0 is Cancelled
     * 1 is Pending
     * 2 is Accepted
     *
     * @return String string
     */
    public String statusCodeToString() {
        if (status == 0) {
            return "Cancelled";
        }
        else if (status == 1) {
            return "Pending";
        }
        else if (status == 2) {
            return "Accepted";
        }
        else {
            return "Unknown Status String";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Request request = (Request) o;

        if (status != request.status) {
            return false;
        }
        if (Double.compare(request.cost, cost) != 0) {
            return false;
        }
        if (Double.compare(request.rate, rate) != 0) {
            return false;
        }
        if (riderId != null ? !riderId.equals(request.riderId) : request.riderId != null) {
            return false;
        }
        if (drivers != null ? !drivers.equals(request.drivers) : request.drivers != null) {
            return false;
        }
        if (acceptedDriverID != null ? !acceptedDriverID.equals(request.acceptedDriverID) : request.acceptedDriverID != null) {
            return false;
        }
        if (description != null ? !description.equals(request.description) : request.description != null) {
            return false;
        }
        if (date != null ? !date.equals(request.date) : request.date != null) {
            return false;
        }
        if (id != null ? !id.equals(request.id) : request.id != null) {
            return false;
        }
        if (fromCoordinates != null ? !fromCoordinates.equals(request.fromCoordinates) : request.fromCoordinates != null) {
            return false;
        }
        if (toCoordinates != null ? !toCoordinates.equals(request.toCoordinates) : request.toCoordinates != null) {
            return false;
        }
        return riderId != null ? riderId.equals(request.riderId) : request.riderId == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = riderId != null ? riderId.hashCode() : 0;
        result = 31 * result + (drivers != null ? drivers.hashCode() : 0);
        result = 31 * result + (acceptedDriverID != null ? acceptedDriverID.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (fromCoordinates != null ? fromCoordinates.hashCode() : 0);
        result = 31 * result + (toCoordinates != null ? toCoordinates.hashCode() : 0);
        temp = Double.doubleToLongBits(cost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (riderId != null ? riderId.hashCode() : 0);
        return result;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

}
