package com.example.eyedentify;

import android.util.Log;
import android.widget.TextView;
import java.io.File;
import ai.cloudsight.androidsdk.CloudSightCallback;
import ai.cloudsight.androidsdk.CloudSightClient;
import ai.cloudsight.androidsdk.CloudSightResponse;

public class CloudSight {
    final public static String CLOUDSIGHT_ERROR = "CloudSightFailed";
    String cloudSightResult = CLOUDSIGHT_ERROR;

    // 1) Constructor
    public CloudSight (File file/*, TextView statusTextView, TextView resultTextView*/) {
        cloudSightResult = uploadImageRequest(file/*, statusTextView, resultTextView*/);
    }

    // 2) Upload the image request to CloudSight API
    public String uploadImageRequest (File file/*, TextView statusTextView, TextView resultTextView*/) {
        Log.e("enterin CS request", "checking entering cloudsight result");
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
                cloudSightResult = response.getName(); // return the string result
//                Log.d("EyeDentify ImgUpload:", response.getName());
            }

            @Override
            public void imageRecognized(CloudSightResponse response) {
                Log.d("EyeDentify ", "imageRecognized");
//                statusTextView.setText("Status: " + response.getStatus());
//                resultTextView.setText("Result: " + response.getName());
                cloudSightResult = response.getName(); // return the string result
                Log.d("EyeDentify CSImage", response.getName());
            }

            // 7) Failure case
            @Override
            public void imageRecognitionFailed(String reason) {
                Log.d("EyeDentify CSFailed", "imageRecognitionFailed");
//                statusTextView.setText("Status: " + reason);
                Log.d("EyeDentify CSFailed", reason);

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("EyeDentify ", "onFailure");
//                statusTextView.setText("Status: " + throwable.getLocalizedMessage());
                Log.d("Mandy ", throwable.getLocalizedMessage());
            }
        });

        return cloudSightResult; // return the result of CloudSight call
    }

    public String getCloudSightResult(){
        return cloudSightResult;
    }
}
