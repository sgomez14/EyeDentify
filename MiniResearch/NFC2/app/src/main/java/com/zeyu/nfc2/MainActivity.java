package com.zeyu.nfc2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private final String ERR_No_Tag = "No Tag Detected";
    private final String SUCC_W = "Write Success";
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter filters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    private TextView TV_content;
    private EditText ET_message;
    private Button BTN_write;
    private Thread tagDetection;
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TV_content = findViewById(R.id.TV_content);
        TV_content.setText("[Tag Content]");
        ET_message = findViewById(R.id.ET_message);
        BTN_write = findViewById(R.id.BTN_write);
        context = this;
        BTN_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (myTag == null){ //if no tag is present
                        Toast.makeText(MainActivity.this, ERR_No_Tag, Toast.LENGTH_SHORT).show();
                    }
                    else{ //tag is there
                        write(""+ET_message.getText(), myTag);
                        Toast.makeText(MainActivity.this, SUCC_W, Toast.LENGTH_SHORT).show();
                        ET_message.setText("");
                    }
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter = NfcAdapter.getDefaultAdapter(this);
        if(adapter == null){ //adapter will only be null if the device does not support NFC at all
            Toast.makeText(MainActivity.this, "Unsupported Device", Toast.LENGTH_SHORT).show();
            finish();
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        filters = new IntentFilter[]{tagDetected};
        tagDetection = new Thread(){ //read what's inside the tag and display tag content on screen
            public void run(){
                readIntent(getIntent());
            }
        };
        tagDetection.start();
    }

    private void readIntent(Intent in) {
        String action = in.getAction();
        // put log to see what this action is
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] msgs = in.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] ndefMsgs = null;
            if (msgs != null) {
                ndefMsgs = new NdefMessage[msgs.length]; //make message
                for (int i = 0; i < msgs.length; i++) {
                    ndefMsgs[i] = (NdefMessage) msgs[i];
                }
            }
            buildTagViews(ndefMsgs); //decode the NdefMessage and display it on a TextView
        }

    }

    private void buildTagViews(NdefMessage[] m){
        if(m == null || m.length == 0) return;

        String text = "";
        byte[] payload = (m.length > 1) ? m[1].getRecords()[0].getPayload() : m[0].getRecords()[0].getPayload();
        String textEncoding = "UTF-8"; //The encoding method we use for the NDEF message
        int len = payload[0] & 0063;
        try{
            text = new String(payload, len+1, payload.length-len-1, textEncoding);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        TV_content.setText(text);
    }

    private void write(String text, Tag tag) throws Exception{
        NdefRecord[] records = {createRecord(text), //first element is the actual message
                NdefRecord.createApplicationRecord("com.zeyu.nfc2"), }; //second element is an application record which tells what app to open upon detection of the tag
        NdefMessage m = new NdefMessage(records);

        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(m);
        ndef.close();
    }

    private NdefRecord createRecord(String text){ //convert text to bytes and make it into a NdefRecord
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes(StandardCharsets.US_ASCII);
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[langLength+textLength+1];

        payload[0] = (byte) langLength;

        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1+langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], payload);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        readIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
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