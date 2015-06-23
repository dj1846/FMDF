package com.picamerica.findmydrunkfriends;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.picamerica.findmydrunkfriends.utils.AppConstants;
import com.picamerica.findmydrunkfriends.utils.FriendsModel;
import com.picamerica.findmydrunkfriends.utils.Preferences;
import com.picamerica.findmydrunkfriends.utils.Utills;
import com.picamerica.findmydrunkfriends.webservices.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

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
    public final static String LOCATION_KEY = "location-key";
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
    protected String mUserID = null;
    protected int mDrinkCount = 0;
    protected int mCurrentFriend = 0;


    private List<FriendsModel> friendsList = new ArrayList<>();
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button btnCreate;
    private EditText edtCreate;
    private TextView txtName;
    private TextView txtFriendName;
    private TextView txtFrDistance;
    private TextView txtDrinkTelly;
    private RadioGroup mapSwitch;
    private RadioGroup appModeSwitch;
    private RadioGroup distanceSwitch;
    private View btnFriend;
    private View btnPhoto;
    private View btnShare;
    private View topMenu;
    private View createForm;
    private View normalView;
    private View compassView;
    private View bottalView;
    private Preferences preferences;

    private ProgressDialog progressDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = Preferences.getInstance(this);
        setUpMapIfNeeded();
        buildGoogleApiClient();

        topMenu = findViewById(R.id.top_menu);
        normalView = findViewById(R.id.normal_view);
        createForm = findViewById(R.id.create_form);
        mUserID = preferences.getString(AppConstants.USER_ID);
        if(mUserID==null){
            setupFirstUse();
        }else{
            setUpNormalUse();
        }
    }

    private void setupFirstUse(){
        try{
            topMenu.setVisibility(View.GONE);
            normalView.setVisibility(View.GONE);
            createForm.setVisibility(View.VISIBLE);
            edtCreate = (EditText) findViewById(R.id.edt_create);
            btnCreate = (Button) findViewById(R.id.btn_create);
            btnCreate.setOnClickListener(createListener);
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    private void setUpNormalUse(){
        try{
            mUserID = preferences.getString(AppConstants.USER_ID);
            topMenu.setVisibility(View.VISIBLE);
            normalView.setVisibility(View.VISIBLE);
            createForm.setVisibility(View.GONE);

            btnFriend    = findViewById(R.id.btn_friends);
            btnPhoto     = findViewById(R.id.btn_photo);
            btnShare     = findViewById(R.id.btn_share);
            compassView  = findViewById(R.id.compass_icon);
            bottalView   = findViewById(R.id.empty_bottel);

            txtName        = (TextView) findViewById(R.id.text_name);
            txtFriendName  = (TextView) findViewById(R.id.txt_friend_name);
            txtFrDistance  = (TextView) findViewById(R.id.txt_fr_distance);
            txtDrinkTelly  = (TextView) findViewById(R.id.txt_drink);
            mapSwitch      = (RadioGroup) findViewById(R.id.map_switch);
            appModeSwitch  = (RadioGroup) findViewById(R.id.app_mode);
            distanceSwitch = (RadioGroup) findViewById(R.id.distance_switch);
            txtName.setText(mUserID);
            mapSwitch.setOnCheckedChangeListener(switchChangedListener);
            appModeSwitch.setOnCheckedChangeListener(switchChangedListener);
            distanceSwitch.setOnCheckedChangeListener(switchChangedListener);
            btnFriend.setOnClickListener(topLinkListener);
            btnPhoto.setOnClickListener(topLinkListener);
            btnShare.setOnClickListener(topLinkListener);
            txtDrinkTelly.setOnClickListener(drinkIncrementListener);
            compassView.setOnClickListener(compassListener);

        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }


    private View.OnClickListener compassListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mCurrentFriend
            findViewById(R.id.bottel_text).setVisibility(View.GONE);
            if(mCurrentFriend<friendsList.size()){
                FriendsModel currentFriend = friendsList.get(mCurrentFriend);
                txtFriendName.setText(currentFriend.getUserName());
                String distance = "%s %s %s";
                distance = String.format(distance,currentFriend.getDistance(),currentFriend.getCardinal(),currentFriend.getLastTimeStamp());
                txtFrDistance.setText(distance);
                bottalView.setRotation(currentFriend.getBearing()+45);
                if(++mCurrentFriend==friendsList.size()){
                    mCurrentFriend=0;
                }
            }
        }
    };

    private View.OnClickListener drinkIncrementListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDrinkCount++;
            if(preferences.getBoolean(AppConstants.FRIST_DRINK)){
                txtDrinkTelly.setText(String.valueOf(mDrinkCount));
            }else{
                AlertDialog.Builder dialog = Utills.getDialog(MainActivity.this, getString(R.string.local376), getString(R.string.local377));
                dialog.setPositiveButton(R.string.local136, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        preferences.setBoolean(AppConstants.FRIST_DRINK, true);
                        txtDrinkTelly.setText(String.valueOf(mDrinkCount));
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }
        }
    };

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
        if (mCurrentLocation != null && mMap!=null) {
            mMap.clear();
            RadioButton mapTypeBtn = (RadioButton) findViewById(R.id.btn_sat);
            RadioButton appModeBtn = (RadioButton) findViewById(R.id.btn_demo);
            mMap.setMapType(mapTypeBtn.isChecked() ? GoogleMap.MAP_TYPE_SATELLITE : GoogleMap.MAP_TYPE_NORMAL);
            if (appModeBtn.isChecked()) {
                friendsList = AppConstants.getDemoFriends(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            }

            /*for (int i = 0; i < friendsList.size(); i++) {
                FriendsModel friend = friendsList.get(i);
                String title = friend.getUserName();
                LatLng demoLat = friend.getLatLng();
                String distance = "%s %s %s";
                distance = String.format(distance, friend.getDistance(), friend.getCardinal(), friend.getLastTimeStamp());
                mMap.addMarker(new MarkerOptions().position(demoLat).title(title).snippet(distance));
            }*/
            FriendsModel[]aa= new FriendsModel[friendsList.size()];
            aa = friendsList.toArray(aa);
            new markerBitmapDownload().execute(aa);
        }
    }

    private View.OnClickListener createListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String userName = edtCreate.getText().toString();
            if(!TextUtils.isEmpty(userName) && mCurrentLocation!=null) {
                HashMap<String,String> request = new HashMap<>();
                request.put("handle",userName);
                request.put("lat", String.valueOf(mCurrentLocation.getLatitude()));
                request.put("lng", String.valueOf(mCurrentLocation.getLongitude()));
                WebServiceHandler webServiceHandler = WebServiceHandler.getInstanceWithProgress(MainActivity.this);
                webServiceHandler.init("findmydrunkfriends_createid.php", WebServiceHandler.RequestType.GET, request, createHandler);
            }else if(mCurrentLocation==null){
                Utills.checkNetwork(MainActivity.this);
            }else{
                edtCreate.setError(getString(R.string.local315));
            }
        }
    };

    private WebServiceHandler.WebServiceCallBackListener createHandler = new WebServiceHandler.WebServiceCallBackListener() {
        @Override
        public void onRequestSuccess(String response) {
            if(response!=null && "added".equalsIgnoreCase(response)){
                String userName = edtCreate.getText().toString();
                preferences.setString(AppConstants.USER_ID,userName);
                findViewById(R.id.create_form).setVisibility(View.GONE);
                AlertDialog.Builder dialog = Utills.getDialog(MainActivity.this, getString(R.string.local135), getString(R.string.local349));
                dialog.setPositiveButton(R.string.local136, continueAfterCreate);
                dialog.setCancelable(false);
                dialog.show();
            }else{
                AlertDialog.Builder dialog = Utills.getDialog(MainActivity.this, "Failed", getString(R.string.local327));
                dialog.setPositiveButton(R.string.local136, null);
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        @Override
        public void onRequestFailed(String errorMsg) {

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
            setUpNormalUse();
        }
    };


    private View.OnClickListener topLinkListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_friends:
                    Intent friendsActivity =new Intent(MainActivity.this,FriendsActivity.class);
                    friendsActivity.putExtra(LOCATION_KEY, mCurrentLocation);
                    startActivity(friendsActivity);
                    break;
                case R.id.btn_photo:
                    startActivity(new Intent(MainActivity.this,PhotoActivity.class));
                    break;
                case R.id.btn_share:
                    startActivity(new Intent(MainActivity.this,ShareActivity.class));
                    break;
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener switchChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (radioGroup.getId()){
                case R.id.map_switch :
                    if (mMap != null) {
                        if(checkedId==R.id.btn_map){
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }else if(checkedId == R.id.btn_sat){
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        }
                    }
                    break;
                case R.id.app_mode :
                        preferences.setBoolean(AppConstants.APP_MODE,checkedId==R.id.btn_real);
                        mDrinkCount=0;
                        mCurrentFriend=0;
                        txtDrinkTelly.setText("");
                    if(checkedId==R.id.btn_real && mCurrentLocation != null){
                        requestForFriendsList();
                    }else{
                        setUpMap();
                    }

                    break;
                case R.id.distance_switch :
                        preferences.setBoolean(AppConstants.DISTANCE_UNIT,checkedId==R.id.btn_km);
                        mCurrentFriend=0;
                    RadioButton appModeBtn = (RadioButton) findViewById(R.id.btn_demo);

                    if(!appModeBtn.isChecked() && mCurrentLocation != null){
                            requestForFriendsList();
                        }else{
                            setUpMap();
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

    /**
     * Get friends data from server
     * according to user preferences
     * selected on top switches.
     * http://picamerica.com/findmydrunkfriends_getinvites.php?
     * handle=x&pw=gopitt&mylat=x&mylng=x&miles=x
     */
    private void requestForFriendsList(){

        String mUserID = preferences.getString(AppConstants.USER_ID);
        RadioButton distBtn = (RadioButton) findViewById(R.id.btn_miles);

        HashMap<String,String> request = new HashMap<>();
        request.put("handle",mUserID);
        request.put("pw","gopitt");
        request.put("mylat",String.valueOf(mCurrentLocation.getLatitude()));
        request.put("mylng",String.valueOf(mCurrentLocation.getLongitude()));
        request.put("miles",distBtn.isChecked()?"1":"0");

        WebServiceHandler webServiceHandler = WebServiceHandler.getInstanceWithProgress(MainActivity.this);
        webServiceHandler.init("findmydrunkfriends_getinvites.php", WebServiceHandler.RequestType.GET, request, friendsHandler);
    }

    private WebServiceHandler.WebServiceCallBackListener friendsHandler = new WebServiceHandler.WebServiceCallBackListener() {
        @Override
        public void onRequestSuccess(String response) {
            List<FriendsModel> demoFriends = new ArrayList<>();

            try {
                JSONObject responceData = XML.toJSONObject(response);

                if (responceData.has("hit")) {
                    JSONArray hitArray = responceData.getJSONArray("hit");
                    if(hitArray.length()>0){

                        for (int i = 0; i < hitArray.length(); i++) {
                            JSONObject jobj = hitArray.getJSONObject(i);
                            long id = jobj.getLong("id");
                            String title = jobj.getString("name");
                            LatLng friendLatLng = new LatLng(jobj.getDouble("lat"),jobj.getDouble("lng"));
                            String user_id = jobj.getString("sec");
                            String cardinal = jobj.getString("cardinal");
                            String distance = jobj.getString("dist");
                            String timeStamp = jobj.getString("timestamp_lastchg");
                            long bearing = jobj.getLong("bearing");
                            FriendsModel friendsModel = new FriendsModel(user_id,title,cardinal,timeStamp,distance,bearing,friendLatLng);
                            friendsModel.setId(id);
                            demoFriends.add(friendsModel);
                        }
                    }else{

                    }
                }
            } catch (Exception e) {
                Log.i(TAG, "Exception :: " + e.getMessage());
            }
            friendsList = demoFriends;
            setUpMap();
        }

        @Override
        public void onRequestFailed(String errorMsg) {

        }
    };

    public class markerBitmapDownload extends AsyncTask<FriendsModel,Void,List<MarkerOptions>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Setting Friends locations...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<MarkerOptions> doInBackground(FriendsModel... friends) {
            List<MarkerOptions> markers = new ArrayList<>();

            for (int i = 0; i <friends.length ; i++) {
                FriendsModel friend = friends[i];
                Bitmap bmImg = downloadUserImage(friend.getUserId());

                String title = friend.getUserName();
                LatLng demoLat = friend.getLatLng();
                String distance = "%s %s %s";
                distance = String.format(distance, friend.getDistance(), friend.getCardinal(), friend.getLastTimeStamp());
                markers.add(new MarkerOptions().position(demoLat).title(title).snippet(distance).icon(BitmapDescriptorFactory.fromBitmap(bmImg)));
            }
            Bitmap bmImg = downloadUserImage(mUserID);
            markers.add(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).icon(BitmapDescriptorFactory.fromBitmap(bmImg)));
            return markers;
        }

        @Override
        protected void onPostExecute(List<MarkerOptions> markerOptionses) {
            super.onPostExecute(markerOptionses);
            progressDialog.dismiss();
            for (int i = 0; i < markerOptionses.size(); i++) {
                mMap.addMarker(markerOptionses.get(i));
            }

        }
    }

    private Bitmap downloadUserImage(String userId){
        Bitmap bmImg = null;
        try {
            URL url = new URL(String.format("http://picamerica.com/upload/%s.jpg",userId));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bmImg = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bmImg==null)
            bmImg= BitmapFactory.decodeResource(getResources(),
                    R.drawable.default_image);
        bmImg = getCroppedBitmapDrawable(Bitmap.createScaledBitmap(bmImg, 100, 100,
                true));
        return bmImg;
    }

    public Bitmap getCroppedBitmapDrawable(Bitmap bitmap){
        try {
            Bitmap output = Bitmap.createBitmap(100,112, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final Rect brect = new Rect(0, 0, output.getWidth(), output.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            Bitmap bmImg = BitmapFactory.decodeResource(getResources(),
                    R.drawable.redcircle);
            bmImg = Bitmap.createScaledBitmap(bmImg, 100, 112, false);
            canvas.drawBitmap(bmImg, brect, brect, null);
            return output;
        }catch (Exception e){
            e.printStackTrace();

        }
        return null;
    }
}
