package com.example.collectqr;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User test = new User("testusername");
        test.addCode("code1", 10, "fakelat", "fakelon", "fakedate");
        test.addCode("code2", 20, "fakelat", "fakelon", "fakedate");
        test.addCode("code3", 5000, "fakelat", "fakelon", "fakedate");
        UserController controller = new UserController();
        controller.writeToFirestore(test);

        QRCode code = new QRCode("fakesha", "fakelat", "fakelon");
        code.addComment("User1", "this is a comment");
        code.addScannedBy("User2", "scanned on this date");

        QRCodeController controller1 = new QRCodeController();
        controller1.writeToFirestore(code);

    };
}