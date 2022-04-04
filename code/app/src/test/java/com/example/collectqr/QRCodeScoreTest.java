package com.example.collectqr;

import static org.junit.Assert.assertEquals;

import com.example.collectqr.utilities.QRCodeScore;

import org.junit.Test;

import java.util.Arrays;


/**
 * The class  QR code score test
 */
public class QRCodeScoreTest {
    @Test

/**
 *
 * Test score system
 *
 */
    public void testScoreSystem() {

        QRCodeScore qrScore = new QRCodeScore();

        char[] chars1 = new char[64];
        Arrays.fill(chars1, '1');
        String test1 = new String(chars1);
        int score1 = qrScore.calculateScore(test1);
        assertEquals(1, score1);

        String test2 = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6";
        int score2 = qrScore.calculateScore(test2);
        assertEquals(111, score2);

        String testSha = "22b910cded0fd44382ca21e82cf1288d47421d8b8d602bf9d8311431b4fec5ab";
        int testScore;
        testScore = qrScore.calculateScore(testSha);
        for (int i = 0; i < 10; i++) {
            assertEquals(testScore, qrScore.calculateScore(testSha));
        }


        // Score has maximum of Integer.MAX_VALUE
        char[] chars3 = new char[64];
        Arrays.fill(chars3, '0');
        String test3 = new String(chars3);
        int score3 = qrScore.calculateScore(test3);
        assertEquals(Integer.MAX_VALUE, score3);
    }
}
