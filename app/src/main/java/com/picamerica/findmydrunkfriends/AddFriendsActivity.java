package com.picamerica.findmydrunkfriends;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.picamerica.findmydrunkfriends.utils.AppConstants;
import com.picamerica.findmydrunkfriends.utils.Preferences;
import com.picamerica.findmydrunkfriends.utils.Utills;


public class AddFriendsActivity extends BaseActivity {


    private Preferences preferences;
    private boolean isDemo = true;

    private TextView txtUserName;
    private View backBtn;
    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    private View.OnClickListener infoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!isDemo){
                alertInfo();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        preferences = Preferences.getInstance(this);

        backBtn = findViewById(R.id.img_back);
        txtUserName = (TextView) findViewById(R.id.user_name);
        backBtn.setOnClickListener(backListener);
        String mUserID = preferences.getString(AppConstants.USER_ID);
        if(mUserID!=null){
            txtUserName.setText(mUserID);
        }
        isDemo = !preferences.getBoolean(AppConstants.APP_MODE);
        if(isDemo){
            alertDemo();
        }

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_friends;
    }

    private void alertDemo(){
        AlertDialog.Builder dialog = Utills.getDialog(AddFriendsActivity.this, getString(R.string.local135), getString(R.string.local354));
        dialog.setPositiveButton(R.string.local136,null);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void alertInfo(){
        AlertDialog.Builder dialog = Utills.getDialog(AddFriendsActivity.this, getString(R.string.local164), getString(R.string.local320));
        dialog.setPositiveButton(R.string.local136,null);
        dialog.setCancelable(false);
        dialog.show();
    }

}
