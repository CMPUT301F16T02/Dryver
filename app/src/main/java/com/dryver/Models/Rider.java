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

/**
 * The Rider class represents a rider. That meaning it can request rides, and then accept them from
 * an instance of the Driver class after specifying some parameters about the ride.
 */

public class Rider extends User {
    public Rider(User user) {
        super(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getEmail());
    }
}