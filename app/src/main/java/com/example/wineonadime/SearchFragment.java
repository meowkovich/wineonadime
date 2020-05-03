package com.example.wineonadime;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private static ArrayList<Wine> displayWines = new ArrayList<Wine>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    // SearchMapAdapter class variables
    private static final double METERS_TO_MILES_RATIO = 1609.344;
    private static final String DISTANCE_ERROR_MSG = "ERROR finding distance";

    private ArrayList<Store> storeArrayList;
    private ArrayList<Wine> wineArrayList;
    private Activity activity;
    private LatLng currLocation = new LatLng(43.0731, 89.4012);

    private SearchMapAdapter.ItemClickListener clickListener;

    // Used later to calculate distance to store
    private FusedLocationProviderClient mFusedLocationProviderClient; // Save the instance
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(ArrayList<Wine> paramDisplay) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
//        ActionBar actionBar = getActivity().getActionBar();
//        actionBar.show();
//        actionBar.setTitle("Filter Search");
        updateCurrLocation();
        storeArrayList = new ArrayList<Store>();
        storeArrayList.addAll(readStoresFromJSON());
        Log.w(TAG, "Store Array Size: " + storeArrayList.size());
        for(int i = 0; i < storeArrayList.size(); ++i) {
            if(true) {
                Log.w(TAG, "Store Wines Size: " + storeArrayList.get(i).wines.size());
                displayWines.addAll(storeArrayList.get(i).getWines());
            }
            Log.w(TAG, "display Array Size: " + displayWines.size());
        }
        super.onCreate(savedInstanceState);


    }

//    public boolean onCreateOptions(Menu menu) {
//        ActionBar actionBar = getActivity().getActionBar();
//        actionBar.show();
//        actionBar.setTitle("Filter Search");
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        displayWines = new ArrayList<Wine>();
//        displayWines.add(new Wine("wine1", 19.99, "red", "brand1", "2014", "United States"));
//        displayWines.add(new Wine("wine2", 12.99, "red", "brand2", "2013", "United States"));
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.searchRecycle);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(new WineAdapter(displayWines));

        return view;
    }

    public void updateCurrLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        // Check if permission granted
        int permission = ActivityCompat.checkSelfPermission(
                getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        //If not, ask for it
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        //If permission granted, display marker at current location
        else {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                    getActivity(), task -> {
                Location mLastKnownLocation = task.getResult();
                if (task.isSuccessful() && mLastKnownLocation != null) {
                    LatLng mCurrentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    setLocation(mCurrentLatLng);
                }
            });
        }
    }

    public double calculateDistanceToStore( Store store )
    {
        //update current location
        updateCurrLocation();

        double distance = -1.0;


        if( currLocation != null )
        {
            float[] results = new float[1];
            Location.distanceBetween(currLocation.latitude, currLocation.longitude,
                    store.getLatitude(), store.getLongitude(), results);
            distance = (double) results[0];
        }

        //Convert the distance (returned in meters) to miles
        if( distance != -1.0 )
        {
            distance = distance / METERS_TO_MILES_RATIO;
            //round to one decimal place
            distance = distance * 10;
            int round = (int) distance;
            distance = ((double) round) / 10;
        }

        return distance;
    }

    public ArrayList<Wine> getWines(Store store) {
        return store.getWines();
    }


    public ArrayList<Store> readStoresFromJSON()
    {
        ArrayList<Store> arrayOfStores = new ArrayList<Store>();
        ArrayList<Wine> arrayOfWines;
        try
        {
            Log.i( "jsondata", "read stores called" );
            //Read JSON file
            JSONObject jsonObject = new JSONObject( loadJSONStoreFile() );
            //Get all stores
            JSONArray storeArray = jsonObject.getJSONArray( "stores" );

            for( int i = 0; i < storeArray.length(); i++ )
            {
                arrayOfWines = new ArrayList<Wine>();
                //Read each parameter for creating a new store
                JSONObject iStore = storeArray.getJSONObject( i ).getJSONObject( "store" );
                String storeName = iStore.getString( "name" );
                double latitude = iStore.getDouble( "latitude" );
                double longitude = iStore.getDouble( "longitude" );
                JSONArray iWine = iStore.getJSONArray("wines");
                for (int j = 0; j < iWine.length(); ++j) {
                    Wine wineH = new Wine(iWine.getJSONObject(j).getString("name"),
                            iWine.getJSONObject(j).getDouble("price"), iWine.getJSONObject(j).getString("type"),
                            iWine.getJSONObject(j).getString("brand"), iWine.getJSONObject(j).getString("year"),
                            iWine.getJSONObject(j).getString("country"));
                    wineH.setStore(storeName);
                    arrayOfWines.add(wineH);
                }
                Store newStore = new Store( storeName, latitude, longitude, null, arrayOfWines );

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
        public void setLocation(LatLng location) {
        currLocation = location;
    }

        public LatLng getLocation() {
        return currLocation;
    }
}
