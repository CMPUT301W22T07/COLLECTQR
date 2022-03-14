package com.example.collectqr;

import android.util.Log;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.github.javafaker.Faker;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FireStoreGeoHashQueryTest {
    FirebaseFirestore db;
    //    User test;
    UserController userController;
    QRCodeController qrController;

    @Before
    public void setupFireStoreData() {
        db = FirebaseFirestore.getInstance();
        userController = new UserController();
        qrController = new QRCodeController();
    }

    @Test
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
            User testUser = new User(faker.name().username());
            QRCode testQrCode = new QRCode(faker.code().ean8(), mutateLat, mutateLng);
            GeoLocation testLocation = new GeoLocation(mutateLat, mutateLng);

            // Create user documents on the db instance
            //TODO: TO TEST WRITER: THE ADDCODE CONSTRUCTOR NEEDS TO BE UPDATED
            int userScanCount = faker.number().numberBetween(0, 4);
            /*for (int j = 0; j < userScanCount; j++) {
                testUser.addCode(faker.code().asin(),
                        faker.number().numberBetween(0, 1000),
                        String.valueOf(mutateLat),
                        String.valueOf(mutateLng),
                        GeoFireUtils.getGeoHashForLocation(testLocation),
                        faker.date().toString());
            }*/
            userController.writeToFirestore(testUser);

            // Create QR code documents
            int qrCommentCount = faker.number().numberBetween(0, 4);
            for (int j = 0; j < qrCommentCount; j++) {
                testQrCode.addComment(faker.funnyName().name(),
                        faker.shakespeare().asYouLikeItQuote());
                testQrCode.addScannedBy(faker.funnyName().name(), faker.date().toString());
            }
            qrController.writeToFirestore(testQrCode);

            // Save to list to query later (e.g. deletion, updating)
            userList.add(testUser);
            qrCodeList.add(testQrCode);
            geoLocationList.add(testLocation);

            // TODO: Assertions
        }
    }
}
