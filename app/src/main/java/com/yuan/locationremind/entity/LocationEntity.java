package com.yuan.locationremind.entity;

import java.io.Serializable;

/**
 * Created by Yuan on 20/10/2016:3:35 PM.
 * <p/>
 * Description:com.yuan.locationremind.entity.LocationEntity
 */

public class LocationEntity implements Serializable {
    private int id;
    private String address;
    private double latitude;
    private double longitude;
    private int selected;//是否使用中
    private float radius;//半径
    private int interval;//刷新间隔

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }


    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "LocationEntity{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", selected=" + selected +
                ", radius=" + radius +
                ", interval=" + interval +
                '}';
    }
}

