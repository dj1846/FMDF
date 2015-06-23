package com.picamerica.findmydrunkfriends.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ErRk on 6/9/2015.
 */
public class AppConstants {
    /**
     * Preferences Keys to store and
     * retrieve data from preferences.
     */
    public static final String USER = "USER";
    public static final String USER_ID = "USER_ID";
    public static final String APP_MODE = "APP_MODE";
    public static final String DISTANCE_UNIT = "D_UNIT";
    public static final String FRIST_DRINK = "FRIST_DRINK";

    /**
     * WebserviceHandler required Strings.
     */
    public static final String BASE_URL ="http://picamerica.com/";
    public static final String AUTH_USER ="pittsburgh";
    public static final String AUTH_PASS ="pirates";

    /**
     * Demo Friends List
     */
    
    public static List<FriendsModel> getDemoFriends(LatLng mCurrentLocation) {
        List<FriendsModel> demoFriends = new ArrayList<>();
        String title = "Ryan Smith";
        LatLng demoLatLng = new LatLng(mCurrentLocation.latitude - .0055,mCurrentLocation.longitude + .0015);
//        String distance = ".9 miles SSE of you, 16 minutes ago";
        String user_id = "rsmith";
        String cardinal = "SSE of you,";
        String distance =".9 miles";
        int bearing = 165;
        demoFriends.add(new FriendsModel(user_id,title,cardinal,"16 minutes ago",distance,bearing,demoLatLng));
        title = "Candace Johnson";
        demoLatLng = new LatLng(mCurrentLocation.latitude - .002,mCurrentLocation.longitude + .003);
        user_id = "ccj";
        cardinal = "SE of you,";
        distance =".3 miles";
        bearing = 125;
//        distance = ".3 miles SE of you, 5 minutes ago";
        demoFriends.add(new FriendsModel(user_id, title, cardinal, "5 minutes ago", distance, bearing, demoLatLng));

        title = "Kevin Newport";
        demoLatLng = new LatLng(mCurrentLocation.latitude + .001,mCurrentLocation.longitude - .007);
        user_id = "newport1";
        cardinal = "NW of you,";
        distance ="1.7 miles";
        bearing = 280;
//        distance = "1.7 miles NW of you, 2 minutes ago";
        demoFriends.add(new FriendsModel(user_id, title, cardinal, "2 minutes ago", distance, bearing, demoLatLng));


        return demoFriends;
    }
}
