package com.example.wineonadime;

public class Address
{
    //Class variables
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipcode;

    //Constructor
    public Address( String street, String city, String state, String country, String zipcode )
    {
        this.street  = street;
        this.city    = city;
        this.state   = state;
        this.country = country;
        this.zipcode = zipcode;
    }

    //Mutators
    public void setStreet( String street )
    {
        this.street = street;
    }

    public void setCity( String city )
    {
        this.city = city;
    }

    public void setState( String state )
    {
        this.state = state;
    }

    public void setCountry( String country )
    {
        this.country = country;
    }

    public void setZipcode( String zipcode )
    {
        this.zipcode = zipcode;
    }

    //Getters
    public String getStreet()
    {
        return this.street;
    }

    public String getCity()
    {
        return this.city;
    }

    public String getState()
    {
        return this.state;
    }

    public String getCountry()
    {
        return this.country;
    }

    public String getZipcode()
    {
        return this.zipcode;
    }
}
