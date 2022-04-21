package com.example.eyedentify;

import android.content.Context;
import android.util.Log;
import java.io.File;
import ai.cloudsight.androidsdk.CloudSightCallback;
import ai.cloudsight.androidsdk.CloudSightClient;
import ai.cloudsight.androidsdk.CloudSightResponse;

public class CloudSight {
    // 1) Constructor
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
            @Override

            public void imageUploaded(CloudSightResponse response) {
                Log.d("Cloudsight debug", "imageUploaded " + response.getStatus());
            }

            /*
            6) Successful case when image is recognized
            Cloudsight would pass back a response using call back function 'imageRecognized'.
            Once call back function is called, call main activity's newActivityWithImageResults to
            start next activity TagActivity.
            This makes sure that next activity gets initiated immediately after cloudsight responds leaving no delay
            and in real time.
             */
            @Override
            public void imageRecognized(CloudSightResponse response) {
                Log.d("Cloudsight debug", response.getName());
                Utilities.newActivityWithImageResults(context, response.getName(), mlkitResult, file);
            }

            // 7) Failure case
            @Override
            public void imageRecognitionFailed(String reason) {
                Log.d("Cloudsight debug", "imageRecognitionFailed " + reason);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("Cloudsight debug", "onFailure " + throwable.getLocalizedMessage());
            }
        });
    }
}
