package com.example.dilkom_hak.assignment4;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


public class GPSService extends Service {
    // >>>>>>>    https://www.youtube.com/watch?v=Yg3BfVNWM_I
    // SIRADA BU VAR: https://www.youtube.com/watch?v=Bjsx9mRBAKM

    // >>>>> https://www.youtube.com/watch?v=gm5n_hRIR-c <--- sonra bunu izle
    //IKI EYLUL latitude : 39.814618    , longitude : 30.536218
    double ikiEylulLatitude = 39.814618, ikiEylulLongitude = 30.533107;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "GPS SERVICE RUNNING ;)", Toast.LENGTH_SHORT).show();


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        locationManager.requestLocationUpdates(provider, 1000, 20, new LocationListener() {
            @Override
            public void onLocationChanged(Location currentLocation) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                Log.d("HAKKE", "onLocationChanged" + " Current Lat:" + latitude + ", Long:" + longitude);

                Location ikiEylulLocation = new Location("ikiEylul");
                ikiEylulLocation.setLatitude(ikiEylulLatitude);
                ikiEylulLocation.setLongitude(ikiEylulLatitude);

                float distance = ikiEylulLocation.distanceTo(currentLocation);

                Log.d("HAKKE", "onLocationChanged" + " Distance between Ikı Eylul and Current: " + distance);

                if (distance <= 50) {

                    //BURASI DEGISECEK
                    Intent intent = new Intent();
                    intent.putExtra("check_status",true);
                    intent.setAction("detect_location_status");
                    sendBroadcast(intent);

                    Log.d("HAKKE", "Welcome to iki eylül");
                    //eğer distance 50m den az ise,
                    //notification manager çalışacak ve telefona bildirim göndereceğiz...

                }
                else
                {
                    Intent intent = new Intent();
                    intent.putExtra("check_status",false);
                    intent.setAction("detect_location_status");
                    sendBroadcast(intent);
                }

                Toast.makeText(GPSService.this, "GPS SERVICE: Curr Post: " + latitude+","+longitude, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });


        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
