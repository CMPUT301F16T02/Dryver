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

import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.R;
import com.dryver.Utility.MapUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Activity provides a user an interface for selecting the to and from destination on a map.
 * Uses Google maps and Google geolocation services.
 * Help from https://www.simplifiedcoding.net/android-google-maps-tutorial-google-maps-android-api/
 * and Google's own documentation
 *
 * @see FragmentActivity
 * @see GoogleApiClient
 * @see GoogleMap
 * @see Place
 * @see PlaceAutocomplete
 * @see Geocoder
 */

public class ActivityRequestMap extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mClient;
    private ArrayList<Marker> mRoute;
    private LocationRequest mLocationRequest;
    private Location currentLocation;

    private static final LatLngBounds EDMONTON_BOUNDS = new LatLngBounds(new LatLng(53.420980, -113.686921), new LatLng(53.657243, -113.330552));
    private static final int REQUEST_SELECT_PLACE = 0;
    private static final String API_KEY = "AIzaSyCqP3QKEmHTVQ7Tq1NFPNS5Ex28xZSuG2o";
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private String routeURL;
    private String encodedPolyline;
    private ArrayList<Polyline> polylineArrayList = new ArrayList<Polyline>();
    private double routeDistance;
    private MapUtil mapUtil = new MapUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_map);

        mRoute = new ArrayList<>();
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
     * Inflates the menu used in this activity
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    /**
     * Details the action taken depending on the button pressed
     * Search: Search for a place on the map, uses google place and autocomplete
     * Delete: Deletes all markers and routes on the map
     * Forward: Sends the coordinates and route of markers to next activity
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder
                            (PlaceAutocomplete.MODE_OVERLAY)
                            .setBoundsBias(EDMONTON_BOUNDS)
                            .build(ActivityRequestMap.this);
                    startActivityForResult(intent, REQUEST_SELECT_PLACE);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_delete:
                mMap.clear();
                mRoute.clear();
                return true;

            case R.id.action_forward:
                if (mRoute.size() == 2) {
                    ArrayList<Location> locations = mRouteToLocation();
                    ArrayList<String> addresses = mRouteToAddress();
                    if ((addresses.get(0) != null) && (addresses.get(1) != null)) {
                        Log.i("REQUEST MAP: ", "From Address: " + addresses.get(0));
                        Log.i("REQUEST MAP: ", "To Address: " + addresses.get(1));
                    }

                    requestSingleton.getTempRequest().setFromLocation(locations.get(0));
                    requestSingleton.getTempRequest().setToLocation(locations.get(1));
                    requestSingleton.getTempRequest().setDistance(routeDistance);
                    requestSingleton.getTempRequest().setFromAddress(addresses.get(0));
                    requestSingleton.getTempRequest().setToAddress(addresses.get(1));
                    requestSingleton.getTempRequest().setEncodedPolyline(encodedPolyline);
                    Toast.makeText(this.getApplicationContext(), "Set distance to: " + routeDistance, Toast.LENGTH_SHORT).show();

                    mMap.clear();
                    mRoute.clear();

                    finish();
                } else {
                    Toast.makeText(ActivityRequestMap.this, "Make sure you have selected 2 points!!!",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onConnected(Bundle bundle) {
        initializeLocationRequest(100, 1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
                mapUtil.moveMap(mMap, currentLocation, 15);
            }
        });

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if (currentLocation != null) {
            mapUtil.moveMap(mMap, currentLocation, 15);
        }
    }

    /**
     * Overriding Callback {@link OnMapReadyCallback} onMapReady and finds the
     * route of the current request and draws it on the map. Also moves the camera to
     * that location.
     *
     * Also sets on long click listeners to allow the user to set start and end locations.
     * Routes are drawn immediately after the user selects the end location.
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
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                if (mRoute.size() == 0) {
                    //Start location
                    mRoute.add(mMap.addMarker(new MarkerOptions().position(point).title("Start Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                } else if (mRoute.size() == 1) {
                    //End location
                    mRoute.add(mMap.addMarker(new MarkerOptions().position(point).title("End Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))));
                    ArrayList<Location> locations = mRouteToLocation();

                    generateRouteURL(locations.get(0), locations.get(1));
                    new FetchDirectionTask().execute();

                    ArrayList<String> addresses = mRouteToAddress();
                    if ((addresses.get(0) != null) && (addresses.get(1) != null)) {
                        Log.i("REQUEST MAP: ", "From Address: " + addresses.get(0));
                        Log.i("REQUEST MAP: ", "To Address: " + addresses.get(1));
                    }
                } else {
                    Marker removeMarker = mRoute.get(1);
                    mRoute.remove(removeMarker);
                    removeMarker.remove();
                    mRoute.add(mMap.addMarker(new MarkerOptions().position(point).title("End Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))));
                    mapUtil.removePolylines(polylineArrayList);
                    ArrayList<Location> locations = mRouteToLocation();
                    generateRouteURL(locations.get(0), locations.get(1));
                    new FetchDirectionTask().execute();
                }
            }
        });
    }

    /**
     * Helper function used to convert {@link LatLng} to {@link Location}
     * @return ArrayList of locations
     */
    private ArrayList<Location> mRouteToLocation() {
        ArrayList<Location> returnLocationArrayList = new ArrayList<Location>();
        Location fromLocation = new Location("Start");
        Location toLocation = new Location("End");

        fromLocation.setLatitude(mRoute.get(0).getPosition().latitude);
        fromLocation.setLongitude(mRoute.get(0).getPosition().longitude);
        toLocation.setLatitude(mRoute.get(1).getPosition().latitude);
        toLocation.setLongitude(mRoute.get(1).getPosition().longitude);

        returnLocationArrayList.add(fromLocation);
        returnLocationArrayList.add(toLocation);

        return returnLocationArrayList;
    }


    /**
     * Helper function used to convert {@link LatLng} to {@link Address}
     * This is accomplished through the use of {@link Geocoder}
     *
     * @see Geocoder
     * @see AsyncTask
     * @return ArrayList of addresses
     */
    private ArrayList<String> mRouteToAddress() {
        String fromAddress = null;
        String toAddress = null;
        GetAddressTask fromAddressTask;
        GetAddressTask toAddressTask;
        ArrayList<Location> toFromLocation = mRouteToLocation();
        ArrayList<String> returnAddressArrayList = new ArrayList<String>();

        fromAddressTask = new GetAddressTask(toFromLocation.get(0));
        toAddressTask = new GetAddressTask(toFromLocation.get(1));
        fromAddressTask.execute();
        toAddressTask.execute();

        try {
            fromAddress = fromAddressTask.get();
            toAddress = toAddressTask.get();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        }

        returnAddressArrayList.add(fromAddress);
        returnAddressArrayList.add(toAddress);

        return returnAddressArrayList;
    }

    /**
     * Receives the data from {@link PlaceAutocomplete} search intent and moves the map to that location
     *
     * @see PlaceAutocomplete
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Location location = new Location("place");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                mapUtil.moveMap(mMap, location, 15);
            }
        }
    }

    /**
     * Generates the URL needed to get routes using Google Directions
     *
     * @param fromLocation
     * @param toLocation
     */
    public void generateRouteURL(Location fromLocation, Location toLocation) {
        String fromLocationCoord = "" + fromLocation.getLatitude() + "," + fromLocation.getLongitude();
        String toLocationCoord = "" + toLocation.getLatitude() + "," + toLocation.getLongitude();
        routeURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + fromLocationCoord + "&" + "destination=" + toLocationCoord + "&key=" + API_KEY;
        Log.i("REQUEST MAP: ", "URL: " + routeURL);
    }

    /**
     * Opens a HTTPS request and sends the URL needed to get routes.
     * The data is saved to a string (Google direction returns a JSON object)
     *
     * @see URL
     * @see HttpURLConnection
     * @param directionURL
     * @return JSON object as a String
     * @throws IOException
     */
    public String getDataFromUrl(String directionURL) throws IOException {
        URL url = new URL(directionURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = urlConnection.getInputStream();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(urlConnection.getResponseMessage() +
                        ": with " + directionURL);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return new String(out.toByteArray());
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Async task that fetches the JSON object from Google directions given the URL.
     * Parses the Json object to obtain the encoded overview polyline
     * Draws the route after background task finishes executing
     *
     * @see <a href="https://developers.google.com/maps/documentation/utilities/polylinealgorithm">Decoding Polyline</a>
     * @see MapUtil
     */
    private class FetchDirectionTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String encodedPoly = null;
            try {
                String jsonResult = getDataFromUrl(routeURL);
                encodedPoly = mapUtil.toEncodedPoly(jsonResult);
            } catch (IOException ioe) {
                Log.e("REQUEST MAP: ", "FAILED TO FETCH ITEMS", ioe);
            }
            return encodedPoly;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                return;
            }
            encodedPolyline = result;

            List<LatLng> routePoly = mapUtil.decodePoly(result);
            routeDistance = mapUtil.drawRoute(mMap, routePoly, polylineArrayList);
        }
    }

    /**
     * Async task that gets the address of coordinates
     * Uses {@link Geocoder}.
     *
     * @see Geocoder
     */
    private class GetAddressTask extends AsyncTask<String, String, String> {
        double latitude;
        double longitude;

        GetAddressTask(Location location) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            Geocoder geocoder = new Geocoder(ActivityRequestMap.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                result = addresses.get(0).getAddressLine(0);
            } catch (IOException ioe) {
                Log.e("REQUEST MAP: ", "Failed to get address", ioe);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO Auto-generated method stub
    }
}