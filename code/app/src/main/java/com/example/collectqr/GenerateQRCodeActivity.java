package com.example.collectqr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
    private ImageView qrCodeImageView;

    private Button qrCodeGeneratorButton;
    private Button qrCodeGeneratorButton2;
    private String qrCodeText;
    private String qrCodeText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);
        qrCodeTextView = findViewById(R.id.frameText);
        qrCodeImageView = findViewById(R.id.QRCodeImg);

        qrCodeGeneratorButton = findViewById(R.id.QRCodeGeneratorBtn);
        qrCodeGeneratorButton2 = findViewById(R.id.QRCodeGeneratorBtn2);
        // gets string of username + LogIn identifier
        qrCodeText = Preferences.loadUserName(this) + " LogIn";
        // gets string of username + GameStatus identifier
        qrCodeText2 = Preferences.loadUserName(this) + " GameStatus";

        // use this line of code if you want to remove the identifier when scanning
        //qrCodeText = qrCodeText.replace(" LogIn", "");


        // button to generate sign in qr code
        qrCodeGeneratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });


        // button to generate game status qr code
        qrCodeGeneratorButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = qrCodeText2.trim();
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
        });


    }
}