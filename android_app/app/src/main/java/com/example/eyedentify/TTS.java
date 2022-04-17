package com.example.eyedentify;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TTS {
    private TextToSpeech textToSpeech;

    //use this constructor
    public TTS(Context context){
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }

            }
        });

    }
    //starts speech input string
    public void startSpeaking(String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    //stops speech early
    public void stopSpeaking(){
        textToSpeech.stop();
    }
}
