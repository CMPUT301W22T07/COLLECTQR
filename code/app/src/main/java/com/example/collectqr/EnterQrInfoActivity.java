package com.example.collectqr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.collectqr.data.LocationRepository;
import com.example.collectqr.model.QRCode;
import com.example.collectqr.utilities.QRCodeScore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.util.Date;

/**
 * This is the screen where the user inputs information about the QR code scanned
 */
public class EnterQrInfoActivity extends AppCompatActivity {
    private TextView pointsView;
    private com.google.android.material.switchmaterial.SwitchMaterial locationSwitch;
    private Button addImageButton;
    private EditText commentView;
    private QRCode qrCode;
    private FusedLocationProviderClient fusedLocationClient;
    private Double latitude = null;
    private Double longitude = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_qr_info);

        // setup activity for entering qr code info
        Intent intent = getIntent();
        String sha = intent.getStringExtra("sha");
        String username = intent.getStringExtra("username");

        pointsView = findViewById(R.id.qr_info_points);
        locationSwitch = findViewById(R.id.qr_info_location_switch);
        addImageButton = findViewById(R.id.qr_info_add_image);
        commentView = findViewById(R.id.qr_info_comment);
        Button saveButton = findViewById(R.id.qr_info_save_code);

        QRCodeController qrCodeController = new QRCodeController();
        qrCode = new QRCode(sha);
        QRCodeScore qrCodeScore = new QRCodeScore();
        qrCode.setDate(new Date());
        // get the score of the scanned qr code
        Integer points = qrCodeScore.calculateScore(qrCode.getSha256());
        qrCode.setPoints(points);

        pointsView.setText(qrCode.getPoints().toString() + " points");

        if (PermissionsManager.areLocationPermissionsGranted(getApplicationContext())) {
            locationSwitch.setEnabled(true);
        }

        //get the location from the user, to be used if they want a location assigned with the code
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                });

        // optional additions to the qr code post
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override

/**
 *
 * On click
 *
 * @param view  the view
 */
            public void onClick(View view) {

                // TODO: add image functionality
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override

/**
 *
 * On click
 *
 * @param view  the view
 */
            public void onClick(View view) {

                qrCode.setQr_image("test.jpg");
                if(locationSwitch.isChecked()) {
                    qrCode.setAllLocations(latitude, longitude);
                }
                if (!commentView.getText().toString().equals("")) {
                    qrCode.addComment(username, commentView.getText().toString());
                }
                qrCodeController.writeToUserFirestore(qrCode, username);
                qrCodeController.writeToCodesFirestore(qrCode, username);
                finish();
            }
        });
    }
}
