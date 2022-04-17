package com.example.eyedentify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.audiofx.AudioEffect;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TagActivity extends AppCompatActivity {

    private Button btnPairTag, btnAddPhoto;
    private EditText etDescription, etKeywords;
    private NFC nfc;
    PendingIntent pendingIntent;
    IntentFilter filters[];
    NfcAdapter adapter;
    boolean writeMode;
    TTS tts ;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        btnPairTag = (Button) findViewById(R.id.btnPairTag);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        etDescription = findViewById(R.id.edtItemDescription);
        etKeywords = findViewById(R.id.edtItemKeywords);
        boolean gotHereWithTag;
        nfc = NFC.makeNFC(this);
        adapter = nfc.adapter;
        nfc.readIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        filters = new IntentFilter[]{tagDetected};
        tts = TTS.getInstanceOf(this);
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak("ready", TextToSpeech.QUEUE_FLUSH, null, null);

            }
        });
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR) {
//                    textToSpeech.setLanguage(Locale.CANADA);
//                }
                if (status == TextToSpeech.SUCCESS){
                    textToSpeech.setLanguage(Locale.US);
                    Toast.makeText(TagActivity.this, "called", Toast.LENGTH_SHORT).show();
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
                speakDescription.start();
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
                String msg = "img%"+etDescription.getText()+"%"+etKeywords.getText()+"%audio";
                try {
                    nfc.write(msg);
                    Toast.makeText(TagActivity.this, "Pair Success", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(TagActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        nfc.readIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            nfc.myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, nfc.myTagInfo, Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(MainActivity.this, TagActivity.class));
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
}