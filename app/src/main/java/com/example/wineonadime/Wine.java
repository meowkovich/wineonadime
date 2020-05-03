package com.example.wineonadime;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Wine
{
    //Class variables
    private String name;
    private Double price;
    private String type;
    private String brand;
    private String year;
    private String country;
    private String store;

    //Constructor
    public Wine(String name, Double price, String type, String brand, String year,
                String country )
    {
        this.name    = name;
        this.price   = price;
        this.type    = type;
        this.brand   = brand;
        this.year    = year;
        this.country = country;
    }

    //Mutators
    public void setName( String name )
    {
        this.name = name;
    }

    public void setPrice(Double price )
    {
        this.price = price;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public void setBrand( String brand )
    {
        this.brand = brand;
    }

    public void setYear( String year )
    {
        this.year = year;
    }

    public void setCountry( String country )
    {
        this.country = country;
    }

    public void setStore(String store) {this.store = store;}


    //Getters
    public String getName()
    {
        return this.name;
    }
  
    public double getPrice()
    {
        return this.price;
    }

    public String getType()
    {
        return this.type;
    }

    public String getBrand()
    {
        return this.brand;
    }

    public String getYear()
    {
        return this.year;
    }

    public String getCountry()
    {
        return this.country;
    }
  
    public String getStore() {return this.store;}

    //Helpers
    public static Wine buildWineFromJSONData( JSONObject jsonObject )
    {
        Wine wine = null;

        try
        {
            String wineName = jsonObject.getString("name");
            String wineType = jsonObject.getString("type");
            String wineBrand = jsonObject.getString("brand");
            String wineCountry = jsonObject.getString("country");
            Double winePrice = jsonObject.getDouble("price");
            String wineYear = jsonObject.getString("year");

            wine = new Wine(wineName, winePrice, wineType, wineBrand, wineYear,
                    wineCountry);
        }
        catch( JSONException e )
        {
            e.printStackTrace();
            Log.i( "jsondata", "read stores error" );
            return null;
        }

        return wine;
    }
}
