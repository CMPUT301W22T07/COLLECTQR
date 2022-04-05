package com.example.collectqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collectqr.utilities.Preferences;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * A class which generates QR codes for the app
 */
public class GenerateQRCodeActivity extends AppCompatActivity {

    private TextView qrCodeTextView;
    private TextView qrGenTxt;
    private TextView qrGenTxt2;
    private ImageView qrCodeImageView;

    private String qrCodeText;
    private String qrCodeText2;

    private int qrGen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);
        qrCodeTextView = findViewById(R.id.frameText);
        qrGenTxt = findViewById(R.id.qrGenTxt);
        qrGenTxt2 = findViewById(R.id.qrGenTxt2);
        qrCodeImageView = findViewById(R.id.QRCodeImg);

        /*
        https://developer.android.com/training/appbar/setting-up#add-toolbar
        https://stackoverflow.com/a/42837106
        StackOverflow, Author tahsinRupam
        https://stackoverflow.com/a/46928058
        StackOverflow, Author: iman hoshmand
        */
        Toolbar toolbar = (Toolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // gets string of username + LogIn identifier
        qrCodeText = Preferences.loadUserName(this) + " LogIn";
        // gets string of username + GameStatus identifier
        qrCodeText2 = Preferences.loadUserName(this) + " GameStatus";

        // use this line of code if you want to remove the identifier when scanning
        //qrCodeText = qrCodeText.replace(" LogIn", "");


        Intent intent = getIntent();
        qrGen = intent.getIntExtra("qrGen", 0);

        qrGenTxt.setVisibility(View.GONE);
        qrGenTxt2.setVisibility(View.GONE);

        if (qrGen == 0) {
            qrGenTxt.setVisibility(View.VISIBLE);

            String data = qrCodeText.trim();
            if(data.isEmpty()){
                Toast.makeText(GenerateQRCodeActivity.this, "Please Enter Some Data to Generate QR Code", Toast.LENGTH_SHORT).show();
            }else{

                // Initialize multi format writer
                MultiFormatWriter writer = new MultiFormatWriter();

                // Initialize bit matrix
                try {
                    BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 250, 250);

                    // Initialize barcode encoder
                    BarcodeEncoder encoder = new BarcodeEncoder();

                    // Initialize Bitmap
                    Bitmap bitmap = encoder.createBitmap(matrix);

                    // set bitmap on image view
                    qrCodeImageView.setImageBitmap(bitmap);

                    // replaces text view with qr code by hiding it
                    qrCodeTextView.setVisibility(View.GONE);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        }


        else {
            qrGenTxt2.setVisibility(qrGenTxt2.VISIBLE);

            String data = qrCodeText2.trim();
            if (data.isEmpty()) {
                Toast.makeText(GenerateQRCodeActivity.this, "Please Enter Some Data to Generate QR Code", Toast.LENGTH_SHORT).show();
            } else {

                // Initialize multi format writer
                MultiFormatWriter writer = new MultiFormatWriter();

                // Initialize bit matrix
                try {
                    BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 250, 250);

                    // Initialize barcode encoder
                    BarcodeEncoder encoder = new BarcodeEncoder();

                    // Initialize Bitmap
                    Bitmap bitmap = encoder.createBitmap(matrix);

                    // set bitmap on image view
                    qrCodeImageView.setImageBitmap(bitmap);

                    // replaces text view with qr code by hiding it
                    qrCodeTextView.setVisibility(View.GONE);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
