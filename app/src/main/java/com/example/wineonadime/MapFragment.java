package com.example.wineonadime;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static java.sql.Types.NULL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener
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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().
                                                                findFragmentById(R.id.frame_map);
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
        int permission = ActivityCompat.checkSelfPermission(
                this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION );
        //If not, ask for it
        if( permission == PackageManager.PERMISSION_DENIED )
        {
            ActivityCompat.requestPermissions( this.getActivity(),
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );
        }
        //If permission granted, display marker at current location
        else
        {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                    this.getActivity(), task -> {
                Location mLastKnownLocation = task.getResult();
                if( task.isSuccessful() && mLastKnownLocation != null )
                {
                    LatLng mCurrentLatLng = new LatLng(  mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude() );
                    googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(mCurrentLatLng,
                            DEFAULT_ZOOM_LEVEL) );
                    googleMap.setMyLocationEnabled(true); // for the blue dot on map
                    googleMap.setOnInfoWindowClickListener(this);

                    //add markers from JSON file
                    ArrayList<Store> storeArrayList = readStoresFromJSON();
                    for( int i = 0; i < storeArrayList.size(); i++ )
                    {
                        LatLng storeLocation = new LatLng(  storeArrayList.get(i).getLatitude(),
                                storeArrayList.get(i).getLongitude() );
                        googleMap.addMarker( new MarkerOptions().position(storeLocation)
                                .title(storeArrayList.get(i).getName()) );
                    }
                }
            });
        }
    }

    @Override
    public void onInfoWindowClick( Marker marker )
    {
        //Load a fragment and set its args to the title of the store so we can call store in frag
        Bundle storeBundle = new Bundle();
        storeBundle.putString( "storename", marker.getTitle() );
        Fragment storeFragment = new StorePageFragment();
        storeFragment.setArguments( storeBundle );

        //Navigate to new fragment using FragmentManager and FragmentTransaction
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace( R.id.frame_map, storeFragment );
        fragmentTransaction.addToBackStack( null );
        fragmentTransaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults )
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

    public ArrayList<Store> readStoresFromJSON()
    {
        ArrayList<Store> arrayOfStores = new ArrayList<>();
        try
        {
            Log.i( "jsondata", "read stores called" );
            //Read JSON file
            JSONObject jsonObject = new JSONObject( loadJSONStoreFile() );
            //Get all stores
            JSONArray storeArray = jsonObject.getJSONArray( "stores" );
            
            for( int i = 0; i < storeArray.length(); i++ )
            {
                //Read each parameter for creating a new store
                JSONObject iStore = storeArray.getJSONObject( i ).getJSONObject( "store" );
                String storeName = iStore.getString( "name" );
                double latitude = iStore.getDouble( "latitude" );
                double longitude = iStore.getDouble( "longitude" );
                Store newStore = new Store( storeName, latitude, longitude, null, null );

                //add store to arraylist
                arrayOfStores.add( newStore );
            }
        }
        catch( JSONException e )
        {
            e.printStackTrace();
            Log.i( "jsondata", "read stores error" );
            return null;
        }

        return arrayOfStores;
    }

    public String loadJSONStoreFile()
    {
        String json;

        try
        {
            InputStream inputStream = getActivity().getAssets().open( "derulo.json" );
            int bufferSize = inputStream.available();
            byte[] buffer = new byte[bufferSize];
            inputStream.read( buffer );
            inputStream.close();
            json = new String( buffer, "UTF-8" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            return null;
        }

        return json;
    }
}
