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

package com.ubertapp;


import com.ubertapp.Models.Rider;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * All Tests for the Rider Class.
 */

public class RiderTests {

    private final String DEFAULT_RIDER_ID = "b8sjd9sl_28sjd2u";

    /**
     * Tests the getId() function from Rider class.
     */
    @Test
    public void testGetId() {
        Rider rider = new Rider(DEFAULT_RIDER_ID);
        assertEquals(DEFAULT_RIDER_ID, rider.getUserId());
    }
}
