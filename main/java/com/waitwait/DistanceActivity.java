package com.waitwait;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DistanceActivity extends AppCompatActivity {

    private Button button5;
    private TextView txtResult;
    Location locationA = new Location("Point A");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        button5 = (Button) findViewById(R.id.button5);
        txtResult = (TextView) findViewById(R.id.textView5);
        locationA.setLatitude(36.83116);
        locationA.setLongitude(127.17639);


        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        button5.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23 &&

                        ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(DistanceActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},

                            0);
                } else {
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String provider = location.getProvider();
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    txtResult.setText("위치정보 : " + provider + "\n" + "위도 : " + longitude + "\n" + "경도 : " + latitude + "\n");

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);
                }
            }
        });
    }

    final LocationListener gpsLocationListener = new LocationListener() {

        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double distance = locationA.distanceTo(location);
            txtResult.setText("위치정보 : " + provider + "\n"+ "위도 : " + longitude + "\n" + "경도 : " + latitude + "\n"+"거리계산 :"+distance/1000 + "km");

        }



        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        public void onProviderEnabled(String provider) {
        }
        public void onProviderDisabled(String provider) {
        }

    };

}
