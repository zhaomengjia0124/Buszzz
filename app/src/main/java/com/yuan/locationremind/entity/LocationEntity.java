package com.yuan.locationremind.entity;

import java.io.Serializable;

/**
 * Created by Yuan on 20/10/2016:3:35 PM.
 * <p/>
 * Description:com.yuan.locationremind.entity.LocationEntity
 */

public class LocationEntity implements Serializable {

    private String address;
    private double latitude;
    private double longitude;
    private String addressId;
    private String isSelected;//是否使用中

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

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return "LocationEntity{" +
                "address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", addressId='" + addressId + '\'' +
                ", isSelected='" + isSelected + '\'' +
                '}';
    }
}
