package com.example.dilkom_hak.assignment4;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;
//server key for direction api: AIzaSyApwxIXVb9XGS9FGCid8olKQ0ST_Uy865s
//android key for maps api : AIzaSyAqsJXq-Ia-zbVqlhRIEfEMDFr7Egq-Is4

//example route: https://maps.googleapis.com/maps/api/directions/json?origin=Eskisehir&destination=Bursa&key=AIzaSyApwxIXVb9XGS9FGCid8olKQ0ST_Uy865s
//example video https://www.youtube.com/watch?v=CCZPUeY94MU
public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private static final String TAG = "HAKKE";
    private GoogleMap mMap;
    Context context;
    Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    String currentLatitude, currentLongitute;

    //IKI EYLUL latitude : 39.814618    , longitude : 30.536218
    double ikiEylulLatitude = 39.814618 , ikiEylulLongitude  = 30.533107;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    private void sendRequest() {
        String origin = currentLatitude+","+currentLongitute;
        Log.d(TAG,"my origin is : " + origin);
        String destination = ikiEylulLatitude+","+ikiEylulLongitude;
        Log.d(TAG,"my destination is : " + destination);

        try{
            new DirectionFinder(this, origin,destination).execute();
        }catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }






    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        currentLatitude = latitude + "";
        currentLongitute = longitude + "";


        LatLng currentLocation = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,14));

        addMarkerIkıEylul();

        //addPolylineCurrentToIkıEylul(currentLocation,addMarkerIkıEylul());

    }



    private LatLng addMarkerIkıEylul()
    {
        LatLng ikiEylulPosition = new LatLng(ikiEylulLatitude,ikiEylulLongitude);
        mMap.addMarker(
                new MarkerOptions()
                .title("İki Eylül Kampüsü")
                .snippet("Orda bi okul var uzakta, o okul bizim okulumuuzzdur..")
                .position(ikiEylulPosition)
        );

        return ikiEylulPosition;
    }//end method addMarkerIkıEylul


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }


    /* EXTRA METHODS */
    public void onSearch(View view) {
        String location = "location"; //et.getText().toString();
        List<Address> addressList = null;
        if (location != null || location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Address address = addressList.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

    }

    public void changeType(View view)
    {
        if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }
    public void onZoom(View view)
    {
        mMap.animateCamera(CameraUpdateFactory.zoomIn());


    }

    private void addPolylineCurrentToIkıEylul(LatLng currentLocation, LatLng ikiEylulLocation) {

        mMap
                .addPolyline((new PolylineOptions())
                        .add(currentLocation, ikiEylulLocation).width(3).color(Color.BLUE)
                        .geodesic(true));
    }
}
