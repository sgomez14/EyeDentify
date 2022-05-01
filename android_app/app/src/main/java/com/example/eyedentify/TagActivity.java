package com.example.eyedentify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileOutputStream;
import java.util.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int IMAGE_CAPTURE_CODE = 101;

    Uri image_uri;
    private Boolean keep_image = false;
    private String cloudSightResult;
    private String mlkitResult;

    private CardView btnPairTag, btnAddVoiceMemo, btnAddPhoto;
    private EditText edtItemDescription, edtItemKeywords;
    private ImageView imgScannedItem;
    private NFC nfc;
    PendingIntent pendingIntent;
    IntentFilter filters[];
    NfcAdapter adapter;
    boolean writeMode;
    TTS tts ;
    private STT sttDesc;
    private STT sttKey;
    private TextToSpeech textToSpeech;
    private String mFileName, iFileName;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private MediaPlayer mPlayer;
    MediaRecorder mRecorder = new MediaRecorder();

    private Animation button_anim;


    @SuppressLint({"WrongThread", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        sp = getSharedPreferences("eyedentify", Context.MODE_PRIVATE);
        editor = sp.edit();
        btnPairTag = findViewById(R.id.cardViewEditTag);
        btnAddVoiceMemo = findViewById(R.id.cardViewAddVoiceMemo);
        btnAddPhoto = findViewById(R.id.cardViewAddPhoto);
        edtItemDescription = findViewById(R.id.edtItemDescription);
        edtItemKeywords = findViewById(R.id.edtItemKeywords);
        imgScannedItem = findViewById(R.id.imgScannedItem);
        button_anim = AnimationUtils.loadAnimation(this, R.anim.button_anim);

        // initialize image file name and audio file name to emtpty,
        mFileName = "";
        iFileName = "";
        imgScannedItem.setVisibility(View.GONE);

        // fill in from sharedpreference if available
        if(sp.contains("audioPath"))
            mFileName = sp.getString("audioPath", null);
        if(sp.contains("imgPath"))
            iFileName = sp.getString("imgPath", null);
        // initialize an NFC object
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
        //create text to speech instance
        tts = TTS.getInstanceOf(this);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission(this);
        }

        //init Speech to text
        sttDesc = new STT(this, edtItemDescription);
        sttKey = new STT(this, edtItemKeywords);

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
                        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(300);
                        startRecording();
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    btnAddVoiceMemo.startAnimation(button_anim);
                    // Stop recording and save file
                    try{
                        //routines to stop recording and release the recorder object
                        mRecorder.stop();
                        mRecorder.release();
                        mRecorder = null;
                        //have the phone vibrate a little bit to confirm the recording has stopped
                        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(300);
                        Toast.makeText(TagActivity.this, R.string.record_complete, Toast.LENGTH_SHORT).show();
                        return true;
                    } catch (Exception e){
                        Toast.makeText(TagActivity.this, R.string.record_error, Toast.LENGTH_SHORT).show();
                        mFileName = "";
                    }

                }
                return false;
            }
        });

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddPhoto.startAnimation(button_anim);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED
                    ){
                        String[] permission = {Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else{
                        openCamera();
                    }
                } else{
                    openCamera();
                }
            }
        });
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    textToSpeech.setLanguage(Locale.getDefault());
                }

            }
        });

        //PARSING TAG MESSAGE HERE
        //checking if arrived at this page with tag or with button
        if (getIntent().hasExtra("tagInfo") && sp.contains(getIntent().getExtras().getString("tagInfo"))) {
            String message = sp.getString(getIntent().getExtras().getString("tagInfo"), null);

            //info array, [0] = img, [1] = description+keywords, [2] = audio
            String[] infoArray = message.split("%");

            if(infoArray.length == 3){ //message is parsable
                if(!infoArray[0].equals(Utilities.NOT_APPLICABLE)){ //image is not null, store image file state
                    iFileName = infoArray[0];
                    editor.putString("imgPath", iFileName);
                    editor.commit();
                }
                String[] info = sp.getString(infoArray[1], null).split("%%%");
                if(info.length == 2){//contains both description and keywords
                    edtItemDescription.setText(info[0]);
                    edtItemKeywords.setText(info[1]);
                }else if(info.length == 1){ //contains only keywords
                    edtItemDescription.setText(info[0]);
                }
                if(!infoArray[0].equals(Utilities.NOT_APPLICABLE)){ //image is not null
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File imgDir = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    String imgFileName = imgDir+"/"+infoArray[0]+".png";
                    imgScannedItem.setVisibility(View.VISIBLE);
                    imgScannedItem.setImageBitmap(BitmapFactory.decodeFile(imgFileName));
                }
                if(!infoArray[2].equals(Utilities.NOT_APPLICABLE)){ //voice memo is not null
                    mFileName = infoArray[2];
                }
            }
            else{
                Toast.makeText(this, R.string.invalid_in_tag, Toast.LENGTH_SHORT).show();
            }
        }
        else if (getIntent().hasExtra("cameraResults")){ // check if intent generated by image recognition results
            Bundle imageResults = getIntent().getExtras();
            String cloudSightResult = imageResults.getString("cloudSightResult");
            String mlkitResult = imageResults.getString("mlkitResult");
            String imageFile = imageResults.getString("imageFile");
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile);
            imageBitmap = TagActivity.rotateBitmap90(imageBitmap); // rotate the bitmap 90 degrees

            edtItemDescription.setText(cloudSightResult); // CloudSight provides descriptive sentence
            edtItemKeywords.setText(mlkitResult); // MLKit provides words detect on the object
            imgScannedItem.setVisibility(View.VISIBLE);
            imgScannedItem.setImageBitmap(imageBitmap);
            try (FileOutputStream out = new FileOutputStream(getImagePath())) {
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (IOException e) {
                Toast.makeText(this, "File Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }



        btnPairTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPairTag.startAnimation(button_anim);
                if(edtItemDescription.getText().toString().isEmpty() && mFileName.equals("")){
                    Toast.makeText(TagActivity.this, R.string.please_fill_either, Toast.LENGTH_SHORT).show();
                    return;
                }
                keep_image = true;
                //generate a unique id as key for the description and keywords
                String unique = UUID.randomUUID().toString();
                //put it into the map
                editor.putString(unique, edtItemDescription.getText()+Utilities.TXT_SEPARATOR+edtItemKeywords.getText());
                editor.commit();
                String msg = (iFileName.equals("") ? Utilities.NOT_APPLICABLE : iFileName) +
                        Utilities.MSG_SEPARATOR + unique+ Utilities.MSG_SEPARATOR +
                        (mFileName.equals("") ? Utilities.NOT_APPLICABLE : mFileName);
                String u = UUID.randomUUID().toString();
                editor.putString(u, msg);
                editor.commit();
                //uncomment the lines below to go to listening and tagging activity
                //=================================================================
                editor.remove("audioPath");
                editor.remove("imgPath");
                editor.commit();
                startActivity(new Intent(TagActivity.this, NFCPairingActivity.class).putExtra("tagInfo", u));
                //=================================================================
                //uncomment the lines below to write to tag directly
                //=================================================================
//                try {
//                    nfc.write(msg);
//                    Toast.makeText(TagActivity.this, "Write Success", Toast.LENGTH_SHORT).show();
//                    mFileName = "";
//                } catch (Exception e) {
//                    Toast.makeText(TagActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
                //=================================================================
            }
        });
        //Speech to text listeners
        edtItemDescription.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                sttDesc.startListen();
                return true;
            }
        });
        edtItemKeywords.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                sttKey.startListen();
                return true;
            }
        });
    }

    private String getRecordingPath(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File musicDir = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        mFileName = UUID.randomUUID().toString();
        editor.putString("audioPath", mFileName);
        editor.commit();
        File f = new File(musicDir, mFileName+".mp3");
        return f.getPath();
    }

    private String getImagePath(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File imgDir = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        iFileName = UUID.randomUUID().toString();
        editor.putString("imgPath", iFileName);
        editor.commit();
        File f = new File(imgDir, iFileName+".png");
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
                Toast.makeText(this, R.string.go_back_page, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        writeModeOff();
        //the only way to keep image is to pair with tag
        if(!keep_image){
            deleteImage();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, R.string.perm_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 4) After photo is taken, come back to the app
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Confirm requestCode and resultCode are valid
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            try{
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {

                        // onPreExecute Method
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent loadResults = new Intent(TagActivity.this, ImageRecognitionPendingActivity.class);
                                startActivity(loadResults);
                            }
                        });

                        // convert image to File image recognition sdks
                        File imageFile = new File(getPath(image_uri));

                        // convert imageFile to bitmap for ML kit
                        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.toString());
                        imageBitmap = rotateBitmap90(imageBitmap);
                        // pass image bitmap to MLKit class
                        mlkitResult = MLKit.getTextFromImage(imageBitmap, TagActivity.this); // pass image to MLKit

                        // pass image file to CloudSight class
                        new CloudSight(TagActivity.this, mlkitResult, imageFile);

                    }
                });
            }
            catch (Exception e) {
                Log.d("EyeDentify debug", e.getMessage());
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;

    }

    private void openCamera() {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From The Camera");
        //set folder for image
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/" + getResources().getString(R.string.app_name));

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }


    public static Bitmap rotateBitmap90 (Bitmap imageBitmap){
        Matrix matrixForRotation = new Matrix();
        matrixForRotation.postRotate(90); // assuming rotating in clockwise direction
        Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0,0, imageBitmap.getWidth(),
                imageBitmap.getHeight(), matrixForRotation, true);
        return rotatedBitmap;
    }

    private void deleteImage(){
//        File img = new File(getImagePath());
//        if(img.exists()){
//            getContentResolver().delete(Uri.fromFile(img),null, null);
//        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editor.remove("audioPath");
        editor.remove("imgPath");
        editor.commit();
        launch_main_activity();
    }

    public void launch_main_activity() {
        Intent main_activity = new Intent(getApplicationContext(), MainActivity.class);
        //put user data in bundle here, if we do anything with user data
        startActivity(main_activity);
        finish();
    }

}