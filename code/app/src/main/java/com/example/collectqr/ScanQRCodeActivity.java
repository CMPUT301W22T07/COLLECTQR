package com.example.collectqr;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.VIBRATE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collectqr.utilities.HashConversion;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

/**
 * An activity which deals with getting permissions for using the camera,
 * along with opening the camera
 */
public class ScanQRCodeActivity extends AppCompatActivity {

    private ScannerLiveView scannerLiveView;
    private TextView scannedTextView;
    private View view = getCurrentFocus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        scannerLiveView = findViewById(R.id.camView);
        scannedTextView = findViewById(R.id.scannedData);

        // check for whether permissions granted or not
        if(checkPermission()){
            Toast.makeText(this, "Permission Granted..",Toast.LENGTH_SHORT).show();
        }else{
            requestPermission();
        }

        // scanner activity begins and is waiting for qr code to be presented to camera
        scannerLiveView.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override

/**
 *
 * On scanner started
 *
 * @param scanner  the scanner
 */
            public void onScannerStarted(ScannerLiveView scanner) {

                Toast.makeText(ScanQRCodeActivity.this, "Scanner Started...", Toast.LENGTH_SHORT).show();
            }

            @Override

/**
 *
 * On scanner stopped
 *
 * @param scanner  the scanner
 */
            public void onScannerStopped(ScannerLiveView scanner) {

                Toast.makeText(ScanQRCodeActivity.this, "Scanner Stopped...", Toast.LENGTH_SHORT).show();
            }

            @Override

/**
 *
 * On scanner error
 *
 * @param err  the err
 */
            public void onScannerError(Throwable err) {

                Toast.makeText(ScanQRCodeActivity.this, "Scanner Error Occurred Please Start Again...", Toast.LENGTH_SHORT).show();
            }

            // if the qr code is scanned successfully, run the following block of code
            @Override

/**
 *
 * On code scanned
 *
 * @param data  the data
 */
            public void onCodeScanned(String data) {

                if (data.contains(" GameStatus")) {
                    //Navigate to the User Profile of the user whose GameStatus code was scanned
                    String userToView = data.substring(0,data.indexOf(' '));
                    Intent intent = new Intent();
                    intent.putExtra("sha", "");
                    intent.putExtra("user_to_view", userToView);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                else {
                    String sha = new HashConversion().convertToSHA256(data);
                    Intent intent = new Intent();
                    intent.putExtra("sha", sha);
                    // https://www.tutorialspoint.com/how-to-send-data-to-previous-activity-in-android
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
    }


    /**
     *
     * Check permission
     *
     * @return boolean
     */
    private boolean checkPermission(){

        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA);
        int vibratePermission = ContextCompat.checkSelfPermission(getApplicationContext(),VIBRATE);
        return cameraPermission == PackageManager.PERMISSION_GRANTED && vibratePermission == PackageManager.PERMISSION_GRANTED;
    }


    /**
     *
     * Request permission
     *
     */
    private void requestPermission(){

        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this,new String[]{CAMERA,VIBRATE},PERMISSION_CODE);
    }

    @Override
    protected void onPause() {
        scannerLiveView.stopScanner();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ZXDecoder decoder = new ZXDecoder();
        decoder.setScanAreaPercent(0.8);
        scannerLiveView.setDecoder(decoder);
        scannerLiveView.startScanner();
    }

    @Override

/**
 *
 * On request permissions result
 *
 * @param requestCode  the request code
 * @param String[]  the string []
 * @param int[]  the int []
 */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean vibrationAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if(cameraAccepted && vibrationAccepted){
                Toast.makeText(this, "Permission Granted...", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permission Denied \n You can't use the app without permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
