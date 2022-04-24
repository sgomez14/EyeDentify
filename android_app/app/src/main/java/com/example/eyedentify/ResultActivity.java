package com.example.eyedentify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private CardView btnPlayVoiceMemo, btnEditTag;
    private NFC nfc;
    PendingIntent pendingIntent;
    IntentFilter filters[];
    NfcAdapter adapter;
    boolean writeMode;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TextView edtItemDescription, edtItemKeywords;
    private TextToSpeech textToSpeech;
    private ImageView imgScannedItem;
    Thread speakDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        sp = getSharedPreferences("eyedentify", Context.MODE_PRIVATE);
        editor = sp.edit();
        btnPlayVoiceMemo = findViewById(R.id.cardViewPlayVoiceMemo);
        btnEditTag = findViewById(R.id.cardViewEditTagResults);
        edtItemDescription = findViewById(R.id.edtItemDescription);
        edtItemKeywords = findViewById(R.id.edtItemKeywords);
        imgScannedItem = findViewById(R.id.imgScannedItem);

        //TODO: add action that plays the voice memo
        btnPlayVoiceMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        btnEditTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this, TagActivity.class).putExtra("tagInfo", getIntent().getExtras().getString("tagInfo")));
            }
        });
        nfc = NFC.makeNFC(this);
        adapter = nfc.adapter;
        nfc.readIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        filters = new IntentFilter[]{tagDetected};
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        if (getIntent().hasExtra("tagInfo") && sp.contains(getIntent().getExtras().getString("tagInfo"))) {
            String message = sp.getString(getIntent().getExtras().getString("tagInfo"), null);
            //info array, [0] = img, [1] = description+keywords, [2] = audio
            String[] infoArray = message.split("%");
            speakDescription = new Thread(){
                public void run(){}
            };
            if(infoArray.length == 3){
                String[] info = sp.getString(infoArray[1], null).split("%%%");
                if(info.length == 2){
                    edtItemDescription.setText(info[0]);
                    edtItemKeywords.setText(info[1]);
                    speakDescription = new Thread(){
                        public void run(){
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            String speech = "You just came across "+info[0]+
                                    ", and possible words are " + info[1];
                            textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    };
                }else if (info.length == 1){
                    edtItemDescription.setText(info[0]);
                    speakDescription = new Thread(){
                        public void run(){
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            String speech = "You just came across "+info[0];
                            textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    };
                }

                if(infoArray[0].equals("na")){
                    //TODO: default image
                }
                else{
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File imgDir = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                    File f = new File(imgDir, infoArray[0]+".png");
                    String imgFileName = imgDir+"/"+infoArray[0]+".png";
                    imgScannedItem.setImageBitmap(BitmapFactory.decodeFile(imgFileName));
                }
                if(infoArray[2].equals("na")){
                    speakDescription.start();
                }
                else{
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File musicDir = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                    File f = new File(musicDir, infoArray[2]+".mp3");
                    MediaPlayer mp = MediaPlayer.create(this, Uri.parse(f.getPath()));
                    mp.start();
                }
            }
            else{
                Toast.makeText(this, "Likely missing files", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Cannot read from tag", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        nfc.readIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            nfc.myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(nfc.myTagInfo != null && nfc.myTagInfo.split("%").length == 3){
                startActivity(new Intent(ResultActivity.this, ResultActivity.class).putExtra("tagInfo", nfc.myTagInfo));
            }
            else{
                Toast.makeText(this, "Cannot Parse Information in Tag", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        launch_main_activity();
    }

    public void launch_main_activity(){
        Intent main_activity = new Intent(getApplicationContext(), MainActivity.class);
        //put user data in bundle here, if we do anything with user data
        startActivity(main_activity);
        finish();
    }

}