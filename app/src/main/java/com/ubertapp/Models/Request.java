package com.ubertapp.Models;


import java.util.ArrayList;
import java.util.Collection;

public class Request {
    private Rider rider;
    private Collection<Driver> drivers;

    private final double cost;
    private final String riderId;

    public Request(double cost, Rider rider) {
        this.cost = cost;
        this.rider = rider;
        this.riderId = rider.getUserId();
        this.drivers = new ArrayList<Driver>();
    }

//    public Request(double cost, String riderId) {
//        this.cost = cost;
//        this.riderId = riderId;
//    }

    public double getCost() {
        return cost;
    }

//    public String getDriverId() {
//        return driverId;
//    }

    public Collection<Driver> getDrivers() {
        return this.drivers;
    }

    public Rider getRider() {
        return this.rider;
    }

    public String getRiderId() {
        return rider.getUserId();
    }

    public void addDriver(Driver driver) {
        drivers.add(driver);
    }

}
