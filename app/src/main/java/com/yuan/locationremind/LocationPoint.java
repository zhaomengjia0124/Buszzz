package com.yuan.locationremind;

import java.io.Serializable;

/**
 * Created by Yuan on 19/10/2016:6:09 PM.
 * <p/>
 * Description:com.yuan.locationremind.LatLng
 */
public class LocationPoint implements Serializable {

    private double longitude;
    private double latitude;

    public LocationPoint(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
