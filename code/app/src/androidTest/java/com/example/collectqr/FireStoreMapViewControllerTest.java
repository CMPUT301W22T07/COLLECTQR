package com.example.collectqr;

import android.util.Log;

import com.example.collectqr.data.UserController;
import com.example.collectqr.model.QRCode;
import com.example.collectqr.model.User;
import com.example.collectqr.utilities.HashConversion;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.github.javafaker.Faker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The class Fire store map view controller test
 */
public class FireStoreMapViewControllerTest {
    FirebaseFirestore db;
    //    User test;
    UserController userController;
    QRCodeController qrController;

    @Before

/**
 *
 * Setup fire store data
 *
 */
    public void setupFireStoreData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.useEmulator("10.0.2.2", 8080);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);

        userController = new UserController();
        qrController = new QRCodeController();
    }

    @Test

/**
 *
 * Test geo query
 *
 */
    public void testGeoQuery() {

        int TEST_LIMIT = 10;
        Faker faker = new Faker();
        List<User> userList = new ArrayList<>();
        List<QRCode> qrCodeList = new ArrayList<>();
        List<GeoLocation> geoLocationList = new ArrayList<>();

        double offset = 100000000;     // instead of raising to a power, get float by div
        double baseLat = 53.5260000;
        double baseLng = -113.5250000;
        for (int i = 0; i < TEST_LIMIT; i++) {
            // Create latitude/longitude offsets from a base coordinate
            // https://stackoverflow.com/a/7747473 by Bozho
            double mutateLng = Math.floor(((faker.number().numberBetween(1111, 9999)) / offset)
                    + baseLng * 100000) / 100000;
            double mutateLat = Math.floor(((faker.number().numberBetween(1111, 9999)) / offset)
                    + baseLat * 100000) / 100000;
            Log.d("TESTING", mutateLat + ", " + mutateLng);

            // Init objects to test
            String hash = new HashConversion().convertToSHA256(faker.dragonBall().character());
            User testUser = new User(faker.name().username());
            QRCode testQrCode = new QRCode(
                    hash,
                    mutateLat,
                    mutateLng);
            GeoLocation testLocation = new GeoLocation(mutateLat, mutateLng);

            // Create user documents on the db instance
            int userScanCount = faker.number().numberBetween(0, 4);
            for (int j = 0; j < userScanCount; j++) {
                testUser.addCode(
                        hash,
                        faker.number().numberBetween(0, 1000),
                        String.valueOf((mutateLat)),
                        String.valueOf((mutateLng)),
                        GeoFireUtils.getGeoHashForLocation(testLocation),
                        new Date(faker.number().numberBetween(0, 9999)),
                        faker.shakespeare().asYouLikeItQuote()
                );
//                userController.writeToFirestore(testUser);

                // Create QR code documents
                int qrCommentCount = faker.number().numberBetween(0, 4);
                for (int k = 0; k < qrCommentCount; k++) {
                    testQrCode.addComment(faker.funnyName().name(),
                            faker.shakespeare().asYouLikeItQuote());
                    testQrCode.addScannedBy(faker.funnyName().name(), faker.date().toString());
                }
//                qrController.writeToFirestore(testQrCode);

                // Save to list to query later (e.g. deletion, updating)
                userList.add(testUser);
                qrCodeList.add(testQrCode);
                geoLocationList.add(testLocation);

                // TODO: Assertions
            }
        }
    }

    @Test

/**
 *
 * Mini test
 *
 */
    public void miniTest() {

        Faker faker = new Faker();

        double offset = 100000000;     // instead of raising to a power, get float by div
        double baseLat = 53.5260000;
        double baseLng = -113.5250000;

        double mutateLng = Math.floor(((faker.number().numberBetween(1111, 9999)) / offset)
                + baseLng * 100000) / 100000;
        double mutateLat = Math.floor(((faker.number().numberBetween(1111, 9999)) / offset)
                + baseLat * 100000) / 100000;
        Log.d("TESTING", mutateLat + ", " + mutateLng);

        String hash = new HashConversion().convertToSHA256(faker.dragonBall().character());
        User testUser = new User(faker.name().username());
        QRCode testQrCode = new QRCode(
                hash,
                mutateLat,
                mutateLng);
        GeoLocation testLocation = new GeoLocation(mutateLat, mutateLng);

        testUser.addCode(
                hash,
                faker.number().numberBetween(0, 1000),
                String.valueOf((mutateLat)),
                String.valueOf((mutateLng)),
                GeoFireUtils.getGeoHashForLocation(testLocation),
                new Date(faker.number().numberBetween(0, 9999)),
                faker.shakespeare().asYouLikeItQuote());

        userController.writeToFirestore(testUser);

        Log.d("TESTING", testUser.getUsername());
    }
}
