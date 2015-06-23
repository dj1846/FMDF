package com.picamerica.findmydrunkfriends;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RadioButton;

import com.google.android.gms.maps.model.LatLng;
import com.picamerica.findmydrunkfriends.adapters.FriendsAdapter;
import com.picamerica.findmydrunkfriends.utils.AppConstants;
import com.picamerica.findmydrunkfriends.utils.FriendsModel;
import com.picamerica.findmydrunkfriends.utils.Preferences;
import com.picamerica.findmydrunkfriends.webservices.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FriendsActivity extends BaseActivity {
    protected static final String TAG = FriendsActivity.class.getCanonicalName();

    private List<FriendsModel> friendsList = new ArrayList<>();

    private Preferences preferences;
    private View backBtn;
    private View addBtn;
    private ListView friendsListView;
    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };
    private View.OnClickListener addFriendListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(FriendsActivity.this,AddFriendsActivity.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        preferences = Preferences.getInstance(this);
        setActionBarTitle(R.string.title_activity_friends);
        backBtn = findViewById(R.id.img_back);
        addBtn = findViewById(R.id.img_add);
        friendsListView = (ListView) findViewById(R.id.list_friends);
        backBtn.setOnClickListener(backListener);
        addBtn.setOnClickListener(addFriendListener);
        setUpData();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_friends;
    }

    private void setUpData(){
        try{
            boolean isDemo = !preferences.getBoolean(AppConstants.APP_MODE);
            if (isDemo){
                friendsList = AppConstants.getDemoFriends(new LatLng(0.0,0.0));
                FriendsAdapter friendsAdapter = new FriendsAdapter(FriendsActivity.this,R.layout.friends_list_item,R.id.txt_fr_distance,friendsList);
                friendsListView.setAdapter(friendsAdapter);
            }else{
                requestForFriendsList();
            }

        }catch (Exception e){
            Log.v("getLayoutResource",e.getMessage());
        }
    }

    /**
     * Get friends data from server
     * according to user preferences
     * selected on top switches.
     * http://picamerica.com/findmydrunkfriends_getinvites.php?
     * handle=x&pw=gopitt&mylat=x&mylng=x&miles=x
     */
    private void requestForFriendsList(){

        String mUserID =preferences.getString(AppConstants.USER_ID);
        HashMap<String,String> request = new HashMap<>();
        request.put("handle",mUserID);
        request.put("pw","gopitt");
        if(getIntent().hasExtra(MainActivity.LOCATION_KEY)){
            Location mCurrentLocation = getIntent().getParcelableExtra(MainActivity.LOCATION_KEY);
            request.put("mylat",String.valueOf(mCurrentLocation.getLatitude()));
            request.put("mylng",String.valueOf(mCurrentLocation.getLongitude()));

        }
        request.put("miles","1");

        WebServiceHandler webServiceHandler = WebServiceHandler.getInstanceWithProgress(FriendsActivity.this);
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
                            friendsModel.setLocationShared(jobj.getInt("checked")==1);
                            demoFriends.add(friendsModel);
                        }
                    }else{

                    }
                }
            } catch (Exception e) {
                Log.i(TAG, "Exception :: " + e.getMessage());
            }
            friendsList = demoFriends;
            FriendsAdapter friendsAdapter = new FriendsAdapter(FriendsActivity.this,R.layout.friends_list_item,R.id.txt_fr_distance,friendsList);
            friendsListView.setAdapter(friendsAdapter);
        }

        @Override
        public void onRequestFailed(String errorMsg) {

        }
    };
}
