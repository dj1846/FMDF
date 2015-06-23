package com.picamerica.findmydrunkfriends.adapters;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.picamerica.findmydrunkfriends.MainActivity;
import com.picamerica.findmydrunkfriends.R;
import com.picamerica.findmydrunkfriends.utils.AppConstants;
import com.picamerica.findmydrunkfriends.utils.FriendsModel;
import com.picamerica.findmydrunkfriends.utils.Preferences;
import com.picamerica.findmydrunkfriends.utils.Utills;
import com.picamerica.findmydrunkfriends.webservices.WebServiceHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by RK on 6/14/2015.
 */
public class FriendsAdapter extends ArrayAdapter<FriendsModel> {
    protected static final String TAG = MainActivity.class.getCanonicalName();
    Context mContext;
    Preferences preferences;
    boolean isDemo = true;
    HashMap<Integer,FriendsModel> checkedList = new HashMap<>();


    public FriendsAdapter(Context context, int resource, int textViewResourceId, List<FriendsModel> objects) {
        super(context, resource, textViewResourceId, objects);
        mContext = context;
        preferences= Preferences.getInstance(mContext);
        isDemo = !preferences.getBoolean(AppConstants.APP_MODE);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FriendsModel currentFriend = getItem(position);
        View view = super.getView(position, convertView, parent);
        View imgDelete = view.findViewById(R.id.img_delete);
        TextView txtFrDistance = (TextView) view.findViewById(R.id.txt_fr_distance);
        final CheckBox shareLoc = (CheckBox) view.findViewById(R.id.checkbox_share_location);
        if(currentFriend.isLocationShared()){
            checkedList.put(position, currentFriend);
        }

        shareLoc.setChecked(checkedList.containsKey(position)||isDemo);
        shareLoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkedList.put(position, currentFriend);
                } else {
                    checkedList.remove(position);
                    compoundButton.setChecked(isDemo);
                    alertDemo();
                }
                if (!isDemo)
                    toggleLocationShared(currentFriend, position);
            }
        });
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDemo)
                    alertDemoDelete();
            }
        });
        String distance = "%s %s\n %s";
        distance = String.format(distance, currentFriend.getDistance(), currentFriend.getCardinal(), currentFriend.getLastTimeStamp());
        txtFrDistance.setText(distance);
        return view;
    }

    private void toggleLocationShared(FriendsModel currentFriend, int position) {
        try{
            String mUserID =preferences.getString(AppConstants.USER_ID);
            HashMap<String,String> request = new HashMap<>();
            request.put("handle",mUserID);
            request.put("pw","gopitt");
            request.put("id",currentFriend.getId()+"");

            WebServiceHandler webServiceHandler = WebServiceHandler.getInstanceWithProgress(mContext);
            webServiceHandler.init("findmydrunkfriends_flipit.php", WebServiceHandler.RequestType.GET, request, new WebServiceHandler.WebServiceCallBackListener() {
                @Override
                public void onRequestSuccess(String response) {
                    Toast.makeText(mContext,"Updated successfully.",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRequestFailed(String errorMsg) {
                    Toast.makeText(mContext,"Oops!,problem while Updating\nplease try again",Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    private void deleteFriend(FriendsModel currentFriend, int position) {
        try{
            String mUserID = preferences.getString(AppConstants.USER_ID);
            HashMap<String,String> request = new HashMap<>();
            request.put("handle",mUserID);
            request.put("pw","gopitt");
            request.put("id",currentFriend.getId()+"");

            WebServiceHandler webServiceHandler = WebServiceHandler.getInstanceWithProgress(mContext);
            webServiceHandler.init("findmydrunkfriends_flipit.php", WebServiceHandler.RequestType.GET, request, new WebServiceHandler.WebServiceCallBackListener() {
                @Override
                public void onRequestSuccess(String response) {
                    Toast.makeText(mContext,"Updated successfully.",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRequestFailed(String errorMsg) {
                    Toast.makeText(mContext,"Oops!,problem while Updating\nplease try again",Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    private void alertDemo(){
        AlertDialog.Builder dialog = Utills.getDialog(mContext, mContext.getString(R.string.local135), mContext.getString(R.string.local353));
        dialog.setPositiveButton(R.string.local136,null);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void alertDemoDelete(){
        AlertDialog.Builder dialog = Utills.getDialog(mContext, mContext.getString(R.string.local135), mContext.getString(R.string.local352));
        dialog.setPositiveButton(R.string.local136,null);
        dialog.setCancelable(false);
        dialog.show();
    }
}
