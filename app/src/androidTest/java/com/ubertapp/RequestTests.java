package com.ubertapp;

import android.support.test.runner.AndroidJUnit4;

import com.ubertapp.Request;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by Adam on 10/8/2016.
 */

@RunWith(AndroidJUnit4.class)
public class RequestTests
{
    private String default_did = "a5d32d2s_21se2s2";
    private String default_rid = "b8sjd9sl_28sjd2u";
    private double default_c = 1.00;


    @Test
    public void testGetCost()
    {
        Request r = new Request(10005.40, default_did, default_rid);
        assertEquals(10005.40, r.getCost(), 0.001);
    }

    @Test
    public void testGetaDriver()
    {
        String did = "fg2swd";

        Request r = new Request(10005.50, did, default_rid);

        assertEquals("fg2swd", r.getDriverId());
    }

    @Test
    public void testGetaRider()
    {
        String rid = "fg2swd";

        Request r = new Request(default_c, default_did, rid);

        assertEquals("fg2swd", r.getRiderId());
    }
}
