package com.example.wineonadime;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Store
{
    //Class variables
    private String name;
    private double latitude;
    private double longitude;
    Address address;
    ArrayList<Wine> wines;

    //Constructor
    public Store( String name, double latitude, double longitude, Address address,
                       ArrayList<Wine> wines )
    {
        this.name      = name;
        this.latitude  = latitude;
        this.longitude = longitude;
        this.address   = address;
        this.wines     = wines;
    }

    //Mutators
    public void setName( String name )
    {
        this.name = name;
    }

    public void setLatitude( double latitude )
    {
        this.latitude = latitude;
    }

    public void setLongitude( double longitude )
    {
        this.longitude = longitude;
    }

    public void setAddress( Address address )
    {
        this.address = address;
    }

    public void setWines( ArrayList<Wine> wines )
    {
        this.wines= wines;
    }

    //Getters
    public String getName()
    {
        return this.name;
    }

    public double getLatitude()
    {
        return this.latitude;
    }

    public double getLongitude()
    {
        return this.longitude;
    }

    public Address getAddress()
    {
        return this.address;
    }

    public ArrayList<Wine> getWines()
    {
        return this.wines;
    }

    //Helpers
    public static Store buildStoreFromJSONData( JSONObject jsonObject )
    {
        Store newStore = null;

        try
        {
            //build new store
            String storeName = jsonObject.getString( "name" );
            double latitude = jsonObject.getDouble( "latitude" );
            double longitude = jsonObject.getDouble( "longitude" );

            JSONObject jsonAddress = jsonObject.getJSONObject( "address" );
            String street = jsonAddress.getString( "street" );
            String city = jsonAddress.getString( "city" );
            String state = jsonAddress.getString( "state" );
            String country = jsonAddress.getString( "country" );
            String zipcode = jsonAddress.getString ( "zipcode" );
            Address address = new Address( street, city, state, country, zipcode );

            JSONArray jsonWineArray = jsonObject.getJSONArray( "wines" );
            ArrayList<Wine> alWine = new ArrayList<>();

            //Build store's list of wines
            for( int j = 0; j < jsonWineArray.length(); j++ )
            {
                JSONObject jsonWine = jsonWineArray.getJSONObject(j);

                String wineName = jsonWine.getString( "name" );
                String wineType = jsonWine.getString( "type" );
                String wineBrand = jsonWine.getString( "brand" );
                String wineCountry = jsonWine.getString( "country" );
                double winePrice = jsonWine.getDouble( "price" );
                String wineYear = jsonWine.getString( "year" );

                Wine wine = new Wine( wineName, winePrice, wineType, wineBrand, wineYear,
                        wineCountry );

                alWine.add( wine );
            }

            //Create new store object
            newStore = new Store( storeName, latitude, longitude, address, alWine );
        }
        catch( JSONException e )
        {
            e.printStackTrace();
            Log.i( "jsondata", "read stores error" );
            return null;
        }

        return newStore;
    }
}
