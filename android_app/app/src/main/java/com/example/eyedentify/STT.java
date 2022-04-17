package com.example.eyedentify;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class STT {
    private SpeechRecognizer spRecog;
    private final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    private static final Integer RecordAudioRequestCode = 1;
    private String most_recent_string;

    //use this constructor
    public STT(Activity activity, EditText et){
        initSTT(activity, et);
    }


    private void initSTT(Activity activity, EditText et){
        //get permissions
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission(activity);
        }
        //intialize speech
        spRecog = SpeechRecognizer.createSpeechRecognizer(activity);

        //create intent , starts an activity that will prompt the user for speech and send it through a speech recognizer
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //putdata for the speech model type and the language id tags i.e. en-US
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        //set listener for speech recognition
        spRecog.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //the spoken words get transcribed to the bundle
                //get the string from the bundle in the data
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                //the transcribed string is first element in data
                et.setText(data.get(0));


                //confidence scores
                float[] conf = bundle.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
                Float score = conf[0];
                String scr = score.toString();
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

    }

    //active permission request for recording audio
    private void checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    //will record and transcribe audio and the string data can be retrived from getMostRecentString
    public void startListen(){
        spRecog.startListening(speechRecognizerIntent);
    }

}
