package com.example.collectqr;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.collectqr.data.LocationRepository;
import com.example.collectqr.model.QRCode;
import com.example.collectqr.utilities.QRCodeScore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.UUID;

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
    private Bitmap imageBitmap = null;

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
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, 1);
                } catch (ActivityNotFoundException e) {
                }
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
                String imagePath = "default_image.jpg"; //set a default image

                //set the qr image, first check if an image was uploaded by checking the bitmap
                if(imageBitmap != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    // Create a storage reference from our app
                    StorageReference storageRef = storage.getReference();
                    String random = UUID.randomUUID().toString();
                    imagePath = username+random+".jpg";
                    // Create a reference to "ourimage.jpg"
                    StorageReference ref = storageRef.child(imagePath);
                    // Create a reference to 'images/ourimage'
                    //StorageReference imagesRef = storageRef.child("images/"+imagePath);

                    //comvert the image bitmap into a stream of bytes
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    //upload the stream of bytes to firestore
                    UploadTask uploadTask = ref.putBytes(data);
                    uploadTask.addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        }
                    });
                }

                //set the path to the image
                qrCode.setQr_image(imagePath);

                //add the location of it was allowed
                if(locationSwitch.isChecked()) {
                    qrCode.setAllLocations(latitude, longitude);
                }

                //add a comment if one was provided
                if (!commentView.getText().toString().equals("")) {
                    qrCode.addComment(username, commentView.getText().toString());
                }

                //finally, write all the data to firestore, and close the activity
                qrCodeController.writeToUserFirestore(qrCode, username);
                qrCodeController.writeToCodesFirestore(qrCode, username);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            System.out.println("EPIC! "+imageBitmap);
        }
    }
}

