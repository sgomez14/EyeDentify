package ai.cloudsight.demoapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

import ai.cloudsight.androidsdk.CloudSightClient;
import ai.cloudsight.androidsdk.CloudSightCallback;
import ai.cloudsight.androidsdk.CloudSightResponse;

public class MainActivity extends AppCompatActivity {

    TextView statusTextView;
    TextView resultTextView;
    private static final int PICK_FROM_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button galleryButton = findViewById(R.id.pick_gallery);
        statusTextView = findViewById(R.id.recogntion_status);
        resultTextView = findViewById(R.id.recogntion_result);

        // 1) Gallery button listener to start gallery intent
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.ACTION_GET_CONTENT, true);
                startActivityForResult(intent, PICK_FROM_GALLERY);
            }
        });
    }

    // 2) After photo is selected, come back to the app
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Confirm requestCode and resultCode are valid
        if (requestCode == PICK_FROM_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                // Get picture data and save as Uri format
                Uri selectedImage = data.getData();
                uploadImageRequest(new File(getPath(selectedImage)));
            }
        }
    }

    // 4) Upload the image request to CloudSight API
    private void uploadImageRequest(File file) {
        // 5) Initiate client with key pass to connect to ClousSight API
        CloudSightClient client = new CloudSightClient().init("iTabmDiyViBULrkoBBCVHA");

        // 6) Set client region to USA
        client.setLocale("en-US");
        client.setNsfw(true);

        //To test on emulator, uncomment one of the url below and change line 73 to "client.getImageInformation(url, new CloudSightCallback().."
        String url = "https://www.thesprucepets.com/thmb/63v75VJoGecxMB4re_KfA4GYZwc=/2400x2400/smart/filters:no_upscale()/popular-small-bird-species-390926-hero-d3d0af7bb6ed4947b0c3c5afb4784456.jpg";
        //String url = "https://cdn-prod.medicalnewstoday.com/content/images/articles/267/267290/a-woman-eating-a-green-apple.jpg";

        // 7) Analyze photo using API
        client.getImageInformation(url, new CloudSightCallback() {
            @Override
            // 1) Successful case
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

            // 2) Failure case
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

    // 3) Convert path of the photo uri to a string
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }
}
