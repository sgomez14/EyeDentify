package com.example.eyedentify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ImageRecognitionPendingActivity extends AppCompatActivity {

    /*
    API loading page for Cloudsight and MLkit
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recognition_pending);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ImageRecognitionPendingActivity.this, MainActivity.class));
    }
}