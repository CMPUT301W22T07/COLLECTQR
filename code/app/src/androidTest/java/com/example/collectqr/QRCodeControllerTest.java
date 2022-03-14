package com.example.collectqr;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.collectqr.model.QRCode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class QRCodeControllerTest {
    FirebaseFirestore db;

    @Test
    public void testWriteToFirebase() {
        QRCode code = new QRCode("fakeshafortesting", 10.12, 12.32);
        QRCodeController controller = new QRCodeController();
        controller.writeToFirestore(code);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        db = FirebaseFirestore.getInstance();
        db.collection("QRCode")
                .whereEqualTo("sha256", "fakeshafortesting")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            //user doesn't exist
                            fail();
                        } else {
                            //user exists
                            return;
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    @After
    public void tearDown() throws Exception {
        db = FirebaseFirestore.getInstance();
        //delete added user
        db.collection("QRCodes").document("fakeshafortesting")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}
