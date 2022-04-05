package com.example.collectqr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.collectqr.model.QRCode;
import com.firebase.geofire.GeoLocation;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

public class QRCodeUnitTest {

    @Test
    public void testQRCodeClass() {
        QRCode code = new QRCode("fake_sha");
        assertEquals("fake_sha", code.getSha256());
        assertNull(code.getLatitude());
        assertNull(code.getLongitude());
        assertNull(code.getLocation());
        assertEquals((int) 0, (int) code.getPoints());
        assertEquals("", code.getQr_image());
        assertEquals(new HashMap<>(), code.getScanned_by());
        assertEquals(new HashMap<>(), code.getComments());
        assertNull(code.getDate());
    }

    @Test
    public void testSetters() {
        QRCode code = new QRCode("fake_sha");
        code.setAllLocations(50.1, 30.2);
        code.addComment("user", "comment");
        code.addScannedBy("user", "fake_date");
        code.setDate(new Date());
        code.setQr_image("fake_image");
        assertEquals((double) 50.1, (double) code.getLatitude(), 0.01);
        assertEquals((double) 30.2, (double) code.getLongitude(), 0.01);
        assertEquals(new GeoLocation(50.1, 30.2), code.getLocation());
        assertEquals("comment", code.getComments().get("user"));
        assertEquals("fake_date", code.getScanned_by().get("user"));
        assertEquals(new Date(), code.getDate());
        assertEquals("fake_image", code.getQr_image());
    }
}
