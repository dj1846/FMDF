package com.picamerica.findmydrunkfriends.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ErRk on 6/9/2015.
 */
public class FriendsModel {

    private long id = 0;
    private String userId;
    private String userName;
    private String cardinal;
    private String lastTimeStamp;
    private long bearing;
    private String distance;
    private LatLng latLng;
    private boolean locationShared = true;

    public FriendsModel(String userId, String userName, String cardinal, String lastTimeStamp,String distance, long bearing, LatLng latLng) {
        this.userId = userId;
        this.userName = userName;
        this.cardinal = cardinal;
        this.lastTimeStamp = lastTimeStamp;
        this.bearing = bearing;
        this.latLng = latLng;
        this.distance = distance;
    }

    public FriendsModel(String id) {
        this.userId = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCardinal() {
        return cardinal;
    }

    public void setCardinal(String cardinal) {
        this.cardinal = cardinal;
    }

    public String getLastTimeStamp() {
        return lastTimeStamp;
    }

    public void setLastTimeStamp(String lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    public long getBearing() {
        return bearing;
    }

    public void setBearing(long bearing) {
        this.bearing = bearing;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public boolean isLocationShared() {
        return locationShared;
    }

    public void setLocationShared(boolean locationShared) {
        this.locationShared = locationShared;
    }
}
