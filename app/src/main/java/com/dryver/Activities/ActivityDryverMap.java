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

package com.dryver.Activities;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.FragmentActivity;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.Models.Request;
import com.dryver.R;
import com.dryver.Utility.MapUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * Map activity for the driver. Displays map based on request coordinates. Uses Google maps and its services
 * to accomplish said tasks.
 *
 * @see FragmentActivity
 * @see GoogleApiClient
 * @see GoogleMap
 * @see Place
 */
public class ActivityDryverMap extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mClient;
    private MapUtil mapUtil = new MapUtil();

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private ArrayList<Request> requestArrayList = requestSingleton.getRequests();
    private List<LatLng> decodedPolyline;
    private ArrayList<Polyline> polylineArrayList = new ArrayList<Polyline>();

    private Request request = requestSingleton.getTempRequest();
    private LocationRequest mLocationRequest;
    private Location currentLocation = new Location("Current Location");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Instantiate GoogleApiClient, using GEO DATA and PLACE DETECTION API
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    /**
     * Overriding {@link FragmentActivity} onStart and connects
     * {@link GoogleApiClient} client instance.
     */
    @Override
    protected void onStart() {
        mClient.connect();
        super.onStart();
    }

    /**
     * Overriding {@link FragmentActivity} onStop and disconnects
     * {@link GoogleApiClient} client instance.
     */
    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    /**
     * Overriding Callback {@link GoogleApiClient.ConnectionCallbacks} onConnected and finds the
     * current location of the user using {@link LocationRequest} and location services
     *
     * @param  bundle
     * @see com.google.android.gms.location.FusedLocationProviderApi
     * @see GoogleApiClient
     */
    @Override
    public void onConnected(Bundle bundle) {
        initializeLocationRequest(1, 1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
            }
        });

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
    }

    /**
     * Overriding Callback {@link OnMapReadyCallback} onMapReady and finds the
     * route of the current request and draws it on the map. Also moves the camera to
     * that location.
     *
     * @param  googleMap
     * @see OnMapReadyCallback
     * @see GoogleMap
     * @see MapUtil
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        LatLng fromLatLng = new LatLng(request.getFromLocation().getLatitude(), request.getFromLocation().getLongitude());
        LatLng toLatLng = new LatLng(request.getToLocation().getLatitude(), request.getToLocation().getLongitude());
        mapUtil.moveMap(mMap, request.getFromLocation(), 15);

        mMap.addMarker(new MarkerOptions().position(fromLatLng).title(request.getFromAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addMarker(new MarkerOptions().position(toLatLng).title(request.getToAddress())
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        decodedPolyline = mapUtil.decodePoly(request.getEncodedPolyline());
        mapUtil.drawRoute(mMap, decodedPolyline, polylineArrayList);
    }

    /**
     * Creates a new location request to automatically query the user for their current location
     * @param locationUpdates
     * @param locationInterval
     *
     * @see LocationRequest
     */
    public void initializeLocationRequest(int locationUpdates, int locationInterval) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(locationUpdates);
        mLocationRequest.setInterval(locationInterval);
    }

    /**
     *Overriding Callback {@link GoogleApiClient.ConnectionCallbacks} onConnectionSuspended
     *
     * @param i
     * @see GoogleApiClient
     *
     */
    @Override
    public void onConnectionSuspended(int i) {
        // TODO Auto-generated method stub
    }

    /**
     * Overriding Callback {@link com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener} onConnectionSuspended
     *
     * @param connectionResult
     * @see GoogleApiClient
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO Auto-generated method stub
    }
}
