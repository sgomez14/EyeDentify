package com.example.eyedentify;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.util.Locale;

import ai.cloudsight.androidsdk.CloudSightCallback;
import ai.cloudsight.androidsdk.CloudSightClient;
import ai.cloudsight.androidsdk.CloudSightResponse;

public class CloudSight {
    static String  language_code = Locale.getDefault().getLanguage();

    // 1) Constructor for MainActivity and TagActivity to call this CloudSight helper class
    public CloudSight (Context context, String mlkitResult, File file) {
        uploadImageRequest(context, mlkitResult, file);
    }

    // 2) Upload the image request to CloudSight API
    public static void uploadImageRequest (Context context, String mlkitResult, File file) {
        // 3) Instantiate client with key pass to connect to CloudSight API
        CloudSightClient client = new CloudSightClient().init("iTabmDiyViBULrkoBBCVHA");

        // 4) Set Client region to USA
        client.setLocale("en-US");
        client.setNsfw(true);

        // 5) Pass in photo file and analyze photo using API
        client.getImageInformation(file, new CloudSightCallback() {

            /*
            6) imageUploaded & imageRecognized
            Successful case when image is recognized
            Cloudsight would pass back a response using call back function 'imageRecognized'.
            Once call back function is called, call main activity's newActivityWithImageResults to
            start next activity TagActivity.
            This makes sure that next activity gets initiated immediately after cloudsight responds leaving no delay
            and in real time.
             */
            @Override
            public void imageUploaded(CloudSightResponse response) {
                Log.d("Cloudsight debug", "imageUploaded " + response.getStatus());
            }

            @Override
            public void imageRecognized(CloudSightResponse response) {
                Log.d("Cloudsight debug", response.getName());
                Utilities.newActivityWithImageResults(context, GoogleTranslate.translate(response.getName(),language_code, context.getApplicationContext()), mlkitResult, file);
            }


            /*
            7) imageRecognitionFailed & onFailure
            they are detected when image failed to get recognized or API key is wrong
            */
            @Override
            public void imageRecognitionFailed(String reason) {
                Log.d("Cloudsight debug", "imageRecognitionFailed " + reason);
                Utilities.newActivityWithImageResults(context, context.getString(R.string.on_cloudsight_failure), mlkitResult, file);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("Cloudsight debug", "onFailure " + throwable.getLocalizedMessage());
                Utilities.newActivityWithImageResults(context, context.getString(R.string.on_cloudsight_failure), mlkitResult, file);
            }
        });
    }
}
