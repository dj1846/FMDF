package picamerica.com.findmydrunkfriends;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;

import picamerica.com.findmydrunkfriends.utils.Utills;

public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener  {

    protected static final String TAG = MainActivity.class.getCanonicalName();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates = false;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime = "";



    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button btnCreate;
    private RadioGroup mapSwitch;
    private RadioGroup appModeSwitch;
    private RadioGroup distanceSwitch;
    private RadioGroup friendSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        btnCreate      = (Button) findViewById(R.id.btn_create);
        mapSwitch      = (RadioGroup) findViewById(R.id.map_switch);
        appModeSwitch  = (RadioGroup) findViewById(R.id.app_mode);
        distanceSwitch = (RadioGroup) findViewById(R.id.distance_switch);
        friendSwitch   = (RadioGroup) findViewById(R.id.friends_switch);
        mapSwitch.setOnCheckedChangeListener(switchChangedListener);
        appModeSwitch.setOnCheckedChangeListener(switchChangedListener);
        distanceSwitch.setOnCheckedChangeListener(switchChangedListener);
        friendSwitch.setOnCheckedChangeListener(switchChangedListener);
        btnCreate.setOnClickListener(createListener);
        buildGoogleApiClient();
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        String title = "Ryan Smith";
        LatLng demolat = new LatLng(mCurrentLocation.getLatitude() - .0055,mCurrentLocation.getLongitude() + .0015);
        String distance = ".9 miles SSE of you, 16 minutes ago";
//      user id = “rsmith”;
//      bearing = 165;
        mMap.addMarker(new MarkerOptions().position(demolat).title(title).snippet(distance));

        title = "Candace Johnson";
        demolat = new LatLng(mCurrentLocation.getLatitude() - .002,mCurrentLocation.getLongitude() + .003);
//      user id = “ccj”;
//      bearing = 125;
        distance = ".3 miles SE of you, 5 minutes ago";
        mMap.addMarker(new MarkerOptions().position(demolat).title(title).snippet(distance));

        title = "Kevin Newport";
        demolat = new LatLng(mCurrentLocation.getLatitude() + .001,mCurrentLocation.getLongitude() - .007);

//      user id = “newport1”;
//      bearing = 280;
        distance = "1.7 miles NW of you, 2 minutes ago";
        mMap.addMarker(new MarkerOptions().position(demolat).title(title).snippet(distance));

    }



    private View.OnClickListener createListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            findViewById(R.id.create_form).setVisibility(View.GONE);
            AlertDialog.Builder dialog = Utills.getDialog(MainActivity.this, getString(R.string.local135), getString(R.string.local349));
            dialog.setPositiveButton(R.string.local136,continueAfterCreate);
            dialog.setCancelable(false);
            dialog.show();
        }
    };


    private DialogInterface.OnClickListener continueAfterCreate = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            AlertDialog.Builder dialog = Utills.getDialog(MainActivity.this, getString(R.string.local135), getString(R.string.local371));
            dialog.setPositiveButton(R.string.local136,continueDemoMode);
            dialog.setCancelable(false);
            dialog.show();
        }
    };

    private DialogInterface.OnClickListener continueDemoMode = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    };


    private RadioGroup.OnCheckedChangeListener switchChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (radioGroup.getId()){
                case R.id.friends_switch :
                    if(checkedId==R.id.btn_friends){
                        startActivity(new Intent(MainActivity.this,FriendsActivity.class));
                    }else if(checkedId == R.id.btn_photo){

                    }else if(checkedId == R.id.btn_share){

                    }
                    break;
                case R.id.map_switch :
                    if (mMap != null) {
                        if(checkedId==R.id.btn_map){
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                            mMap.invalidate();
                        }else if(checkedId == R.id.btn_sat){
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        }
                    }
                    break;
                case R.id.app_mode :
                    if(checkedId==R.id.btn_demo){

                    }else if(checkedId == R.id.btn_real){

                    }
                    break;
                case R.id.distance_switch :
                    if(checkedId==R.id.btn_km){

                    }else if(checkedId == R.id.btn_miles){

                    }
                    break;

            }
        }
    };

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        Utills.checkNetwork(MainActivity.this);

        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {
        if (mCurrentLocation != null && mMap!=null) {
            LatLng coordinate = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 15);
            mMap.animateCamera(yourLocation);
            mMap.clear();
            setUpMap();
        }
    }


    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }
}
