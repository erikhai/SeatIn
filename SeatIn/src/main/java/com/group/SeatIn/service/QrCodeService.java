package com.group.SeatIn.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.UserRepository;
import com.group.SeatIn.security.JwtUtil;
import com.group.SeatIn.security.ValidateToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@CrossOrigin(origins = "http://localhost:3000")
public class QrCodeService {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ValidateToken validateToken;

    @Autowired
    private UserRepository userRepository;
    /**
     * Generates a QR code image from the given text with the specified dimensions.
     *
     * @param text   the text content to encode in the QR code (e.g. URL,
     *               identifier, etc.)
     * @param width  the width of the generated QR code image in pixels
     * @param height the height of the generated QR code image in pixels
     *
     * @return a byte array representing the QR code image in PNG format
     *
     * @throws WriterException if QR code generation fails due to invalid content or
     *                         encoding issues
     * @throws IOException     if an I/O error occurs during image writing
     */
    public byte[] generateQrCode(String text, int width, int height, String token) throws WriterException, IOException {
        validateToken.validateToken(token);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        }
    }
}
