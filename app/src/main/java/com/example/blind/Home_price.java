package com.example.blind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class Home_price extends AppCompatActivity {

    Button btn_price, btn_barcode;
    private TextToSpeech myTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_price);

       // btn_price = findViewById(R.id.id_btn_home_price);
        btn_barcode = findViewById(R.id.id_btn_barcode);

        /*btn_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            // replace this Locale with whatever you want
                            Locale localeToUse = new Locale("en","US");
                            myTTS.setLanguage(localeToUse);
                            myTTS.speak("You have Click Price Reader Button.", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });

                Intent intnt_Barcode = new Intent(Home_price.this, Product_price.class);
                startActivity(intnt_Barcode);

                //Toast.makeText(Home_price.this, "in progress!!!", Toast.LENGTH_SHORT).show();

            }
        });*/

        btn_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            // replace this Locale with whatever you want
                            Locale localeToUse = new Locale("en","US");
                            myTTS.setLanguage(localeToUse);
                            myTTS.speak("You have Click BarCode Reader Button.", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });

                Intent intnt_Barcode = new Intent(Home_price.this, BarcodeReader.class);
                startActivity(intnt_Barcode);
            }
        });
    }
}
