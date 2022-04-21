package com.example.eyedentify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;

public class Utilities {

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
