package com.example.blind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;
    Button btn_ProductInfo, btn_Currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //img_detectCarNo = findViewById(R.id.id_image_detectCarNo);
        btn_ProductInfo = findViewById(R.id.id_btn_productInfo);
        btn_Currency = findViewById(R.id.id_btn_currency);

        //check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    Locale localeToUse = new Locale("en","US");
                    myTTS.setLanguage(localeToUse);
                    myTTS.speak("Hi, Welcome to blind app! Blind Application Use for Blind people. if you want to scan the barcode tap on the upper side of the screen.", TextToSpeech.QUEUE_FLUSH, null);
                }
            }

        });

        btn_ProductInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            // replace this Locale with whatever you want
                            Locale localeToUse = new Locale("en","US");
                            myTTS.setLanguage(localeToUse);
                            myTTS.speak("You have Click Product Information Button.", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });

                Intent intnt_Barcode = new Intent(MainActivity.this, BarcodeReader.class);
                startActivity(intnt_Barcode);


            }
        });

        btn_Currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // Toast.makeText(MainActivity.this, "Click Note Reader", Toast.LENGTH_SHORT).show();

                myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            // replace this Locale with whatever you want
                            Locale localeToUse = new Locale("en","US");
                            myTTS.setLanguage(localeToUse);
                            myTTS.speak("You have Click Currency Reader Home Button.", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });

                Intent intnt_Barcode = new Intent(MainActivity.this, CurrencyReader.class);
                startActivity(intnt_Barcode);

            }
        });
    }




}
