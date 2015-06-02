package picamerica.com.findmydrunkfriends.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import picamerica.com.findmydrunkfriends.R;

/**
 * Created by ErRk on 6/2/2015.
 */
public class Utills {

    private static String TAG = Utills.class.getCanonicalName();

    public static void checkNetwork(final Context context) {
        LocationManager lm = null;
        boolean gps_enabled = false, network_enabled = false;
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                }
            });
            dialog.show();

        }
    }

    public static AlertDialog.Builder getDialog(Context context,String title,String message){
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(title);
            dialog.setMessage(message);
            return dialog;
        }catch (Exception e){
            Log.v(TAG,"Exception in getDialog::"+e.getMessage());
        }
        return null;
    }
}
