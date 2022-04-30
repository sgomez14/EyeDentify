package com.example.eyedentify;

import android.content.Context;
import android.os.StrictMode;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;


import java.io.IOException;
import java.io.InputStream;

//The google translate class that contains the function that translates the results into the native language of the device
public class GoogleTranslate {

    private static String translatedText;
    static String language_code;
    static Translate translate;

    //The static translate function that takes the string to be translated and the language code as the input
    public static String translate(String originalText,String code, Context context) {
        language_code = code;


       StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
       StrictMode.setThreadPolicy(policy);

       //Create an input stream that opens the credentials file from the raw folder in resources
        try (InputStream is = context.getResources().openRawResource(R.raw.credentials)) {

            //Get the credentials from the Inout Stream
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = (Translate) translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }

        //Get input text to be translated
        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage(language_code), Translate.TranslateOption.model("base"));
        //Returen the translated text
        translatedText = translation.getTranslatedText();

        return translatedText;

    }
}
