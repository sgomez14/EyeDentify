package com.example.eyedentify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int IMAGE_CAPTURE_CODE = 101;
    Uri image_uri;
    private SharedPreferences sp;

    // Strings to save results from image recognition algorithms
    private String cloudSightResult;
    private String mlkitResult;

    private CardView btnTagItem, btnScanItem;
    private NFC nfc;
    PendingIntent pendingIntent;
    IntentFilter filters[];
    NfcAdapter adapter;
    boolean writeMode;

    private Animation button_anim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("eyedentify", Context.MODE_PRIVATE);
        // 1) Request camera access permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

        btnTagItem  = findViewById(R.id.cardViewTagItem);
        btnScanItem = findViewById(R.id.cardViewScanItem);

        button_anim = AnimationUtils.loadAnimation(this, R.anim.button_anim);

        // stuff related to NFC
        nfc = NFC.makeNFC(this);
        //make an adapter for nfc to read and write
        adapter = nfc.adapter;
        //try to read once to see if there's a tag
        nfc.readIntent(getIntent());
        //this MUTABLE intent flag will allow the tag information to be actually modified.
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        //will handle NFC Tag discovered intents
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        filters = new IntentFilter[]{tagDetected};


        // Tag item button listener
        btnTagItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTagItem.startAnimation(button_anim); // start button animation
                startActivity(new Intent(MainActivity.this, TagActivity.class));
            }
        });

        // Scan item button listener
        btnScanItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnScanItem.startAnimation(button_anim); // start button animation
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED
                    ){
                        String[] permission = {Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else{
                        openCamera();
                    }
                } else{
                    openCamera();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, R.string.perm_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 4) After photo is taken, come back to the app
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Confirm requestCode and resultCode are valid
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            try{
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {

                        // onPreExecute Method
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent loadResults = new Intent(MainActivity.this, ImageRecognitionPendingActivity.class);
                                startActivity(loadResults);
                            }
                        });

                        // convert image to File image recognition sdks
                        File imageFile = new File(getPath(image_uri));

                        // convert imageFile to bitmap for ML kit
                        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.toString());
                        try {
                            imageBitmap = rotateImage(imageBitmap, imageFile.toString()); // need to handle IOException

                            // pass image bitmap to MLKit class
                            mlkitResult = MLKit.getTextFromImage(imageBitmap, MainActivity.this); // pass image to MLKit

                            // pass image file to CloudSight class
                            new CloudSight(MainActivity.this, mlkitResult, imageFile);
                        } catch (IOException e) {
                            Log.e("Error Rotating Image", e.getMessage());
                            Toast.makeText(MainActivity.this, "Image Rotation Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
            catch (Exception e) {
                Log.d("EyeDentify debug", e.getMessage());
            }
        }
    }

    /*
    Function to open camera intent
     */
    private void openCamera() {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From The Camera");
        //set folder for image
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/" + getResources().getString(R.string.app_name));

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    // 3) Convert path of the photo uri to a string
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
        
    }


    /*
     Gets triggered whenever a tag gets read
     fills in the tag information for nfc instance
     gets to result page with the tag information
     */
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        nfc.readIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            nfc.myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(nfc.myTagInfo != null && sp.contains(nfc.myTagInfo) && sp.getString(nfc.myTagInfo, null).split(Utilities.MSG_SEPARATOR).length == Utilities.MESSAGE_FULL_LENGTH){
                startActivity(new Intent(MainActivity.this, ResultActivity.class).putExtra("tagInfo", nfc.myTagInfo));
            }
            else {
                    Toast.makeText(this, R.string.cannot_read, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    NFC write mode off when paused
     */
    @Override
    public void onPause(){
        super.onPause();
        writeModeOff();
    }

    /*
    NFC write mode on when paused
     */
    @Override
    public void onResume(){
        super.onResume();
        writeModeOn();
    }

    /*
    Disabling write mode for NFC
     */
    private void writeModeOff(){
        writeMode = false;
        adapter.disableForegroundDispatch(this);
    }

    /*
    Enabling write mode for NFC
     */
    private void writeModeOn(){
        writeMode = true;
        adapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    /*
    Rotate bitmap image, reference: https://gist.github.com/tomogoma/788e3b775dd611c9226f8e17781a0f0c
     */
    public static Bitmap rotateImage(Bitmap imageBitmap, String path) throws IOException {

        int rotate = 0;
        ExifInterface exif; // this interface helps get tags about the image's orientation
        exif = new ExifInterface(path);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        Matrix matrixForRotation = new Matrix();
        matrixForRotation.postRotate(rotate);
        Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0,0, imageBitmap.getWidth(),
                imageBitmap.getHeight(), matrixForRotation, true);
        return rotatedBitmap;
    }


    @Override
    public void onBackPressed() {
        finishAffinity(); //exit the app
    }

}