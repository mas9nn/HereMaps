package com.example.heremaps.Model;

public class Coordinates {
    double lon;
    double lat;
    String name;
    String address;

    public Coordinates(double lon, double lat, String name,String address) {
        this.lon = lon;
        this.lat = lat;
        this.name = name;
        this.address = address;
    }

    public Coordinates() {
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
