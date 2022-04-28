package com.example.eyedentify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

public class NFCPairingActivity extends AppCompatActivity {

    private NFC nfc;
    PendingIntent pendingIntent;
    IntentFilter filters[];
    NfcAdapter adapter;
    boolean writeMode;
    String uniqueIdToSPStorage;
    CountDownTimer countDownTimer;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    long timer = 5000;
    long interval = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        sp = getSharedPreferences("eyedentify", Context.MODE_PRIVATE);
        editor = sp.edit();

        // A toast to warn users that every tag will get overwritten
        Toast.makeText(this, R.string.warning_overwrite, Toast.LENGTH_SHORT).show();

        // Get unique id from storage
        if (getIntent().hasExtra("tagInfo")) {
            uniqueIdToSPStorage = getIntent().getExtras().getString("tagInfo");
        }


        // initialize an NFC object
        nfc = NFC.makeNFC(this);

        //make an adapter for nfc to read and write
        adapter = nfc.adapter;

        //this MUTABLE intent flag will allow the tag information to be actually modified.
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);

        //will handle NFC Tag discovered intents
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        filters = new IntentFilter[]{tagDetected};

        /*
        30 seconds timer for pairing activity,
        once timer is up and there is no tag scanned, go back to previous activity (TagActivity)
        */
        countDownTimer = new CountDownTimer(timer, interval) {

            /*
            onTick debugger to count down the time
            */
            public void onTick(long millisUntilFinished) {
                Log.d("Mandy", ""+millisUntilFinished/1000);
            }

            /*
            when timer is finished and tag is null, go to TagActivity
             */
            public void onFinish() {
                if (nfc.myTag == null) {
                    onBackPressed();
                }
            }
        }.start();
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
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            nfc.myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(nfc.myTag != null){
                try {
                    nfc.write(uniqueIdToSPStorage);
                    Toast.makeText(this, R.string.write_success, Toast.LENGTH_SHORT).show();
                    countDownTimer.cancel();
                    startActivity(new Intent(NFCPairingActivity.this, ResultActivity.class).putExtra("tagInfo", uniqueIdToSPStorage));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            else{
                Toast.makeText(this, R.string.incorrect_response, Toast.LENGTH_SHORT).show();
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
    Back press brings to previous activity (TagActivity)
    */
    @Override
    public void onBackPressed() {
        //does nothing on back press
        if (getIntent().hasExtra("tagInfo")) {
            countDownTimer.cancel();
            editor.remove("audioPath");
            editor.remove("imgPath");
            editor.commit();
            startActivity(
                    new Intent(NFCPairingActivity.this, TagActivity.class).putExtra(
                            "tagInfo", getIntent().getExtras().getString("tagInfo"))
            );
        }
    }
}