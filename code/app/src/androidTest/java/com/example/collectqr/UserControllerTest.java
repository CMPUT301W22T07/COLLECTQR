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


/**
 * The class User controller test
 */
public class UserControllerTest {
    FirebaseFirestore db;

    @Test

/**
 *
 * Test write to firebase
 *
 */
    public void testWriteToFirebase() {

        User testUser = new User("usernamefortesting");
        UserController controller = new UserController();
        controller.writeToFirestore(testUser);

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

    @After

/**
 *
 * Tear down
 *
 * @param Exception  the exception
 * @throws   Exception
 */
    public void tearDown() throws Exception {

        db = FirebaseFirestore.getInstance();
        //delete added user
        db.collection("Users").document("usernamefortesting")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override

/**
 *
 * On success
 *
 * @param aVoid  the a void
 */
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override

/**
 *
 * On failure
 *
 * @param Exception  the exception
 */
                    public void onFailure(@NonNull Exception e) {

                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}
