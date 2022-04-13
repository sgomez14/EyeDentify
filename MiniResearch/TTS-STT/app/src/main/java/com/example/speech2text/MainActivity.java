package com.example.speech2text;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SpeechRecognizer spRecog;
    public static final Integer RecordAudioRequestCode = 1;

    private TextView tvBox;
    private TextView tvConf;
    private Button btnStart;
    private Boolean isListening  = false;

    private TextToSpeech tts;
    private EditText etSpeak;
    private Button btnSpeak;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        //intialize speech
        spRecog = SpeechRecognizer.createSpeechRecognizer(this);
        tvBox = (TextView) findViewById(R.id.tvBox);
        tvConf = (TextView) findViewById(R.id.tvConf);
        btnStart = (Button) findViewById(R.id.btnStart);
        etSpeak = (EditText) findViewById(R.id.etSpeak);
        btnSpeak = (Button) findViewById(R.id.btnSpeak);

        //create intent , starts an activity that will prompt the user for speech and send it through a speech recognizer
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //putdata for the speech model type and the language id tags i.e. en-US
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isListening){
                    //start listening to mic
                    spRecog.startListening(speechRecognizerIntent);
                    btnStart.setText("starting...");
                    isListening = true;
                }
                else {
                    //stop listening to mic
                    //The speech recognizer stops it self on a pause, so theres no need to stop listening.
                    spRecog.stopListening();
                    isListening = false;
                    btnStart.setText("reset");
                }
            }
        });

        //set listener for speech recognition
        spRecog.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                btnStart.setText("listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                btnStart.setText("stopping ...");

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //the spoken words get transcribed to the bundle
                //get the string from the bundle in the data
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                tvBox.setText(data.get(0));
                //confidence scores
                float[] conf = bundle.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
                Float score = conf[0];
                String str = score.toString();
                tvConf.setText(str);
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        //set listener for text to speech
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.getDefault());
                    tts.setPitch(2f);
                }

            }
        });

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tts.isSpeaking()){
                String speakTxt = etSpeak.getText().toString();
                //start speaking, queues the string and gets rid of previous string.
                tts.speak(speakTxt, TextToSpeech.QUEUE_FLUSH, null, null);
                }
                else {
                    tts.stop();
                }
            }
        });
    }
    //active permission request for recording audio
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

}