package com.zeyu.nfc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
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
//    private final String ERR_WR = "Error Writing/Reading";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TV_content = findViewById(R.id.TV_content);
        ET_message = findViewById(R.id.ET_message);
        BTN_write = findViewById(R.id.BTN_write);
        context = this;
        BTN_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 try{
                     if (myTag == null){
                         Toast.makeText(MainActivity.this, ERR_No_Tag, Toast.LENGTH_SHORT).show();
                     }
                     else{
                         write("Text:" + ET_message.getText(), myTag);
                         Toast.makeText(MainActivity.this, SUCC_W, Toast.LENGTH_SHORT).show();
                     }
                 }catch (Exception e){
                     Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                 }
            }
        });
//        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
//        adapter = manager.getDefaultAdapter();
        adapter = NfcAdapter.getDefaultAdapter(this);
        if(adapter == null){
            Toast.makeText(MainActivity.this, "Device does not support NFC", Toast.LENGTH_SHORT).show();
            finish();
        }
        readIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        filters = new IntentFilter[]{tagDetected};
    }

    private void readIntent(Intent in) {
        String action = in.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] msgs = in.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] ndefMsgs = null;
            if (msgs != null) {
                ndefMsgs = new NdefMessage[msgs.length];
                for (int i = 0; i < msgs.length; i++) {
                    ndefMsgs[i] = (NdefMessage) msgs[i];
                }
            }
            buildTagViews(ndefMsgs);
        }
    }

    private void buildTagViews(NdefMessage[] m){
        if(m == null || m.length == 0) return;

        byte[] payload = m[0].getRecords()[0].getPayload();
        int len = payload[0] & 0063;
        String text = new String(payload, len+1, payload.length-len-1, StandardCharsets.UTF_8);
        TV_content.setText(text);
    }

    private void write(String text, Tag tag) throws Exception{
        NdefRecord[] records = {createRecord(text)};
        NdefMessage m = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(m);
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException{
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[langLength+textLength+1];
        payload[0] = (byte) langLength;
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1+langLength, textLength);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
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