package com.example.collectqr;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.VIBRATE;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.collectqr.data.UserController;
import com.example.collectqr.model.User;
import com.example.collectqr.utilities.HashConversion;
import com.example.collectqr.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

/**
 * An activity which deals with getting permissions for using the camera,
 * along with opening the camera
 */
public class ScanQRCodeLoginActivity extends AppCompatActivity {

    private ScannerLiveView scannerLiveView;
    private TextView scannedTextView;
    FirebaseFirestore db;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        scannerLiveView = findViewById(R.id.camView);
        scannedTextView = findViewById(R.id.scannedData);

        if(checkPermission()){
            Toast.makeText(this, "Permission Granted..",Toast.LENGTH_SHORT).show();
        }else{
            requestPermission();
        }

        scannerLiveView.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override

/**
 *
 * On scanner started
 *
 * @param scanner  the scanner
 */
            public void onScannerStarted(ScannerLiveView scanner) {

                Toast.makeText(ScanQRCodeLoginActivity.this, "Scanner Started...", Toast.LENGTH_SHORT).show();
            }

            @Override

/**
 *
 * On scanner stopped
 *
 * @param scanner  the scanner
 */
            public void onScannerStopped(ScannerLiveView scanner) {

                Toast.makeText(ScanQRCodeLoginActivity.this, "Scanner Stopped...", Toast.LENGTH_SHORT).show();
            }

            @Override

/**
 *
 * On scanner error
 *
 * @param err  the err
 */
            public void onScannerError(Throwable err) {

                Toast.makeText(ScanQRCodeLoginActivity.this, "Scanner Error Occurred Please Start Again...", Toast.LENGTH_SHORT).show();
            }

            @Override

/**
 *
 * On code scanned
 *
 * @param data  the data
 */
            public void onCodeScanned(String data) {

                //extract username from the QR Code data
                String username = data.replace(" LogIn", "");

                //we know this username must be valid, so we can just add the user's device_id
                //to the database under the same username, and configure shared preferences as usual
                db = FirebaseFirestore.getInstance();
                DocumentReference doc = db.collection("Users").document(username);

                //get the device id
                @SuppressLint("HardwareIds") String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                //this adds the device_id to the devices array
                doc.update("devices", FieldValue.arrayUnion(device_id));

                //then, add the username to shared preferences for later use
                Preferences.saveUserName(context, username);

                //search firebase to see if the user should be an admin or not
                db.collection("Admins")
                        .whereEqualTo("device_id", device_id)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    //user doesn't exist within admin document, so they are not an admin
                                    //write this to shared preferences
                                    Preferences.saveAdminStatus(context, false);
                                } else {
                                    //user is an admin, write this to shared preferences
                                    Preferences.saveAdminStatus(context, true);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        });

                //finally, go to the MainAppActivity
                Intent intent = new Intent (context, MainAppActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
