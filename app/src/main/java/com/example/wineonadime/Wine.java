package com.example.wineonadime;

public class Wine
{
    //Class variables
    private String name;
    private Double price;
    private String type;
    private String brand;
    private String year;
    private String country;

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

    public void setPrice( Double price )
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

    //Getters
    public String getName()
    {
        return this.name;
    }

    public Double getPrice()
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
}
