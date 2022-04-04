package com.example.collectqr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.collectqr.model.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class UserUnitTest {

    @Test
    public void testUserClass() {
        User user = new User("username");
        assertEquals("username", user.getUsername());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPhone());
        assertEquals(new ArrayList<>(), user.getDevices());
        assertFalse(user.isScan_1_code());
        assertFalse(user.isScan_10_codes());
        assertFalse(user.isScan_50_codes());
        assertFalse(user.isScan_10_points());
        assertFalse(user.isScan_100_points());
        assertFalse(user.isScan_300_points());
        HashMap<String, Integer> fakeMap = new HashMap<>();
        fakeMap.put("num_codes", 0);
        fakeMap.put("total_points", 0);
        fakeMap.put("best_code", 0);
        assert(user.getStats().equals(fakeMap));
        assertEquals(new HashMap<>(), user.getCodes_scanned());
    }

    @Test
    public void testSetters() {
        User user = new User("username");
        user.setEmail("fake_email");
        user.setPhone("fake_phone");
        user.setScan_1_code(true);
        user.setScan_10_codes(true);
        user.setScan_50_codes(true);
        user.setScan_10_points(true);
        user.setScan_100_points(true);
        user.setScan_300_points(true);
        user.addDevice("fake_device");
        user.updateScore(1, 1, 1);

        assertEquals("fake_email", user.getEmail());
        assertEquals("fake_phone", user.getPhone());
        assertEquals("fake_device", user.getDevices().get(0));
        assertTrue(user.isScan_1_code());
        assertTrue(user.isScan_10_codes());
        assertTrue(user.isScan_50_codes());
        assertTrue(user.isScan_10_points());
        assertTrue(user.isScan_100_points());
        assertTrue(user.isScan_300_points());
        HashMap<String, Integer> fakeMap = new HashMap<>();
        fakeMap.put("num_codes", 1);
        fakeMap.put("total_points", 1);
        fakeMap.put("best_code", 1);
        assert(user.getStats().equals(fakeMap));
        assertEquals(new HashMap<>(), user.getCodes_scanned());
    }
}
