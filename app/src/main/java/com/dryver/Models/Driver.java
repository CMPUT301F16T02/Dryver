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
 * The Driver Class represents a driver when instantiated. That meaning an actual person who is
 * capable of offering "rides" to instances of the Rider class.
 */

public class Driver extends User {
    private String vehicleDescription;

    public Driver(User user) {
        super(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getEmail());
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public void setVehicleDescription(String vehicleDescription) {
        this.vehicleDescription = vehicleDescription;
    }

    @Override
    public String toString(){
        return getId();
    }
}
