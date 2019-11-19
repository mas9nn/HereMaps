package com.example.heremaps.Model;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class City_Info {
    LatLng first_point;
    LatLng second_point;
    LatLng center;
    String name;

    public City_Info(LatLng first_point, LatLng second_point, LatLng center, String name) {
        this.first_point = first_point;
        this.second_point = second_point;
        this.center = center;
        this.name = name;
    }

    public LatLng getFirst_point() {
        return first_point;
    }

    public void setFirst_point(LatLng first_point) {
        this.first_point = first_point;
    }

    public LatLng getSecond_point() {
        return second_point;
    }

    public void setSecond_point(LatLng second_point) {
        this.second_point = second_point;
    }

    public LatLng getCenter() {
        return center;
    }

    public void setCenter(LatLng center) {
        this.center = center;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
