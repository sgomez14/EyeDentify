package com.example.eyedentifyguidraft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class TagActivity extends AppCompatActivity {

    // this activity simulates the results of taking a photo and processing it with computer vision ML
    // this results show the photo of the item, a descriptive sentence, and words that were
    // identified on the item.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }
}