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

package com.ubertapp.ClassTests;


import com.ubertapp.Models.Driver;
import com.ubertapp.Models.User;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * All Tests for the Driver Class.
 * @see Driver
 */

public class DriverTests {

    private final String DEFAULT_DRIVER_ID = "a5d32d2s_21se2s2";

    /**
     * Tests the getId() function from Driver class.
     */
    @Test
    public void testGetId() {
        User user = new User(DEFAULT_DRIVER_ID);
        Driver driver = new Driver(user);
        assertEquals(DEFAULT_DRIVER_ID, driver.getUserId());
    }
}
