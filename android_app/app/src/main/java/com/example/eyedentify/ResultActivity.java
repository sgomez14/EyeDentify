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
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    Handler handler;
    MediaPlayer mp;

    private Animation button_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        sp = getSharedPreferences("eyedentify", Context.MODE_PRIVATE);
        editor = sp.edit();
        btnPlayVoiceMemo = findViewById(R.id.cardViewPlayVoiceMemo);
        btnEditTag = findViewById(R.id.cardViewEditTagResults);
        edtItemDescription = findViewById(R.id.edtItemDescription);
        edtItemDescription.setMovementMethod(new ScrollingMovementMethod());
        edtItemKeywords = findViewById(R.id.edtItemKeywords);
        edtItemKeywords.setMovementMethod(new ScrollingMovementMethod());
        imgScannedItem = findViewById(R.id.imgScannedItem);
        button_anim = AnimationUtils.loadAnimation(this, R.anim.button_anim);
        btnPlayVoiceMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPlayVoiceMemo.startAnimation(button_anim);
                try{
                    if (getIntent().hasExtra("tagInfo") && sp.contains(getIntent().getExtras().getString("tagInfo"))) {
                        //get the message from sharedpreference using the key
                        String message = sp.getString(getIntent().getExtras().getString("tagInfo"), null);
                        //split the message into image, text and audio
                        String[] infoArray = message.split(Utilities.MSG_SEPARATOR);
                        //if message is indeed the format we constructed and there is a audio memo, retrieve the audio memo and play it
                        if (infoArray.length == Utilities.MESSAGE_FULL_LENGTH && !infoArray[2].equals(Utilities.NOT_APPLICABLE)) {
                            ContextWrapper cw = new ContextWrapper(getApplicationContext());
                            File musicDir = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                            File f = new File(musicDir, infoArray[2] + ".mp3");
                            if(mp != null && mp.isPlaying()) mp.stop();
                            mp = MediaPlayer.create(ResultActivity.this, Uri.parse(f.getPath()));
                            mp.start();
                        }
                    }
                }catch(Exception e){
                    Toast.makeText(ResultActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnEditTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEditTag.startAnimation(button_anim);
                startActivity(new Intent(ResultActivity.this, TagActivity.class).putExtra("tagInfo", getIntent().getExtras().getString("tagInfo")));
            }
        });
        nfc = NFC.makeNFC(this);
        //make an adapter for nfc to read and write
        adapter = nfc.adapter;
        //try to read once to see if there's a tag
        nfc.readIntent(getIntent());
        //this MUTABLE intent flag will allow the tag information to be actually modified.
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        //will handle NFC Tag discovered intents
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
            String[] infoArray = message.split(Utilities.MSG_SEPARATOR);
            speakDescription = new Thread(){
                public void run(){}
            };
            //
            if(infoArray.length == Utilities.MESSAGE_FULL_LENGTH){ //message is parsable
                String[] info = sp.getString(infoArray[1], null).split(Utilities.TXT_SEPARATOR);
                if(info.length == Utilities.DESCRIPTION_AND_KEYWORDS){ //there are both description and keywords available
                    edtItemDescription.setText(info[0]); //set description text
                    edtItemKeywords.setText(info[1]); //set keywords text
                    speakDescription = new Thread(){
                        public void run(){
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            String speech = getResources().getString(R.string.you_just) + info[0]+
                                    getResources().getString(R.string.possible_words) + info[1];
                            textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    };
                }else if (info.length == Utilities.JUST_DESCRIPTION){ //just description available
                    edtItemDescription.setText(info[0]); //set description text
                    speakDescription = new Thread(){
                        public void run(){
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            String speech = getResources().getString(R.string.you_just)+info[0];
                            textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    };
                }

                // Hide memo button when there is no memo
                if(infoArray[2].equals(Utilities.NOT_APPLICABLE)){
                    btnPlayVoiceMemo.setVisibility(View.GONE);
                }

                //if there is an image available, retrieve it and set it to screen
                if(!infoArray[0].equals(Utilities.NOT_APPLICABLE)){
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File imgDir = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                    File f = new File(imgDir, infoArray[0]+".png");
                    String imgFileName = imgDir+"/"+infoArray[0]+".png";
                    imgScannedItem.setImageBitmap(BitmapFactory.decodeFile(imgFileName));
                }
                if(infoArray[2].equals(Utilities.NOT_APPLICABLE)){//if no audio memo available
                    //if no audio memo available
                    speakDescription.start();
                }
            }
            else{
                Toast.makeText(this, R.string.missing_file, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, R.string.cannot_read, Toast.LENGTH_SHORT).show();
        }

    }

    /*
     Gets triggered whenever a tag gets read
     fills in the tag information for nfc instance
     gets to result page with the tag information
    */
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        nfc.readIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){ //if has a tag intent
            nfc.myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(nfc.myTagInfo != null){
                //if there is something in the tag, open result page again with new information discovered in tag
                editor.remove("audioPath");
                editor.remove("imgPath");
                editor.commit();
                startActivity(new Intent(ResultActivity.this, ResultActivity.class).putExtra("tagInfo", nfc.myTagInfo));
            }
            else{
                Toast.makeText(this, R.string.cannot_parse, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    NFC write mode off when paused
    */
    @Override
    public void onPause(){
        super.onPause();
        writeModeOff();
        if(mp != null && mp.isPlaying())
            mp.stop();
        if(textToSpeech != null && textToSpeech.isSpeaking())
            textToSpeech.stop();

    }

    /*
    NFC write mode on when paused
    */
    @Override
    public void onResume(){
        super.onResume();
        writeModeOn();
    }

    /*
    Disabling write mode for NFC
    */
    private void writeModeOff(){
        writeMode = false;
        adapter.disableForegroundDispatch(this);
    }

    /*
    Enabling write mode for NFC
    */
    private void writeModeOn(){
        writeMode = true;
        adapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    /*
    Back press brings to previous activity (MainActivity)
    */
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