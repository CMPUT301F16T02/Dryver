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

package com.dryver.Utility;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Map utility class that has helper functions used to interact with maps
 *
 * @see GoogleMap
 * @see com.google.android.gms.maps.model.Marker
 * @see Polyline
 */
public class MapUtil {

    /**
     * Empty constructor
     */
    public MapUtil() {

    }

    /**
     * Function used to decode polylines
     * Credit to http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     *
     * @see <a href="https://developers.google.com/maps/documentation/utilities/polylinealgorithm">Decoding Polyline</a>
     * @param encoded
     * @return List of {@link LatLng} with all the nodes required to draw a route
     */
    public List<LatLng> decodePoly(String encoded) {
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

    /**
     * Helper function that parses the return JSON from Google Directions and extracts the Overview Polyline field
     *
     * @see JSONObject
     * @param json
     * @return encoded polyline String
     */
    public String toEncodedPoly(String json) {
        String encodedPoly = null;
        try {
            JSONObject jsonObject = new JSONObject(json).getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline");
            encodedPoly = jsonObject.getString("points");
        } catch (JSONException je) {
            Log.e("REQUEST MAP: ", "Failed to parse JSON", je);
        }
        return encodedPoly;
    }

    /**
     * Helper function to move the map given a location and zoom level
     *
     * @param map
     * @param location
     * @param zoom
     */
    public void moveMap(GoogleMap map, Location location, int zoom) {
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    /**
     * Helper function to remove all polylines
     *
     * @param polylineArrayList
     */
    public void removePolylines(ArrayList<Polyline> polylineArrayList) {
        if (polylineArrayList.size() != 0) {
            for (Polyline polyline : polylineArrayList) {
                polyline.remove();
            }
        }
    }

    /**
     * Helper function used to draw routes on the map given 2 locations
     *
     * @param mMap
     * @param decodedPolyLine
     * @param polylineArrayList
     * @return
     */
    public int drawRoute(GoogleMap mMap, List<LatLng> decodedPolyLine, ArrayList<Polyline> polylineArrayList) {
        int routeDistance = 0;
        for (int i = 0; i < (decodedPolyLine.size() - 1); i++) {
            LatLng point1 = decodedPolyLine.get(i);
            LatLng point2 = decodedPolyLine.get(i + 1);

            Location location1 = new Location("1");
            location1.setLatitude(point1.latitude);
            location1.setLongitude(point1.longitude);
            Location location2 = new Location("2");
            location2.setLatitude(point2.latitude);
            location2.setLongitude(point2.longitude);

            routeDistance += location1.distanceTo(location2);
            polylineArrayList.add(mMap.addPolyline(new PolylineOptions()
                    .add(point1, point2)
                    .width(5)
                    .color(Color.RED)));
        }
        return routeDistance;
    }

}
