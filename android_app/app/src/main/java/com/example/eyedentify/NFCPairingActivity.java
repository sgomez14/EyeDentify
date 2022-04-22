package com.example.eyedentify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

public class NFCPairingActivity extends AppCompatActivity {

    private NFC nfc;
    PendingIntent pendingIntent;
    IntentFilter filters[];
    NfcAdapter adapter;
    boolean writeMode;
    String uniqueIdToSPStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        if (getIntent().hasExtra("tagInfo")) {
            uniqueIdToSPStorage = getIntent().getExtras().getString("tagInfo");
            Toast.makeText(this, uniqueIdToSPStorage, Toast.LENGTH_SHORT).show();
        }
        nfc = NFC.makeNFC(this);
        adapter = nfc.adapter;
//        nfc.readIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        filters = new IntentFilter[]{tagDetected};
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        nfc.readIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            nfc.myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(nfc.myTag != null){
//                startActivity(new Intent(ResultActivity.this, ResultActivity.class).putExtra("tagInfo", nfc.myTagInfo));
                try {
                    Toast.makeText(this, uniqueIdToSPStorage, Toast.LENGTH_LONG).show();
                    nfc.write(uniqueIdToSPStorage);
                    startActivity(new Intent(NFCPairingActivity.this, ResultActivity.class).putExtra("tagInfo", uniqueIdToSPStorage));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            else{
                Toast.makeText(this, "Null tag", Toast.LENGTH_SHORT).show();
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
        //does nothing on back press
    }
}