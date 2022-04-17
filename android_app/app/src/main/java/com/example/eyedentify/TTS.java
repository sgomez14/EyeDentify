package com.example.eyedentify;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TTS {
    private TextToSpeech textToSpeech;

    public static TTS T;

    public static TTS getInstanceOf(Context c){
        if(T == null){
            T = new TTS(c);
        }
        return T;
    }

    //use this constructor
    public TTS(Context context){
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR) {
//                    textToSpeech.setLanguage(Locale.CANADA);
//                }
                if (status == TextToSpeech.SUCCESS){
                    textToSpeech.setLanguage(Locale.US);
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
