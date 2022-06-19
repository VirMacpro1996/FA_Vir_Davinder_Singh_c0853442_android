package com.example.finalassignmentandroid;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MarkerModel  {
    private int id;


    private String city , address ;
    private Double lat , lng ;
    private int isVisited;

    public MarkerModel(int id, String city ,String address , Double lat, Double lng, int isVisited) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.city = city ;
        this.address = address ;
        this.isVisited = isVisited;

    }

    public int getId() {
        return id;
    }

    public Double getLat() {
        return lat;
    }


    public Double getLng() {
        return lng;
    }
    public String geCity() {
        return city;
    }
    public String getAddress()
    {
        return address;
    }

    public int isVisited() {
        return isVisited;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setIsVisited(int isVisited) {
        this.isVisited = isVisited;
    }

    @Override
    public String toString() {
        return "MarkerModel{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", isVisited=" + isVisited +
                '}';
    }
}
