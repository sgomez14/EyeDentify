package com.example.eyedentify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TagActivity extends AppCompatActivity {

    private Button btnPairTag, btnAddPhoto, btnAddVoiceMemo;
    private EditText etDescription, etKeywords;
    private NFC nfc;
    PendingIntent pendingIntent;
    IntentFilter filters[];
    NfcAdapter adapter;
    boolean writeMode;
    TTS tts ;
    private TextToSpeech textToSpeech;
    private String mFileName;

    private MediaPlayer mPlayer;
    MediaRecorder mRecorder = new MediaRecorder();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        btnPairTag = (Button) findViewById(R.id.btnPairTag);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        btnAddVoiceMemo = findViewById(R.id.btnAddVoiceMemo);
        etDescription = findViewById(R.id.edtItemDescription);
        etKeywords = findViewById(R.id.edtItemKeywords);
        boolean gotHereWithTag;
        mFileName = "";
        nfc = NFC.makeNFC(this);
        adapter = nfc.adapter;
        nfc.readIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        filters = new IntentFilter[]{tagDetected};
        tts = TTS.getInstanceOf(this);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission(this);
        }

//        mr.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mr.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//        mr.setOutputFile(Environment.getExternalStorageDirectory() + File.separator
//                + Environment.DIRECTORY_DCIM + File.separator + "FILE_NAME.mp3");
        btnAddVoiceMemo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    // start recording.
                        startRecording();
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    // Stop recording and save file
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    return true;
                }
                return false;
            }
        });
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    textToSpeech.setLanguage(Locale.US);
                }

            }
        });
        //checking if arrived at this page with tag or with button
        if (getIntent().hasExtra("tagInfo")) {
            String message = getIntent().getExtras().getString("tagInfo");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            gotHereWithTag = true;
            //disable fields if with a tag
            etDescription.setEnabled(false);
            etKeywords.setEnabled(false);
            //info array, [0] = img, [1] = description, [2] = keywords, [3] = audio
            String[] infoArray = message.split("%");
            if(infoArray.length == 4){
                etDescription.setText(infoArray[1]);
                etKeywords.setText(infoArray[2]);
//                try {
//                    TimeUnit.SECONDS.sleep(2);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                textToSpeech.speak(infoArray[2], TextToSpeech.QUEUE_FLUSH, null, null);
                Thread speakDescription = new Thread(){
                    public void run(){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String speech = "You just came across "+infoArray[1]+
                                "and possible words are " + infoArray[2];
                        textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                };
                if(infoArray[3].equals("na")){
//                    speakDescription.start();
                }
                else{
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File musicDir = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                    File f = new File(musicDir, infoArray[3]+".mp3");
                    MediaPlayer mp = MediaPlayer.create(this, Uri.parse(f.getPath()));
                    mp.start();
                }
            }
            else{
                Toast.makeText(this, "Invalid Information in Tag", Toast.LENGTH_SHORT).show();
            }
        } else {
            gotHereWithTag = false;
            //enable fields if with a button
            etDescription.setEnabled(true);
            etKeywords.setEnabled(true);
        }



        btnPairTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "img%"+etDescription.getText()+"%"+etKeywords.getText()+"%"+(mFileName.equals("") ? "na" : mFileName);
                try {
                    nfc.write(msg);
                    Toast.makeText(TagActivity.this, "Pair Success", Toast.LENGTH_SHORT).show();
                    Toast.makeText(TagActivity.this, mFileName, Toast.LENGTH_SHORT).show();
                    mFileName = "";
                } catch (Exception e) {
                    Toast.makeText(TagActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private String getRecordingPath(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File musicDir = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        mFileName = UUID.randomUUID().toString();
        Toast.makeText(TagActivity.this, mFileName, Toast.LENGTH_SHORT).show();
        File f = new File(musicDir, mFileName+".mp3");
        return f.getPath();
    }

    private void startRecording() {
        // check permission method is used to check
        // that the user has granted permission
        // to record nd store the audio.
        if (CheckPermissions()) {

            // setbackgroundcolor method will change
            // the background color of text view.
            // we are here initializing our filename variable
            // with the path of the recorded audio file.

            // below method is used to initialize
            // the media recorder clss
            mRecorder = new MediaRecorder();

            // below method is used to set the audio
            // source which we are using a mic.
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            // below method is used to set
            // the output format of the audio.
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            // below method is used to set the
            // audio encoder for our recorded audio.
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // below method is used to set the
            // output file location for our recorded audio
            mRecorder.setOutputFile(getRecordingPath());
            try {
                // below method will prepare
                // our audio recorder class
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed " + e.getMessage());
            }
            // start method will start
            // the audio recording.
            mRecorder.start();
        } else {
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions();
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        nfc.readIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            nfc.myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(nfc.myTagInfo!= null){
                Toast.makeText(this, nfc.myTagInfo, Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        writeModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        writeModeOn();
    }

    private void writeModeOff(){
        writeMode = false;
        adapter.disableForegroundDispatch(this);
    }

    private void writeModeOn(){
        writeMode = true;
        adapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1  == PackageManager.PERMISSION_GRANTED && result2  == PackageManager.PERMISSION_GRANTED ;
    }
    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }


    private void checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},1);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }
}