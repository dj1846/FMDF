package picamerica.com.findmydrunkfriends;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import picamerica.com.findmydrunkfriends.utils.Utills;

public class MainActivity extends FragmentActivity {

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
        mapSwitch      = (RadioGroup) findViewById(R.id.friends_switch);
        appModeSwitch  = (RadioGroup) findViewById(R.id.app_mode);
        distanceSwitch = (RadioGroup) findViewById(R.id.distance_switch);
        friendSwitch   = (RadioGroup) findViewById(R.id.friends_switch);
        mapSwitch.setOnCheckedChangeListener(switchChangedListener);
        appModeSwitch.setOnCheckedChangeListener(switchChangedListener);
        distanceSwitch.setOnCheckedChangeListener(switchChangedListener);
        friendSwitch.setOnCheckedChangeListener(switchChangedListener);
        btnCreate.setOnClickListener(createListener);
        Utills.checkNetwork(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(-.0055, +.0015)).title("Ryan Smith"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-.002, +.003)).title("Candace Johnson"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(+ .001, - .007)).title("Kevin Newport"));
        LatLng coordinate = new LatLng(-.0055, +.0015);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 20);
        mMap.animateCamera(yourLocation);

    }

    /*Ryan Smith
    demolat = userlat - .0055;
    demolng = userlng + .0015;
    user id = “rsmith”;
    bearing = 165;
    dist = [NSString stringWithFormat:@".9 miles SSE of you, 16 minutes ago"];
    Candace Johnson
    demolat = userlat - .002;
    demolng = userlng + .003;
    user id = “ccj”;
    bearing = 125;
    dist = [NSString stringWithFormat:@".3 miles SE of you, 5 minutes ago"];
    Kevin Newport
    demolat = userlat + .001;
    demolng = userlng - .007;
    user id = “newport1”;
    bearing = 280;
    dist = [NSString stringWithFormat:@"1.7 miles NW of you, 2 minutes*/

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


}
