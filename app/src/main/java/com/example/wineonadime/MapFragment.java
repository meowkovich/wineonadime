package com.example.wineonadime;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SearchListener searchListener;
    FloatingActionButton fabSearch;

    // Map variables
    private final float DEFAULT_ZOOM_LEVEL = 14.0f;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient; // Save the instance

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach( Context context )
    {
        super.onAttach( context );

        try
        {
            searchListener = (SearchListener) context;
        }
        catch( ClassCastException castException )
        {
            // the activity has not implemented search listener...
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );

        // Set up onclick for floating search button
        // must be done here to avoid null pointer exception thrown from findViewById, since
        // getView() does not work until after onCreateView
        fabSearch = getView().findViewById( R.id.floatingSearchButton );
        fabSearch.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchListener.openSearch( v );
            }
        });

        //Display current location on map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frame_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            displayMyLocation( googleMap );
        });

        //Obtain a FusedLocationProviderClient
        //TODO: make sure this.getActivity() actually works? Can't pass fragment
        // (it seems to work)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this.getActivity() );
    }

    private void displayMyLocation( GoogleMap googleMap )
    {
        // Check if permission granted
        int permission = ActivityCompat.checkSelfPermission( this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION );
        //If not, ask for it
        if( permission == PackageManager.PERMISSION_DENIED )
        {
            ActivityCompat.requestPermissions( this.getActivity(),
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );
        }
        //If permission granted, display marker at current location
        else
        {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener( this.getActivity(), task -> {
                Location mLastKnownLocation = task.getResult();
                if( task.isSuccessful() && mLastKnownLocation != null )
                {
                    LatLng mCurrentLatLng = new LatLng(  mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude() );
//                    googleMap.addMarker( new MarkerOptions().position(mCurrentLatLng).title("Current Location") );
                    googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, DEFAULT_ZOOM_LEVEL) );
                    googleMap.setMyLocationEnabled(true); // for the blue dot on map
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
