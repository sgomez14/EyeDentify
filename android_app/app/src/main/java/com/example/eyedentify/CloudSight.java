package com.example.eyedentify;

import android.util.Log;
import android.widget.TextView;
import java.io.File;
import ai.cloudsight.androidsdk.CloudSightCallback;
import ai.cloudsight.androidsdk.CloudSightClient;
import ai.cloudsight.androidsdk.CloudSightResponse;

public class CloudSight {

    // 1) Constructor
    public CloudSight (File file, TextView statusTextView, TextView resultTextView) {
        uploadImageRequest(file, statusTextView, resultTextView);
    }

    // 2) Upload the image request to CloudSight API
    private void uploadImageRequest (File file, TextView statusTextView, TextView resultTextView) {

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
                Log.d("Mandy ", "imageUploaded");
                statusTextView.setText("Status: " + response.getStatus());
                resultTextView.setText("Result: " + response.getName());
            }

            @Override
            public void imageRecognized(CloudSightResponse response) {
                Log.d("Mandy ", "imageRecognized");
                statusTextView.setText("Status: " + response.getStatus());
                resultTextView.setText("Result: " + response.getName());
            }

            // 7) Failure case
            @Override
            public void imageRecognitionFailed(String reason) {
                Log.d("Mandy", "imageRecognitionFailed");
                statusTextView.setText("Status: " + reason);
                Log.d("Mandy ", reason);

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("Mandy", "onFailure");
                statusTextView.setText("Status: " + throwable.getLocalizedMessage());
                Log.d("Mandy ", throwable.getLocalizedMessage());
            }
        });
    }
}
