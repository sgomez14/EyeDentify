package com.example.eyedentify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utilities {

    /*
    newActivityWithImageResults is called from CloudSight imageRecognized call back function
    This makes sure that TagActivity gets initiated immediately after cloudsight responds,
    guaranteeing real time response.
    */
    public static void newActivityWithImageResults(Context context, String cloudSightResult, String mlkitResult, File imageFile){
        Intent resultsActivity = new Intent(context, TagActivity.class);
        // indicate the source of the intent
        resultsActivity.putExtra("cameraResults", "image results");
        Bundle resultsBundle = new Bundle();
        resultsBundle.putString("cloudSightResult", cloudSightResult);
        resultsBundle.putString("mlkitResult", mlkitResult);
        resultsBundle.putString("imageFile", imageFile.toString());
        resultsActivity.putExtras(resultsBundle);
        context.startActivity(resultsActivity);
    }
}
