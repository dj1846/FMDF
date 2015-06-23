package com.picamerica.findmydrunkfriends;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.picamerica.findmydrunkfriends.utils.AppConstants;
import com.picamerica.findmydrunkfriends.utils.ImageUtility;
import com.picamerica.findmydrunkfriends.utils.Preferences;
import com.picamerica.findmydrunkfriends.utils.Utills;
import com.picamerica.findmydrunkfriends.webservices.WebServiceHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;


public class PhotoActivity extends BaseActivity {
    protected static final String TAG = PhotoActivity.class.getCanonicalName();

    protected static final int CAMERA_PIC_REQUEST = 21250;
    protected static final int GALLERY_REQUEST = 2750;
    private Uri fileUri = null;
    private File targetLoc = null;
    private Preferences preferences;

    Button buttonCamera;
    Button buttonAlbum;
    Button buttonUpload;
    ImageView showImage;
    View imageContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences  = Preferences.getInstance(this);

        buttonCamera = (Button) findViewById(R.id.button_camera);
        buttonAlbum  = (Button) findViewById(R.id.button_album);
        buttonUpload = (Button) findViewById(R.id.button_upload);
        showImage    = (ImageView) findViewById(R.id.image_profile);
        imageContainer = findViewById(R.id.photo_lyt);
        buttonCamera.setOnClickListener(cameraListener);
        buttonAlbum.setOnClickListener(albumListener);
        buttonUpload.setOnClickListener(uploadListener);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_photo;
    }

    private View.OnClickListener cameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = ImageUtility.getOutputMediaFileUri(ImageUtility.MEDIA_TYPE_IMAGE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            // start the image capture Intent
            startActivityForResult(intent, CAMERA_PIC_REQUEST);
        }
    };

    private View.OnClickListener albumListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pictureActionIntent,
                    GALLERY_REQUEST);

        }
    };
    private View.OnClickListener uploadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            HashMap<String,String> requestMap = new HashMap();
            HashMap<String,File> imgs = new HashMap();
            String mUserID = preferences.getString(AppConstants.USER_ID);

            try {
                imgs.put("userfile", targetLoc);
                WebServiceHandler webServiceHandler = WebServiceHandler.getInstanceWithProgress(PhotoActivity.this);
                webServiceHandler.init("findmydrunkfriends_upload.php", fileUploadHandler,WebServiceHandler.RequestType.MULTIPART,requestMap,imgs);
            }catch (Exception e) {
                Log.i(TAG, "EXCEPTION requestToServer :: " + e.getMessage());
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        browseHandler(requestCode, resultCode, data);
    }

    private void browseHandler(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {

                case CAMERA_PIC_REQUEST:
                    if (resultCode == Activity.RESULT_OK) {

                        BitmapFactory.Options options = new BitmapFactory.Options();
//
                        // downsizing image as it throws OutOfMemory Exception for
                        // larger
                        // images
                        options.inSampleSize = 2;

                        final Bitmap bitmap = BitmapFactory.decodeFile(
                                fileUri.getPath(), options);
                        FileOutputStream fOut;
                        try {
                            fOut = new FileOutputStream(fileUri.getPath());
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                            fOut.flush();
                            fOut.close();

                        } catch (Exception e) {

                        }
                        targetLoc = new File(fileUri.getPath());
                        showImage.setImageBitmap(bitmap);
                        break;
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.e("TAG", "Selecting picture cancelled");
                    }
                    break;

                case GALLERY_REQUEST:

                    if (resultCode == Activity.RESULT_OK) {
                        Uri uri = data.getData();
                        if (uri != null) {

                            // User had pick an image.
                            Cursor cursor = getContentResolver()
                                    .query(uri,
                                            new String[]{android.provider.MediaStore.Images.ImageColumns.DATA},
                                            null, null, null);
                            cursor.moveToFirst();
                            // Link to the image
                            final String imageFilePath = cursor.getString(0);

                            // Assign string path to File
                            ImageUtility.Default_DIR = new File(imageFilePath);

                            // Create new dir MY_IMAGES_DIR if not created and copy
                            // image into that dir and store that image path in
                            // valid_photo
                            ImageUtility.Create_MY_IMAGES_DIR();

                            ImageUtility.Default_File_Name = preferences.getString(AppConstants.USER_ID);


                            // Copy your image
                            ImageUtility.copyFile(ImageUtility.Default_DIR, ImageUtility.MY_IMG_DIR);

                            // Get new image path and decode it
                            targetLoc = ImageUtility.Paste_Target_Location;
                            Bitmap b = ImageUtility
                                    .decodeFile(ImageUtility.Paste_Target_Location);

                            // use new copied path and use anywhere
                            Bitmap thumbnail = Bitmap.createScaledBitmap(b, 200, 200,
                                    true);
                            // set your selected image in image view
                            showImage.setImageBitmap(thumbnail);

                            cursor.close();

                        } else {
                            Toast.makeText(this,
                                    "Sorry!!! You haven't selected any image.",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.e(TAG, "Selecting picture cancelled");
                    }
                    break;
            }
        }catch (Exception e){
            Log.e(TAG, "Exception in browseHandler::"+e.getMessage());
        }
    }

    private WebServiceHandler.WebServiceCallBackListener fileUploadHandler = new WebServiceHandler.WebServiceCallBackListener() {
        @Override
        public void onRequestSuccess(String response) {
            if(response!=null){
                Log.e(TAG,response);
            }
            alertSuccess();
        }

        @Override
        public void onRequestFailed(String errorMsg) {

        }
    };

    private void alertSuccess(){
        AlertDialog.Builder dialog = Utills.getDialog(PhotoActivity.this, getString(R.string.local135), getString(R.string.local365));
        dialog.setPositiveButton(R.string.local136, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PhotoActivity.this.finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
}
