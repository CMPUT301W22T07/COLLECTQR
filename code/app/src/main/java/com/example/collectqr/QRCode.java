package com.example.collectqr;


public class QRCode {
    private String QRCode;
    private int score;

    /**
     * Initializes the QRCode object
     */
    QRCode(String QRCode) {
        QRCodeScore qrScore = new QRCodeScore();
        score = qrScore.calculateScore(this);
    }


    /**
     * A getter function to get the score of the QR Code
     *
     * @return The score that was calculated for the QR Code
     */
    public int getScore() {
        return score;
    }


}
