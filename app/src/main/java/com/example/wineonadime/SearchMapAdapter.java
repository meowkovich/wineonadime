package com.example.wineonadime;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

public class SearchMapAdapter extends RecyclerView.Adapter<SearchMapAdapter.SearchMapViewHolder>
{
    // SearchMapAdapter class variables
    private static final double METERS_TO_MILES_RATIO = 1609.344;
    private static final String DISTANCE_ERROR_MSG = "ERROR finding distance";

    private ArrayList<Store> storeArrayList;
    private ArrayList<Wine> wineArrayList;
    private Activity activity;
    private LatLng currLocation;

    private ItemClickListener clickListener;

    // Used later to calculate distance to store
    private FusedLocationProviderClient mFusedLocationProviderClient; // Save the instance
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class SearchMapViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener
    {
        // each data item is just a string in this case
        public TextView tvStoreName;
        public TextView tvStoreCity;
        public TextView tvStoreDist;
        public TextView tvAuxLine;
        public ImageView ivIcon;
        public String realStoreName;

        public SearchMapViewHolder(View view)
        {
            super(view);
            tvStoreName = view.findViewById( R.id.store_name );
            tvStoreCity = view.findViewById( R.id.store_city );
            tvStoreDist = view.findViewById( R.id.store_dist );
            tvAuxLine   = view.findViewById( R.id.store_wine_num ); //use depends on item type
            ivIcon = view.findViewById( R.id.entry_icon );
            view.setOnClickListener( this );
        }

        @Override
        public void onClick( View view )
        {
            if( clickListener != null )
            {
                clickListener.onItemClick( view, getAdapterPosition() );
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchMapAdapter(ArrayList<Store> storeResults, ArrayList<Wine> wineResults,
                            Activity activity)
    {
        this.storeArrayList = storeResults;
        this.wineArrayList = wineResults;
        this.activity = activity;

        //update current location
        updateCurrLocation();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SearchMapAdapter.SearchMapViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType)
    {
        // create a new view
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_map_search_entry, parent, false);

        SearchMapViewHolder vh = new SearchMapViewHolder(linearLayout);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    // note that since we a treating two arrays as one, position may be
    // offset to access second array properly
    @Override
    public void onBindViewHolder(SearchMapViewHolder holder, int position)
    {
        //In this method we are treating storeArrayList and wineArrayList as one combined array
        //Access the first "part" of the array (ie storeArrayList is the first "part")
        if( position < storeArrayList.size() )
        {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.tvStoreName.setText(storeArrayList.get(position).getName());
            holder.realStoreName = storeArrayList.get(position).getName();
            holder.tvStoreCity.setText(storeArrayList.get(position).getAddress().getCity() + ", " +
                                        storeArrayList.get(position).getAddress().getState());
            holder.tvAuxLine.setText(storeArrayList.get(position).getWines().size() +
                                        " wines available");
            holder.ivIcon.setImageResource( R.drawable.ic_store_red_24dp ); //set to store icon

            //set distance away field
            double tempDist = calculateDistanceToStore(storeArrayList.get(position));
            if (tempDist != -1.0)
            {
                holder.tvStoreDist.setText("" + tempDist + " miles away");
            }
            else
            {
                holder.tvStoreDist.setText( DISTANCE_ERROR_MSG );
            }
        }
        //else go to second "part" of array (into wineArrayList)
        else
        {
            //get wine and set text as needed
            int relativePosition = position - storeArrayList.size();
            Wine currWine = wineArrayList.get(relativePosition);

            Store storeWithCurrentWine = findClosestStoreWithWine( currWine );
            if( storeWithCurrentWine != null )
            {
                holder.realStoreName = storeWithCurrentWine.getName();
            }
            else
            {
                holder.realStoreName = null;
            }

            holder.tvStoreName.setText( currWine.getName() );
            holder.tvStoreCity.setText(storeWithCurrentWine.getAddress().getCity() + ", " +
                    storeWithCurrentWine.getAddress().getState());
            holder.ivIcon.setImageResource( R.drawable.ic_drink_red_24dp );

            //set distance away field
            if( storeWithCurrentWine != null ) {
                double tempDist = calculateDistanceToStore(storeWithCurrentWine);
                if (tempDist != -1.0)
                {
                    holder.tvStoreDist.setText("" + tempDist + " miles away");
                }
                else
                {
                    holder.tvStoreDist.setText( DISTANCE_ERROR_MSG );
                }
            }
            else
            {
                holder.tvStoreDist.setText( DISTANCE_ERROR_MSG );
            }

            //set price to display on screen
            //add an extra 0 if no extra cents in price
            double tempPrice = currWine.getPrice() * 10;
            int isPriceEvenCents = (int) tempPrice;
            if( (isPriceEvenCents % 10) == 0 )
            {
                holder.tvAuxLine.setText("$" + currWine.getPrice() + "0" );
            }
            else
            {
                holder.tvAuxLine.setText("$" + currWine.getPrice() );
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return storeArrayList.size() + wineArrayList.size();
    }

    public Store findClosestStoreWithWine( Wine wine )
    {
        Store closestStore = null;
        double distanceToStore = -1.0;

        try
        {
            //Load data from json file into json object
            JSONObject jsonData = new JSONObject(loadJSONStoreFile());
            //Get all stores
            JSONArray storeArray = jsonData.getJSONArray( "stores" );

            //Search thru all stores to find where wine is located
            for( int i = 0; i < storeArray.length(); i++ )
            {
                //Read each parameter for creating a new store
                JSONObject iStore = storeArray.getJSONObject( i ).getJSONObject( "store" );
                JSONArray iStoreWines = iStore.getJSONArray( "wines" );
                String storeName = iStore.getString( "name" );

                //Iterate thru all wines in the store to find store with wine
                for( int j = 0; j < iStoreWines.length(); j++ )
                {
                    String jWineName = iStoreWines.getJSONObject(j).getString( "name" );
                    if( jWineName.equalsIgnoreCase( wine.getName() ) )
                    {
                        //calculate distance to store with matching wine, build store if closest
                        double tempDist = ( calculateDistanceToStore(
                                iStore.getDouble( "latitude"),
                                iStore.getDouble( "longitude" ) ) );

                        //If distance has not been set or distance is smaller
                        if( (distanceToStore == -1) || (tempDist < distanceToStore) )
                        {
                            closestStore = Store.buildStoreFromJSONData( iStore );
                        }
                    }
                }
            }
        }
        catch( JSONException e )
        {
            e.printStackTrace();
            Log.i( "jsondata", "read stores error" );
            return null;
        }

        return closestStore;
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

    public double calculateDistanceToStore( double latitude, double longitude )
    {
        //update current location
        updateCurrLocation();

        double distance = -1.0;


        if( currLocation != null )
        {
            float[] results = new float[1];
            Location.distanceBetween(currLocation.latitude, currLocation.longitude,
                    latitude, longitude, results);
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

    public void updateCurrLocation()
    {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( activity );
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

        // Check if permission granted
        int permission = ActivityCompat.checkSelfPermission(
                activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION );
        //If not, ask for it
        if( permission == PackageManager.PERMISSION_DENIED )
        {
            ActivityCompat.requestPermissions( activity,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );
        }
        //If permission granted, display marker at current location
        else
        {
            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            this.currLocation = new LatLng( location.getLatitude(), location.getLongitude() );
        }
    }

    //let clicks be caught
    public void setClickListener(ItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }

    //get items for click
    public String getItem( int id )
    {
        if( id < storeArrayList.size() )
        {
            //return the name of the store
            return storeArrayList.get( id ).getName();
        }
        else
        {
            //return the name of the closest store with the wine
            int relativePosition = id - storeArrayList.size();
            return findClosestStoreWithWine( wineArrayList.get( relativePosition ) ).getName();
        }
    }

    // can be implemented by parent activity for responding to click events
    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public String loadJSONStoreFile()
    {
        String json;

        try
        {
            InputStream inputStream = activity.getAssets().open( "derulo.json" );
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