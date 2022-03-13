package com.example.collectqr;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectqr.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch main application activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        User test = new User("testusername");
        test.addCode("code1", 10, "fakelat", "fakelon", "fakegeohash", new Date(), "fakeimage");
        //test.addCode("code2", 20, "fakelat", "fakelon", "faksgeohash", "fakedate");
        //test.addCode("code3", 5000, "fakelat", "fakelon", "faksgeohash", "fakedate");
        UserController controller = new UserController();
        controller.writeToFirestore(test);

        //QRCode code = new QRCode("fakesha", 53.5261794, -113.5259656);
        ////code.addComment("User1", "this is a comment");
        //code.addScannedBy("User2", "scanned on this date");

        //QRCodeController controller1 = new QRCodeController();
        //controller1.writeToFirestore(code);

    }

}