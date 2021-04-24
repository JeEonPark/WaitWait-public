package com.waitwait;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class GetDistanceClass extends AppCompatActivity {

    Location locationA = new Location("Point A");
    double distance;

    Location location;

    LocationManager lm;


    double locationTest;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


    }

    public double GetDiatancefromGPS(double Lati, double Longi, Activity ac, Context c) {

        locationA.setLatitude(Lati);
        locationA.setLongitude(Longi);


        if (Build.VERSION.SDK_INT >= 23 &&

                ContextCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ac, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {

            lm = (LocationManager)c.getSystemService(LOCATION_SERVICE);
            List<String> providers = lm.getProviders(true);
            Location bestLocation = null;
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);

            for (String provider : providers) {
                Location l = lm.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            distance = locationA.distanceTo(bestLocation);


            System.out.println(bestLocation.getLatitude() + " / " + bestLocation.getLongitude() + " / " + bestLocation.getProvider());

            System.out.println("Distance is " + distance);


        }




        return distance;

    }

    final LocationListener gpsLocationListener = new LocationListener() {

        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double distance = locationA.distanceTo(location);

            System.out.println("in OnLocationChaanged : " + location.getLatitude() + " / " + location.getLongitude() + " / dis " + distance);

            locationTest = distance;

        }



        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        public void onProviderEnabled(String provider) {
        }
        public void onProviderDisabled(String provider) {
        }

    };

}