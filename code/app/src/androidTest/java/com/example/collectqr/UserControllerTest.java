package com.example.collectqr;

import static android.content.ContentValues.TAG;

import static org.junit.Assert.fail;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.collectqr.data.UserController;
import com.example.collectqr.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class UserControllerTest {
    FirebaseFirestore db;

    @Test
    public void testWriteToFirebase() {
        User testUser = new User("usernamefortesting");
        UserController controller = new UserController();
        controller.writeToFirestore(testUser);
        try {
            TimeUnit.SECONDS.sleep(8);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .whereEqualTo("username", "usernamefortesting")
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

    @Test
    public void testAddAdminUser() {
        String device_id = "fakedeviceid";
        UserController controller = new UserController();
        controller.addAdminUser(device_id);
        try {
            TimeUnit.SECONDS.sleep(8);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        db = FirebaseFirestore.getInstance();
        db.collection("Admins")
                .whereEqualTo("device_id", "fakedeviceid")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            //admin doesn't exist
                            fail();
                        } else {
                            //admin exists
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
        db.collection("Users").document("usernamefortesting")
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

        //delete added admin
        db.collection("Admins").document("fakedeviceid")
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
