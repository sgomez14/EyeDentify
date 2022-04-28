package com.example.eyedentify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MLKit {

    // 1) Call Google ML Kit TextRecognizer: https://developers.google.com/android/reference/com/google/mlkit/vision/text/TextRecognizer
    public static String getTextFromImage(Bitmap bitmap, Context context) {

        String textFromImage;

        // 2) Create an instance of TextRecognizer
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();

        // 3) Confirm TextRecognizer is operational, otherwise log error
        if (!textRecognizer.isOperational()) {
            Log.d("EyeDentify", "Error Occured");
            textFromImage = context.getString(R.string.TextRecognizerError);
        }
        else {
            // 4) Create an instance of Frame using bitmap
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            // 5) Detect frame and save it into a sparse array
            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frame);

            // 6) Append results to a string builder
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            // 7) Set string builder result to text view
            textFromImage = stringBuilder.toString();
        }

        return textFromImage;
    }
}
