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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Product_price extends AppCompatActivity {

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

    Button btn_priceReader, btn_totalCurrency, btn_clearCurrency, btn_productPrice;
    TextView txt_result, mTextView;
    int int_getNo, int_byDefaultNo = 0;
    String price;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_price);

        textView = (TextView)findViewById(R.id.id_result_text);
        btn_priceReader = findViewById(R.id.id_btn_priceReader);

        btn_priceReader.setOnClickListener(new View.OnClickListener() {
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


                final Dialog dialog = new Dialog(Product_price.this);
                dialog.setContentView(R.layout.custom_product);
                dialog.setTitle("Please Read Your Price.");

                mCameraView = dialog.findViewById(R.id.surfaceView);;
                mTextView = dialog.findViewById(R.id.text_view);
                btn_productPrice = dialog.findViewById(R.id.id_productPrice);

                btn_productPrice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        price = getPriceFromString(stringBuilder.toString());
                        String weight = getWeightFromString(stringBuilder.toString());
                        textView.setText(stringBuilder.toString() + "\n" + "\n" + price + "\n" +weight);
                   //     txt_result.setText(stringBuilder.toString() + "\n" + "\n" + price + "\n" +weight);

                        myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    // replace this Locale with whatever you want
                                    Locale localeToUse = new Locale("en","US");
                                    myTTS.setLanguage(localeToUse);
                                    myTTS.speak(price+"Rupees Only", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        });

                        dialog.dismiss();
                    }
                });

                startCameraSource();
                dialog.show();
            }
        });

    }

    public void dailogBoxFunction(){


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

                            ActivityCompat.requestPermissions(Product_price.this,
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

    private String getPriceFromString(String text) {
        String result;
        Pattern p = Pattern.compile("[Rr][Ss][\\s\\S][\\s\\S][\\d]+|[Uu][Ss][Dd][\\S\\s][\\d]+",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {
            Log.d("Extracted String", matcher.group().toString());
            result = matcher.group().toString();
            return result;
        }else {
            return "no match found";
        }
    }

    private String getWeightFromString(String text){
        String result;
        Pattern p = Pattern.compile("\\d+([\\s\\S]+(ml|gram|gm|kg|litre|Litre|L|Ltr|ltr))",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {
            Log.d("Extracted String", matcher.group().toString());
            result = matcher.group().toString();
            return result;
        }else {
            return "no match found";
        }
    }
}
