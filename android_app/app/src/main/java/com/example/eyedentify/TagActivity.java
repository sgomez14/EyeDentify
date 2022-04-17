package com.example.eyedentify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.audiofx.AudioEffect;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TagActivity extends AppCompatActivity {

    private Button btnPairTag;
    private EditText etDescription, etKeywords;
    private NFC nfc;
    PendingIntent pendingIntent;
    IntentFilter filters[];
    NfcAdapter adapter;
    boolean writeMode;
    TTS tts ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        btnPairTag = (Button) findViewById(R.id.btnPairTag);

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
        tts = new TTS(this);
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
                tts.startSpeaking(infoArray[1]);
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