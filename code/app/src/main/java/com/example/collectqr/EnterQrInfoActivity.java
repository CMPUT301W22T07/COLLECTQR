package com.example.collectqr;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.collectqr.model.QRCode;
import com.example.collectqr.ui.MapViewFragment;
import com.example.collectqr.utilities.QRCodeScore;
import com.example.collectqr.viewmodels.LeaderboardViewModel;
import com.example.collectqr.viewmodels.MapViewViewModel;
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
    private Bitmap imageBitmap = null;
    private LeaderboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_qr_info);
        viewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);

        // setup activity for entering qr code info
        Intent intent = getIntent();
        String sha = intent.getStringExtra("sha");
        String username = intent.getStringExtra("username");

        pointsView = findViewById(R.id.qr_info_points);
        locationSwitch = findViewById(R.id.qr_info_location_switch);
        addImageButton = findViewById(R.id.qr_info_add_image);
        commentView = findViewById(R.id.qr_info_comment);
        Button saveButton = findViewById(R.id.qr_info_save_code);

        /*
        https://developer.android.com/training/appbar/setting-up#add-toolbar
        https://stackoverflow.com/a/42837106
        StackOverflow, Author tahsinRupam
        https://stackoverflow.com/a/46928058
        StackOverflow, Author: iman hoshmand
        */
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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

        // optional additions to the qr code post
        /**
         *
         * On click
         *
         * @param view  the view
         */addImageButton.setOnClickListener(view -> {

             // TODO: add image functionality
             Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
             try {
                 startActivityForResult(takePictureIntent, 1);
             } catch (ActivityNotFoundException e) {
             }
         });
        /**
         *
         * On click
         *
         * @param view  the view
         */saveButton.setOnClickListener(view -> {
             String imagePath = "default_image.jpg"; //set a default image

             //set the qr image, first check if an image was uploaded by checking the bitmap
             if (imageBitmap != null) {
                 FirebaseStorage storage = FirebaseStorage.getInstance();
                 // Create a storage reference from our app
                 StorageReference storageRef = storage.getReference();
                 String random = UUID.randomUUID().toString();
                 imagePath = username + random + ".jpg";
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
             if (locationSwitch.isChecked()) {
                 viewModel.getLocationLiveData().observe(this, location -> {
                     if (location != null) {
                         qrCode.setAllLocations(location.getLatitude(), location.getLongitude());

                         MapViewViewModel viewModel = new ViewModelProvider(this).get(MapViewViewModel.class);
                         viewModel.clearQrGeoLocations();
                     }
                 });
             }

             //add a comment if one was provided
             if (!commentView.getText().toString().equals("")) {
                 qrCode.addComment(username, commentView.getText().toString());
             }

             //finally, write all the data to firestore, and close the activity
             qrCodeController.writeToUserFirestore(qrCode, username);
             qrCodeController.writeToCodesFirestore(qrCode, username);
             finish();
         });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            System.out.println("EPIC! " + imageBitmap);
        }
    }
}

