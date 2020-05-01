package com.example.wineonadime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends FragmentActivity {

    //TODO delete? private final LatLng mDestinationLatLng = new LatLng(  43.075385, -89.404179 );
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient; // Save the instance

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frame_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            displayMyLocation( googleMap );
        });

        //Obtain a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        onSearchRequested();
    }

    private void displayMyLocation( GoogleMap googleMap )
    {
        // Check if permission granted
        int permission = ActivityCompat.checkSelfPermission( this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION );
        //If not, ask for it
        if( permission == PackageManager.PERMISSION_DENIED )
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );
        }
        //If permission granted, display marker at current location
        else
        {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener( this, task -> {
                Location mLastKnownLocation = task.getResult();
                if( task.isSuccessful() && mLastKnownLocation != null )
                {
                    //Add marker
                    LatLng mCurrentLatLng = new LatLng(  mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude() );
                    googleMap.addMarker( new MarkerOptions().position(mCurrentLatLng).title("Current Location") );
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults )
    {
        if( requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION )
        {
            // If request cancelled, the result arrays are empty
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                displayMyLocation( mMap );
            }
        }
    }
}
