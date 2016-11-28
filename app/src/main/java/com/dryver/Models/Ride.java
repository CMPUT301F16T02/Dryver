/*
 * Copyright (C) 2016
 *  Created by: usenka, jwu5, cdmacken, jvogel, asanche
 *  This program is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE
 *  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.dryver.Models;

import java.io.Serializable;
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
