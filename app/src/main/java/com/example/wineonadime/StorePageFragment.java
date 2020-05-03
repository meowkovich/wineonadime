package com.example.wineonadime;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static androidx.core.content.ContextCompat.getColor;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StorePageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StorePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StorePageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_STORE_NAME = "storename";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String storeName;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Store store;

    // Map variables
    private final float DEFAULT_ZOOM_LEVEL = 14.0f;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient; // Save the instance
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;

    public StorePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StorePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StorePageFragment newInstance(String param1, String param2) {
        StorePageFragment fragment = new StorePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STORE_NAME, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            storeName = getArguments().getString(ARG_STORE_NAME);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_page, container, false);

        // Set onclick listener for button on store page
        Button button = view.findViewById( R.id.get_directions );
        button.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                storePageButtonClick( view );
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated( Bundle savedInstance )
    {
        super.onActivityCreated( savedInstance );

        TextView tvStoreName = getView().findViewById( R.id.storeName );
        TextView tvStoreAddress = getView().findViewById( R.id.storeAddress );

        this.store = createStoreFromJSON();

        if( store != null )
        {
            tvStoreName.setText( store.getName() );
            tvStoreAddress.setText( store.getAddress().getFormattedAddress() );
        }

        //Display current location of user and store on map
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this.getActivity() );
        SupportMapFragment mapFragment = (ScrollViewFixedMapFragment) getChildFragmentManager().
                findFragmentById(R.id.frame_small_map);
        mapFragment.getMapAsync(googleMap ->
        {
            mMap = googleMap;

            ScrollView scrollView = getActivity().findViewById( R.id.store_scroll_view );
            ((ScrollViewFixedMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.frame_small_map))
                    .setListener(new ScrollViewFixedMapFragment.OnTouchListener() {
                        @Override
                        public void onTouch()
                        {
                            scrollView.requestDisallowInterceptTouchEvent(true);
                        }
                    });

            //Calculate padding needed for location in map
            int padding = mapFragment.getView().getWidth();
            padding = ( padding / 10 );
            //set up location to display
            setLocationUserStoreInMap( googleMap, store, padding );
        });

        //Add the wines in the store to the store page
        addWinesToStorePage( store.getWines() );
    }

    public void setLocationUserStoreInMap( GoogleMap googleMap, Store store, int padding )
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
                            //Add user location to map
                            LatLng mCurrentLatLng = new LatLng(  mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude() );
                            googleMap.setMyLocationEnabled(true); // for the blue dot on map

                            //Add store marker to map
                            LatLng storeLocation = new LatLng(  store.getLatitude(),
                                    store.getLongitude() );
                            googleMap.addMarker( new MarkerOptions().position(storeLocation)
                                    .title( store.getName()) );

                            //Move camera with appropriate zoom level
                            LatLngBounds centerStoreUser;
                            centerStoreUser = LatLngBounds.builder().include(mCurrentLatLng).
                                    include(storeLocation).build();

                            //Set up initial location on user's position
                            googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(
                                    mCurrentLatLng, DEFAULT_ZOOM_LEVEL) );

                            //animate camera to move to include both locations on map
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(
                                    centerStoreUser, padding );
                            googleMap.animateCamera( cameraUpdate );
                        }
                    });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
        //not needed
//        else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public Store createStoreFromJSON()
    {
        Store newStore = null;

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

                if( this.storeName.equalsIgnoreCase(storeName) )
                {
                    //Create new store object
                    newStore = Store.buildStoreFromJSONData( iStore );

                    break; //break out of for loop
                }
            }
        }
        catch( JSONException e )
        {
            e.printStackTrace();
            Log.i( "jsondata", "read stores error" );
            return null;
        }

        return newStore;
    }

    private void addWinesToStorePage( ArrayList<Wine> wines )
    {
        LinearLayout linearLayout = ( LinearLayout ) getActivity().
                                    findViewById( R.id.storeLinearLayout );

        for( int i = 0; i < wines.size(); i++ )
        {
            //dynamically add entries based on layout for each wine
            View wineEntry = getLayoutInflater().inflate( R.layout.store_wine_entry, null );

            TextView wineName = wineEntry.findViewById( R.id.entry_wine_name );
            TextView winePrice = wineEntry.findViewById( R.id.entry_wine_price );
            TextView wineYear = wineEntry.findViewById( R.id.entry_wine_year );
            TextView wineBrand = wineEntry.findViewById( R.id.entry_wine_brand );

            //Set textviews
            wineName.setText( wines.get(i).getName() );
            wineYear.setText( wines.get(i).getYear() );
            wineBrand.setText( wines.get(i).getBrand() );

            //set price to display correctly to two decimals
            //add an extra 0 if no extra cents in price
            double tempPrice = wines.get(i).getPrice() * 10;
            int isPriceEvenCents = (int) tempPrice;
            if( (isPriceEvenCents % 10) == 0 )
            {
                winePrice.setText("$" + wines.get(i).getPrice() + "0" );
            }
            else
            {
                winePrice.setText("$" + wines.get(i).getPrice() );
            }

            //finally add to view
            linearLayout.addView( wineEntry );
        }

        //Add some extra filler at the bottom so bottom navigation menu doesn't obscure last wine
        TextView emptyTV = new TextView( getActivity() );
        emptyTV.setText( "" );
        emptyTV.setPadding( 10, 100, 10, 100 );
        linearLayout.addView( emptyTV );
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

    public void storePageButtonClick( View view )
    {
        String unencodedUri = "https://www.google.com/maps/dir/?api=1&destination=";
        unencodedUri = unencodedUri + store.getAddress().getStreet() + store.getAddress().getCity()
                + ", " + store.getAddress().getState() + " " + store.getAddress().getZipcode();

        Uri uri = Uri.parse( unencodedUri );
        Intent intent = new Intent( Intent.ACTION_VIEW, uri );
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }
}
