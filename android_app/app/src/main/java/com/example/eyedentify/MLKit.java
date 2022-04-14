package com.example.eyedentify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MLKit {

    public static final String TextRecognizerError = "MLKit: Error TextRecognizer Not Operational";


    // 5) Call Google ML Kit TextRecognizer: https://developers.google.com/android/reference/com/google/mlkit/vision/text/TextRecognizer
    public String getTextFromImage(Bitmap bitmap, Context context) {

        String textFromImage = "";

        // 6) Create an instance of TextRecognizer
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();

        // 7) Confirm TextRecognizer is operational, otherwise log error
        if (!textRecognizer.isOperational()) {
            Log.d("Mandy", "Error Occured");

            textFromImage = TextRecognizerError;
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
            textFromImage = stringBuilder.toString();
        }

        return textFromImage;
    }
}
