
package com.example.collectqr.utilities;

import com.example.collectqr.model.QRCode;


/**
 * This class includes methods that help with calculating the
 * score of a QR code
 */
public class QRCodeScore extends HashConversion {
     /**
     * Calculates the score of a QR Code converted to SHA-256 hash following the scoring system
     * proposed in the project description.
     *
     * @param sha256String
     *      SHA-256 hash String to obtain the score of
     * @return
     *      long value of the score of the SHA-256 hash
     */
    public int calculateScore(String sha256String) {

        int score = 0;
        int comboLength = 0;
        int comboScore = 0;
        char comboChar = '\u0000';

        for (int i = 0; i < sha256String.length(); i++){
            char currentChar = sha256String.charAt(i);
            int charValue = Integer.parseInt(String.valueOf(currentChar), 16);

            if (currentChar == comboChar) {
                // currently in a combo!
                comboLength++;
                comboScore = (int) (charValue == 0 ? Math.pow(20, comboLength) : Math.pow(charValue, comboLength));
                if (i == sha256String.length() - 1) {
                    score += comboScore;
                }
            } else {
                // combo ended / started
                score += comboScore;
                comboLength = 0;
                comboChar = currentChar;
                comboScore = 0;
            }

        }

        return score;
    }

}
