package com.example.collectqr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User test = new User("testusername");
        test.addCode("code1", 10, "fakelat", "fakelon", "fakegeohash", "fakedate");
        test.addCode("code2", 20, "fakelat", "fakelon", "faksgeohash", "fakedate");
        test.addCode("code3", 5000, "fakelat", "fakelon", "faksgeohash", "fakedate");
        UserController controller = new UserController();
        controller.writeToFirestore(test);

        QRCode code = new QRCode("fakesha", 53.5261794, -113.5259656);
        code.addComment("User1", "this is a comment");
        code.addScannedBy("User2", "scanned on this date");

        QRCodeController controller1 = new QRCodeController();
        controller1.writeToFirestore(code);

    }

    ;
}