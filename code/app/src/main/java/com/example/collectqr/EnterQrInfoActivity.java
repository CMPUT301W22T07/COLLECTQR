package com.example.collectqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectqr.model.QRCode;
import com.example.collectqr.utilities.QRCodeScore;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_qr_info);

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
        Integer points = qrCodeScore.calculateScore(qrCode);
        qrCode.setPoints(points);

        pointsView.setText(qrCode.getPoints().toString()+" points");

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: add image functionality
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCode.setQr_image("test.jpg");
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
