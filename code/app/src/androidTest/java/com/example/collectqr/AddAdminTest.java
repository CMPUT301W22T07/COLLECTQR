package com.example.collectqr;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.collectqr.data.UserController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Test to check the addAdminUser() function of the UserController.
 * It has been placed in a separate file from the UserControllerTest, as putting them together
 * causes asynchronous related testing issues
 */
public class AddAdminTest {
    FirebaseFirestore db;

    @Test

/**
 *
 * Test add admin user
 *
 */
    public void testAddAdminUser() {

        String device_id = "fakedeviceid";
        UserController controller = new UserController();
        controller.addAdminUser(device_id);
        try {
            TimeUnit.SECONDS.sleep(10);
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
        //delete added admin
        db.collection("Admins").document("fakedeviceid")
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
