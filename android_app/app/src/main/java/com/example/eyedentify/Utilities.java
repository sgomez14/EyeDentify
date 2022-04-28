package com.example.eyedentify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import java.io.File;

public class Utilities {

    public static String NOT_APPLICABLE = "na";
    public static String MSG_SEPARATOR = "%";
    public static String TXT_SEPARATOR = "%%%";
    public static int MESSAGE_FULL_LENGTH = 3;
    public static int DESCRIPTION_AND_KEYWORDS = 2;
    public static int JUST_DESCRIPTION = 1;
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
