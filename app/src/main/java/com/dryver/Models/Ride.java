package com.dryver.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by drei on 2016-11-27.
 */

public class Ride implements Serializable {

    private String id;
    private final String riderId;
    private final String driverId;
    private String description;
    private Calendar date;

    private SimpleCoordinates fromCoordinates;
    private SimpleCoordinates toCoordinates;
    private double cost;
    private double rate = 0.70;
    private double distance = 1.00;

    public Ride(Request request) {
        this.id = UUID.randomUUID().toString();
        this.riderId = request.getRiderId();
        this.driverId = request.getAcceptedDriverID();
        this.description = request.getDescription();
        this.date = request.getDate();

        this.fromCoordinates = new SimpleCoordinates(request.getFromLocation());
        this.toCoordinates = new SimpleCoordinates(request.getToLocation());
        this.cost = request.getCost();
        this.rate = request.getRate();
        this.distance = request.getDistance();
    }
}
