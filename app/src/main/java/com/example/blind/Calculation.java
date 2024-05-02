package com.example.blind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class Calculation extends AppCompatActivity {

    SharedPreferences prf;
    public static final String MyPREFERENCES = "MyPrefs" ;
    //TTS object
    private TextToSpeech myTTS;

    String str_currencyNote, str_productPrice;
    private TextView txt_product_Price, txt_currency, txt_Reminder;
    private int int_cur, int_pro, result;
    Button btn_result;
    float mValueOne, mValueTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);

        txt_product_Price = findViewById(R.id.id_yourProductPrice);
        txt_currency = findViewById(R.id.id_currency);
        txt_Reminder = findViewById(R.id.id_Reminder);
        btn_result = findViewById(R.id.id_btnResult);

        Intent intent = getIntent();
        final String str_currencyNote = intent.getStringExtra("myKey_CurrencyNote");
        final String str_productPrice = intent.getStringExtra("myKey_ProductPrice");

        myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    Locale localeToUse = new Locale("en","US");
                    myTTS.setLanguage(localeToUse);
                    myTTS.speak("Your Product Price "+str_productPrice+"Rupees Only"+"\n"+"Please Click Tab and find your reminder rupies", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        txt_product_Price.setText(str_productPrice);
        txt_currency.setText(str_currencyNote);

        btn_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   mValueOne = Float.parseFloat(str_currencyNote + str_productPrice);
                mValueOne = Float.parseFloat(str_productPrice);
                mValueTwo = Float.parseFloat(str_currencyNote);
                boolean mSubtract;

              //  mSubtract = true;
                final float result = mValueTwo - mValueOne;
                txt_Reminder.setText(result + "");
                mSubtract = false;

                myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            // replace this Locale with whatever you want
                            Locale localeToUse = new Locale("en","US");
                            myTTS.setLanguage(localeToUse);
                            myTTS.speak("Your Reminder is: "+result+"", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });

            }
        });

    }
}
