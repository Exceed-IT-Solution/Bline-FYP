package com.example.blind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CurrencyReader extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final String TAG = "CurrencyReader";
    private static final int requestPermissionID = 100;

    ArrayList<String> array_list = new ArrayList<String>();
    Set set = new HashSet();

    StringBuilder stringBuilder;
    CameraSource mCameraSource;

    SurfaceView mCameraView;
    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;

    Button btn_currencyReader, btn_totalCurrency, btn_clearCurrency, btn_getCurrecny;
    TextView txt_result, mTextView;
    int int_getNo, int_byDefaultNo = 0;
    String str_mytext, str_byDefaultNo = "0", result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_reader);

        btn_currencyReader = findViewById(R.id.id_btn_currencyReader);
        txt_result = findViewById(R.id.id_result_text);

        btn_currencyReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            // replace this Locale with whatever you want
                            Locale localeToUse = new Locale("en","US");
                            myTTS.setLanguage(localeToUse);
                            myTTS.speak("You have Click Currency Reader Button.", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });

                // custom dialog

                final Dialog dialog = new Dialog(CurrencyReader.this);
                dialog.setContentView(R.layout.custom);
                dialog.setTitle("Please Read Your Currency.");

                mCameraView = dialog.findViewById(R.id.surfaceView);
                mTextView = dialog.findViewById(R.id.text_view);
                btn_getCurrecny = dialog.findViewById(R.id.id_GetCarNo);

                btn_getCurrecny.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        str_mytext = stringBuilder.toString();
                        set.add(str_mytext);
                        array_list.addAll(set);
                        txt_result.setText(str_mytext);

                        myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    // replace this Locale with whatever you want
                                    Locale localeToUse = new Locale("en","US");
                                    myTTS.setLanguage(localeToUse);
                                    myTTS.speak(str_mytext+"Rupees Only", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        });

                        // edt_getCarNo.setText(mytext);
                        dialog.dismiss();
                    }
                });

                startCameraSource();
                dialog.show();

            }
        });

    }

    private void startCameraSource() {

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(CurrencyReader.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }
                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 **/
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                mTextView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }
}
