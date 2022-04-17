package com.example.eyedentify;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int IMAGE_CAPTURE_CODE = 101;
    Uri image_uri;

    // Strings to save results from image recognition algorithms
    private String cloudSightResult;
    private String mlkitResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) Request camera access permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }


    }

    // 4) After photo is taken, come back to the app
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Confirm requestCode and resultCode are valid
        if (requestCode == IMAGE_CAPTURE_CODE) {
            if (resultCode == RESULT_OK) {
                // Get picture data and save as Uri format
                Uri resultUri = data.getData();
                try{

                    File imageFile = new File(getPath(resultUri)); // convert image to File for CloudSight

                    cloudSightResult = CloudSight.uploadImageRequest(imageFile); // pass to cloudsight


                    Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath()); // convert image to bitmap for MLKit
                    mlkitResult = MLKit.getTextFromImage(imageBitmap, this); // pass image to MLKit
                }
                catch (Exception e) {
                    Log.d("EyeDentify", e.getMessage());
                }

            }
        }
    }


    private void openCamera() {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From The Camera");
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
}