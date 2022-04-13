package com.example.mlkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity {

    private Button btnCapture, btnCopy;
    private TextView txtData;
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int REQUEST_CAPTURE_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declaration of views
        btnCapture = findViewById(R.id.btnCapture);
        btnCopy = findViewById(R.id.btnCopy);
        txtData = findViewById(R.id.txtData);

        // 1) Request camera access permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

        // 2) Capture button listener to open camera
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the camera by creating an Intent object
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAPTURE_CODE);
            }
        });

        // 3) Copy button listener to copy text to system
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = txtData.getText().toString();
                copyToClipboard(text);
            }
        });
    }

    // 4) After photo is taken, come back to the app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Confirm requestCode and resultCode are valid
        if (requestCode == REQUEST_CAPTURE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get picture data and save as Uri format
                Uri resultUri = data.getData();
                try {
                    // Convert picture content to bitmap format
                    // Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    
                    // To test on emulator, comment above line and uncomment line below for hardcoded image
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_with_words);
                    getTextFromImage(bitmap);
                } catch (Exception e) {
                    Log.d("Mandy", e.getMessage());
                }
            }
        }
    }

    // 5) Call Google ML Kit TextRecognizer: https://developers.google.com/android/reference/com/google/mlkit/vision/text/TextRecognizer
    private void getTextFromImage(Bitmap bitmap) {
        // 6) Create an instance of TextRecognizer
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

        // 7) Confirm TextRecognizer is operational, otherwise log error
        if (!textRecognizer.isOperational()) {
            Log.d("Mandy", "Error Occured");
        }
        else {
            // 8) Create an instance of Frame using bitmap
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            // 9) Detect frame and save it into a sparse array
            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frame);

            // 10) Append results to a string builder
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            // 11) Set string builder result to text view
            txtData.setText(stringBuilder.toString());
            btnCapture.setText("Retake");
            btnCopy.setVisibility(View.VISIBLE);
        }
    }

    // 12) Copy text to system
    private void copyToClipboard(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied data", text);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}