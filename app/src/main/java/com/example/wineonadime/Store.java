package com.example.wineonadime;

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
}
