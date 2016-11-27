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

package com.dryver.Activities;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

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
 */
public class ActivityRequestMap extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        PlaceSelectionListener {

    private GoogleMap mMap;
    private GoogleApiClient mClient;
    private ArrayList<Marker> mRoute;
    private LocationRequest mLocationRequest;
    private Location currentLocation;

    private static final LatLngBounds edmontonBounds = new LatLngBounds(new LatLng(53.420980, -113.686921), new LatLng(53.657243, -113.330552));
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

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder
                            (PlaceAutocomplete.MODE_OVERLAY)
                            .setBoundsBias(edmontonBounds)
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

    @Override
    protected void onStart() {
        mClient.connect();
        super.onStart();
    }

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

    public ArrayList<Location> mRouteToLocation() {
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

    public ArrayList<String> mRouteToAddress() {
        String fromAddress = null;
        String toAddress = null;
        getAddressTask fromAddressTask;
        getAddressTask toAddressTask;
        ArrayList<Location> toFromLocation = mRouteToLocation();
        ArrayList<String> returnAddressArrayList = new ArrayList<String>();

        fromAddressTask = new getAddressTask(toFromLocation.get(0));
        toAddressTask = new getAddressTask(toFromLocation.get(1));
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

    public void generateRouteURL(Location fromLocation, Location toLocation) {
        String fromLocationCoord = "" + fromLocation.getLatitude() + "," + fromLocation.getLongitude();
        String toLocationCoord = "" + toLocation.getLatitude() + "," + toLocation.getLongitude();
        routeURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + fromLocationCoord + "&" + "destination=" + toLocationCoord + "&key=" + API_KEY;
        Log.i("REQUEST MAP: ", "URL: " + routeURL);
    }

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

    private class FetchDirectionTask extends AsyncTask<Void, Void, String> {
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

        protected void onPostExecute(String result) {
            if (result == null) {
                return;
            }
            encodedPolyline = result;

            List<LatLng> routePoly = mapUtil.decodePoly(result);
            routeDistance = mapUtil.drawRoute(mMap, routePoly, polylineArrayList);
        }
    }

    private class getAddressTask extends AsyncTask<String, String, String> {
        double latitude;
        double longitude;

        public getAddressTask(Location location) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        }

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

        protected void onPostExecute(String result) {
        }
    }

    public void initializeLocationRequest(int LOCATION_UPDATES, int LOCATION_INTERVAL) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(LOCATION_UPDATES);
        mLocationRequest.setInterval(LOCATION_INTERVAL);
    }

    @Override
    public void onPlaceSelected(Place place) {

    }

    @Override
    public void onError(Status status) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}