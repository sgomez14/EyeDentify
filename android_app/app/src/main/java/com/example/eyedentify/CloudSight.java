package com.example.eyedentify;

import android.util.Log;
import android.widget.TextView;
import java.io.File;
import ai.cloudsight.androidsdk.CloudSightCallback;
import ai.cloudsight.androidsdk.CloudSightClient;
import ai.cloudsight.androidsdk.CloudSightResponse;

public class CloudSight {
    final public static String CLOUDSIGHT_ERROR = "CloudSightFailed";

    // 1) Constructor
    public CloudSight (File file, TextView statusTextView, TextView resultTextView) {
        uploadImageRequest(file/*, statusTextView, resultTextView*/);
    }

    // 2) Upload the image request to CloudSight API
    public static String uploadImageRequest (File file/*, TextView statusTextView, TextView resultTextView*/) {
        final String[] cloudSightResult = {CLOUDSIGHT_ERROR};
        // 3) Instantiate client with key pass to connect to CloudSight API
        CloudSightClient client = new CloudSightClient().init("iTabmDiyViBULrkoBBCVHA");

        // 4) Set Client region to USA
        client.setLocale("en-US");
        client.setNsfw(true);

        // 5) Pass in photo file and analyze photo using API
        client.getImageInformation(file, new CloudSightCallback() {
            @Override
            // 6) Successful case
            public void imageUploaded(CloudSightResponse response) {
                Log.d("EyeDentify ", "imageUploaded");
//                statusTextView.setText("Status: " + response.getStatus());
//                resultTextView.setText("Result: " + response.getName());
                cloudSightResult[0] = response.getName(); // return the string result
            }

            @Override
            public void imageRecognized(CloudSightResponse response) {
                Log.d("EyeDentify ", "imageRecognized");
//                statusTextView.setText("Status: " + response.getStatus());
//                resultTextView.setText("Result: " + response.getName());
                cloudSightResult[0] = response.getName(); // return the string result
            }

            // 7) Failure case
            @Override
            public void imageRecognitionFailed(String reason) {
                Log.d("EyeDentify ", "imageRecognitionFailed");
//                statusTextView.setText("Status: " + reason);
                Log.d("Mandy ", reason);

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("EyeDentify ", "onFailure");
//                statusTextView.setText("Status: " + throwable.getLocalizedMessage());
                Log.d("Mandy ", throwable.getLocalizedMessage());
            }
        });

        return cloudSightResult[0]; // return the result of CloudSight call
    }
}
