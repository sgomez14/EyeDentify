package com.example.eyedentify;

import android.content.Context;
import android.os.StrictMode;

//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.translate.Translate;
//import com.google.cloud.translate.TranslateOptions;
//import com.google.cloud.translate.Translation;
//
//
//import java.io.IOException;
//import java.io.InputStream;
//
//public class GoogleTranslate {
//
//    String translatedText;
//    String language_code;
//
//
//
//    Translate translate;
//
//
//
//
//    public GoogleTranslate(String code){
//        language_code = code;
//
//    }
//
//
//
//
//
//
//
//
//    public String translate(String originalText, Context context) {
//
//
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        try (InputStream is = context.getResources().openRawResource(R.raw.credentials)) {
//
//            //Get credentials:
//            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);
//
//            //Set credentials and get translate service:
//            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
//            translate = (Translate) translateOptions.getService();
//
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//
//        }
//
//        //Get input text to be translated:
//        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage(language_code), Translate.TranslateOption.model("base"));
//        translatedText = translation.getTranslatedText();
//
//        return translatedText;
//
//    }
//}
