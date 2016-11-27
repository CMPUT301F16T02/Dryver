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
 * Created by Jiawei on 11/26/2016.
 */
public class MapUtil {

    public MapUtil() {

    }

    //Code taken from http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
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

    public void moveMap(GoogleMap map, Location location, int zoom) {
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    public void removePolylines(ArrayList<Polyline> polylineArrayList) {
        if (polylineArrayList.size() != 0) {
            for (Polyline polyline : polylineArrayList) {
                polyline.remove();
            }
        }
    }

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
