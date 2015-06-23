package com.picamerica.findmydrunkfriends;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.picamerica.findmydrunkfriends.utils.AppConstants;
import com.picamerica.findmydrunkfriends.utils.Preferences;


public class ShareActivity extends BaseActivity {


    private Preferences preferences;
    protected String mUserID = null;

    private View btnBack;
    private View btnHoobee;
    private View btnUpgrade;
    private View btnGetUpgrade;
    private View btnAppReview;
    private View btnGetHoobee;
    private View btnFeedback;
    private View btnMail;
    private View btnText;
    private View lytHoobeeInfo;
    private View lytUpgradeInfo;
    private View lytShareInfo;

    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            lytHoobeeInfo.setVisibility(View.GONE);
            lytUpgradeInfo.setVisibility(View.GONE);
            if(lytShareInfo.getVisibility()==View.GONE){
                lytShareInfo.setVisibility(View.VISIBLE);
            }else {
                onBackPressed();
            }
        }
    };
    private View.OnClickListener hoobeeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            lytHoobeeInfo.setVisibility(View.VISIBLE);
            lytShareInfo.setVisibility(View.GONE);
            lytUpgradeInfo.setVisibility(View.GONE);
        }
    };
    private View.OnClickListener upgradeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            lytUpgradeInfo.setVisibility(View.VISIBLE);
            lytShareInfo.setVisibility(View.GONE);
            lytHoobeeInfo.setVisibility(View.GONE);
        }
    };
    private View.OnClickListener mailListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.local6));
            email.putExtra(Intent.EXTRA_TEXT, getString(R.string.local208A, mUserID, getPackageName()) + "\n\nSent from Android");

            // need this to prompts email client only
            email.setType("message/rfc822");

            try {
                startActivity(Intent.createChooser(email, "Choose an Email client"));
            }
            catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ShareActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private View.OnClickListener textListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent smsVIntent = new Intent(Intent.ACTION_VIEW);
            // prompts only sms-mms clients
            smsVIntent.setType("vnd.android-dir/mms-sms");
            // extra fields for number and message respectively
            smsVIntent.putExtra("sms_body", getString(R.string.local208A, mUserID, getPackageName()));
            try{
                startActivity(smsVIntent);
            } catch (Exception ex) {
                Toast.makeText(ShareActivity.this, "Your sms has failed...", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
    };
    private View.OnClickListener feedbackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[] { "suggestions@TheGlobelAppCompany.com" });
            email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.local379));
            email.putExtra(Intent.EXTRA_TEXT, "Sent from Android");

            // need this to prompts email client only
            email.setType("message/rfc822");
            try {
                startActivity(Intent.createChooser(email, "Choose an Email client"));
            }
            catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ShareActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener getHoobeeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String appPackageName = "com.picamerica.hoobee";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    };

    private View.OnClickListener getUpgradeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String appPackageName = "com.picamerica.findmydrunkfriends.pro";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    };

    private View.OnClickListener appReviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String appPackageName = "com.picamerica.findmydrunkfriends";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        preferences = Preferences.getInstance(this);
        mUserID = preferences.getString(AppConstants.USER_ID);


        btnFeedback = findViewById(R.id.btn_feedback);
        btnText = findViewById(R.id.btn_text);
        btnMail = findViewById(R.id.btn_mail);
        btnBack = findViewById(R.id.img_back);
        btnHoobee = findViewById(R.id.btn_hoobee);
        btnUpgrade = findViewById(R.id.btn_upgrade);
        btnGetUpgrade = findViewById(R.id.btn_get_upgrade);
        btnGetHoobee = findViewById(R.id.btn_get_hoobee);
        btnAppReview = findViewById(R.id.btn_app_review);
        lytHoobeeInfo = findViewById(R.id.hoobee_info);
        lytUpgradeInfo = findViewById(R.id.upgrade_info);
        lytShareInfo = findViewById(R.id.share_info);
        btnBack.setOnClickListener(backListener);
        btnHoobee.setOnClickListener(hoobeeListener);
        btnUpgrade.setOnClickListener(upgradeListener);
        btnFeedback.setOnClickListener(feedbackListener);
        btnText.setOnClickListener(textListener);
        btnMail.setOnClickListener(mailListener);
        btnGetHoobee.setOnClickListener(getHoobeeListener);
        btnGetUpgrade.setOnClickListener(getUpgradeListener);
        btnAppReview.setOnClickListener(appReviewListener);


    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_share;
    }
}
