package fr.esgi.robin.colorrun.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRCodeGenerator {

    /**
     * Génère une image QR code sous forme de byte array
     */
    public static byte[] generateQRCodeImage(String data, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
        
        return baos.toByteArray();
    }

    /**
     * Génère les données pour le QR code d'un dossard
     */
    public static String generateQRCodeData(String courseId, String userId, String dossardNumber) {
        // Format: COLORRUN|COURSE_ID|USER_ID|DOSSARD|TIMESTAMP
        long timestamp = System.currentTimeMillis();
        return String.format("COLORRUN|%s|%s|%s|%d", courseId, userId, dossardNumber, timestamp);
    }

    /**
     * Valide un QR code de dossard
     */
    public static boolean validateQRCode(String qrData, String expectedCourseId, String expectedUserId) {
        if (qrData == null || qrData.isEmpty()) {
            return false;
        }
        
        String[] parts = qrData.split("\\|");
        if (parts.length != 5) {
            return false;
        }
        
        return "COLORRUN".equals(parts[0]) && 
               expectedCourseId.equals(parts[1]) && 
               expectedUserId.equals(parts[2]);
    }
} 