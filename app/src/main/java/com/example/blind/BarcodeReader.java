package com.example.blind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blind.Utils.RetrofitClient;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarcodeReader extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private ZXingScannerView mScannerView;
    String str_BarCode, str_mytext;
    private ProgressBar mProgressBar;
    String str_color, str_Currency, str_message, str_noteAmount, str_product;
    int int_id;
    //TTS object
    private TextToSpeech myTTS;
    SurfaceView mCameraView;
    TextView mTextView, txt_result;
    Button btn_getCurrecny;
    StringBuilder stringBuilder;
    CameraSource mCameraSource;
    private static final int CAMERA_REQUEST = 1888;
    private static final String TAG = "CurrencyReader";
    private static final int requestPermissionID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Scanner QR Code
        ActivityCompat.requestPermissions(BarcodeReader.this,
                new String[]{Manifest.permission.CAMERA},
                1);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    // QR Code Scanner

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();

    }

    @Override
    public void handleResult(final Result rawResult) {

       str_BarCode = rawResult.getText();
       //str_BarCode = "1";
        //edt_getTokenNo.setText(rawResult.getText());

        Toast.makeText(this, "BarCode Result : "+str_BarCode, Toast.LENGTH_SHORT).show();

        if (isConnected()) {

            mProgressBar.setVisibility(View.VISIBLE);

            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .CurrencyNote(str_BarCode);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                  //  mProgressBar.setVisibility(View.GONE);

                    JSONObject jobj= null;
                    try {
                        jobj = new JSONObject(response.body().string());
                        // String status = jobj.getString("status");

                        str_message = jobj.getString("message");

                        if (str_message.equals("error")){
                            Toast.makeText(BarcodeReader.this, "Sorry!!! Not Found", Toast.LENGTH_SHORT).show();

                            myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        // replace this Locale with whatever you want
                                        Locale localeToUse = new Locale("en","US");
                                        myTTS.setLanguage(localeToUse);
                                        myTTS.speak("Sorry!!! Not Found", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }
                            });

                        }else {
                            str_message = jobj.getString("message");
                            //  Toast.makeText(CurrencyReader.this, "" + str_message, Toast.LENGTH_SHORT).show();
                            // str_noteAmount = jobj.getString("noteAmount");
                            // Toast.makeText(CurrencyReader.this, "" + str_message, Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject = jobj.getJSONObject("model2");
                            int_id = jsonObject.getInt("ID");
                            // parkId = jsonObject.getInt("PARK_ID");
                            str_noteAmount = jsonObject.getString("Price");
                            str_color = jsonObject.getString("Color");
                            str_product = jsonObject.getString("ProductName");
                            //  FirstName = jsonObject.getString("FIRST_NAME");
                            // ParkName = jsonObject.getString("PARK_NAME");

                            myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        // replace this Locale with whatever you want
                                        Locale localeToUse = new Locale("en","US");
                                        myTTS.setLanguage(localeToUse);
                                        myTTS.speak("Your Product is: "+str_product+"Product Color is: "+str_color+" and price is: "+str_noteAmount+" Only", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }
                            });


                            /*sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("product_price", str_noteAmount);
                            //editor.putString(Phone, ph);
                            editor.commit();*/


                          //  func_DialogueBox();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Toast.makeText(getApplication(), "Server Something Problem", Toast.LENGTH_LONG).show();

                }
            });


        }else{

            Toast.makeText(BarcodeReader.this, "Net Connection Problem", Toast.LENGTH_LONG).show();

        }

        /*Intent int_QRScannerResult = new Intent(Admin_QRScanner.this, Admin_QRScannerResult.class);
        int_QRScannerResult.putExtra("getQR", str_QR);
        int_QRScannerResult.putExtra("getAdminID", str_adminID);
        int_QRScannerResult.putExtra("getLogo", str_logo);
        int_QRScannerResult.putExtra("getColor", str_color);
        startActivity(int_QRScannerResult);*/

        mScannerView.stopCamera();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(BarcodeReader.this, "Permission denied to camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getApplication().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }



    public void func_DialogueBox(){


        // custom dialog

        final Dialog dialog = new Dialog(BarcodeReader.this);
        dialog.setContentView(R.layout.custom);
        dialog.setTitle("Please Read Your Currency.");

        mCameraView = dialog.findViewById(R.id.surfaceView);
        mTextView = dialog.findViewById(R.id.text_view);
        btn_getCurrecny = dialog.findViewById(R.id.id_GetCarNo);

        btn_getCurrecny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //  img_detectCarNo.setImageBitmap(photo);
                str_mytext = stringBuilder.toString();
                // int_getNo = Integer.parseInt(stringBuilder.toString());

             //   set.add(str_mytext);

                // ArrayList<String> list = new ArrayList<String>();
                // array_list.addAll(list);
                //array_list.addAll(set);

              //  txt_result.setText(str_mytext);

                Intent intent = new Intent(getApplicationContext(), Calculation.class);
                intent.putExtra("myKey_CurrencyNote", str_mytext);
                intent.putExtra("myKey_ProductPrice", str_noteAmount);
                startActivity(intent);


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

                            ActivityCompat.requestPermissions(BarcodeReader.this,
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
