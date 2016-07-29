package com.example.mihirvaghela.notes.track;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by King on 2015.10.30..
 */
public class LocationReceiver {
    private Context mContext;
    private Double latitude;
    private Double longitude;
    private LocationManager locManager;


    public LocationReceiver(Context context) {
        mContext = context;
    }

    public Location getLastLocation1() {
        locManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 0, locationListener);
        return locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public Location getLastLocation() {
        Location location = null;

        try {
            locManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {

                if (isGPSEnabled) {
                    if (location == null) {
                        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 0, locationListener);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locManager != null) {
                            location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            return location;
                        }
                    }
                }

                if (isNetworkEnabled) {
                    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 0, locationListener);
                    Log.d("Network", "Network");
                    if (locManager != null) {
                        location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        return location;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

}
