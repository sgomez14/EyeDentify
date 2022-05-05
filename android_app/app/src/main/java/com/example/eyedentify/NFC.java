package com.example.eyedentify;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;

import java.nio.charset.StandardCharsets;

public class NFC {
    NfcAdapter adapter; //an adapter is necessary
    Tag myTag;          //tag object
    String myTagInfo;   //info inside the tag
    Context context;    //context of the activity where this nfc object is instantiated at

    public static NFC makeNFC(Context c){
        return new NFC(c);
    }
    public NFC(Context c){
        adapter = NfcAdapter.getDefaultAdapter(c);
        //getting adapter. If it is null, then that means this device does not support NFC
        context = c;
    }

    void readIntent(Intent in) {
        String action = in.getAction();
        // put log to see what this action is
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
            myTagInfo = getTagViews(ndefMsgs);
        }
    }

    // return the actual text from the ndef message in the tag
    public String getTagViews(NdefMessage[] m){
        if(m == null || m.length == 0) return null;
        String text = "";
        byte[] payload = (m.length > 1) ? m[1].getRecords()[0].getPayload() : m[0].getRecords()[0].getPayload();
        String textEncoding = "UTF-8";
        int len = payload[0] & 0063;

        try{
            text = new String(payload, len+1, payload.length-len-1, textEncoding);
        } catch (Exception e) {
        }
        return text;
    }

    //write to the tag
    public void write(String text) throws Exception{
        NdefRecord[] records = {createRecord(text), NdefRecord.createApplicationRecord("com.example.eyedentify") };
        NdefMessage m = new NdefMessage(records);
        Ndef ndef = Ndef.get(myTag);
        ndef.connect();
        ndef.writeNdefMessage(m);
        ndef.close();
    }

    //create ndef record
    public NdefRecord createRecord(String text){
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes(StandardCharsets.US_ASCII);
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[langLength+textLength+1];

        payload[0] = (byte) langLength;

        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1+langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
    }



}
