package com.example.collectqr;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;

public class QRCodeScoreTest {
    @Test
    public void testScoreSystem() {
        QRCodeScore qrScore = new QRCodeScore();

        char[] chars1 = new char[64];
        Arrays.fill(chars1, '1');
        String test1 = new String(chars1);
        int score1 = qrScore.scoreSystem(test1);
        assertEquals(1, score1);

        String test2 = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6";
        int score2 = qrScore.scoreSystem(test2);
        assertEquals(111, score2);

        // Score has maximum of Integer.MAX_VALUE
        char[] chars3 = new char[64];
        Arrays.fill(chars3, '0');
        String test3 = new String(chars3);
        int score3 = qrScore.scoreSystem(test3);
        assertEquals(Integer.MAX_VALUE, score3);
    }
}
