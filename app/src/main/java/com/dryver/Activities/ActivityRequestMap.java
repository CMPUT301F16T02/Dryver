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
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.R;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity provides a user an interface for selecting the to and from destination on a map.
 */
public class ActivityRequestMap extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        PlaceSelectionListener {

    private GoogleMap map;
    private GoogleApiClient mClient;
    private ArrayList<Marker> mRoute;

    private Location currentLocation;
    private LocationRequest mLocationRequest;
    private static final LatLngBounds edmontonBounds = new LatLngBounds(new LatLng(53.420980, -113.686921), new LatLng(53.657243, -113.330552));
    private static final int REQUEST_SELECT_PLACE = 0;
    private static final String API_KEY = "AIzaSyCqP3QKEmHTVQ7Tq1NFPNS5Ex28xZSuG2o";
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private String routeURL;
    private ArrayList<Polyline> polylineArrayList = new ArrayList<Polyline>();
    private int routeDistance;

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
            case R.id.action_direction:
                //TODO generate direction
                if (mRoute.size() == 2 ) {
                    Location fromLocation = new Location("Start");
                    Location toLocation = new Location("End");

                    fromLocation.setLatitude(mRoute.get(0).getPosition().latitude);
                    fromLocation.setLongitude(mRoute.get(0).getPosition().longitude);
                    toLocation.setLatitude(mRoute.get(1).getPosition().latitude);
                    toLocation.setLongitude(mRoute.get(1).getPosition().longitude);
                    generateRouteURL(fromLocation, toLocation);
                    new FetchItemsTask().execute();
                }
                return true;

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
                map.clear();
                mRoute.clear();
                return true;

            case R.id.action_forward:
                if (mRoute.size() == 2) {
                    Location fromLocation = new Location("Start");
                    Location toLocation = new Location("End");

                    fromLocation.setLatitude(mRoute.get(0).getPosition().latitude);
                    fromLocation.setLongitude(mRoute.get(0).getPosition().longitude);
                    toLocation.setLatitude(mRoute.get(1).getPosition().latitude);
                    toLocation.setLongitude(mRoute.get(1).getPosition().longitude);

                    requestSingleton.getTempRequest().setFromLocation(fromLocation);
                    requestSingleton.getTempRequest().setToLocation(toLocation);
                    requestSingleton.getTempRequest().setDistance(routeDistance);

                    map.clear();
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
                moveMap(currentLocation);
            }
        });

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if (currentLocation != null) {
            moveMap(currentLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                if (mRoute.size() == 0) {
                    //Start location
                    mRoute.add(map.addMarker(new MarkerOptions().position(point).title("Start Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                }
                else if (mRoute.size() == 1) {
                    //End location
                    mRoute.add(map.addMarker(new MarkerOptions().position(point).title("End Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))));
                }
                else {
                    Marker removeMarker = mRoute.get(1);
                    mRoute.remove(removeMarker);
                    removeMarker.remove();
                    mRoute.add(map.addMarker(new MarkerOptions().position(point).title("End Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))));
                    removePolylines();
                }
            }
        });
    }

    public void moveMap(Location location) {
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    public void initializeLocationRequest(int LOCATION_UPDATES, int LOCATION_INTERVAL) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(LOCATION_UPDATES);
        mLocationRequest.setInterval(LOCATION_INTERVAL);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Location location = new Location("place");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                moveMap(location);
            }
        }
    }

    public void generateRouteURL(Location fromLocation, Location toLocation) {
        String fromLocationCoord = "" + fromLocation.getLatitude() + "," + fromLocation.getLongitude();
        String toLocationCoord = "" + toLocation.getLatitude() + "," + toLocation.getLongitude();
        routeURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + fromLocationCoord + "&" + "destination=" + toLocationCoord + "&key=" + API_KEY;
        Log.i("REQUEST MAP: ", "URL: " + routeURL);
    }

    public String getDataFromUrl(String directionURL) throws IOException{
        URL url = new URL(directionURL);
        HttpURLConnection urlConnection  = (HttpURLConnection) url.openConnection();

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

    private class FetchItemsTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... params) {
            String encodedPoly = null;
            try {
                String jsonResult = getDataFromUrl(routeURL);
                encodedPoly = toEncodedPoly(jsonResult);
            } catch (IOException ioe) {
                Log.e("REQUEST MAP: ", "FAILED TO FETCH ITEMS", ioe);
            }
            return encodedPoly;
        }

        protected void onPostExecute(String result) {
            if (result == null) {
                return;
            }
            List<LatLng> routePoly = decodePoly(result);
            for (int i = 0; i < (routePoly.size() - 1); i++) {
                LatLng point1 = routePoly.get(i);
                LatLng point2 = routePoly.get(i+1);

                Location location1 = new Location("1");
                location1.setLatitude(point1.latitude);
                location1.setLongitude(point1.longitude);
                Location location2 = new Location("2");
                location2.setLatitude(point2.latitude);
                location2.setLongitude(point2.longitude);

                routeDistance += location1.distanceTo(location2);
                polylineArrayList.add(map.addPolyline(new PolylineOptions()
                        .add(point1, point2)
                        .width(5)
                        .color(Color.RED)));
            }
        }
    }
    //Code taken from http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public String toEncodedPoly (String json) {
        String encodedPoly = null;
        try {
            JSONObject jsonObject = new JSONObject(json).getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline");
            encodedPoly = jsonObject.getString("points");
        } catch (JSONException je) {
            Log.e("REQUEST MAP: ", "Failed to parse JSON", je);
        }
        return encodedPoly;
    }

    public void removePolylines() {
        if (polylineArrayList.size() != 0) {
            for (Polyline polyline : polylineArrayList) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onPlaceSelected(Place place) {

    }

    @Override
    public void onError(Status status) {

    }
}