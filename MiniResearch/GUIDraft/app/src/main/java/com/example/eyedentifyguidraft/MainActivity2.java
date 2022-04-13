package com.example.eyedentifyguidraft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity2 extends AppCompatActivity {

    private Button btnAddPhoto;
    private STT stt;
    private EditText edtItemDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);

        edtItemDescription = (EditText) findViewById(R.id.edtItemDescription);

        stt = new STT(this, edtItemDescription);


        // clicking this button simulates adding photo to tag data
        // new intent opens UI with simulates results of adding that photo
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TagActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        edtItemDescription.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                stt.startListen();
                return true;
            }
        });





    }
}