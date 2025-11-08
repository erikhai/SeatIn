package com.group.SeatIn.service;

import com.google.zxing.WriterException;
import com.group.SeatIn.model.User;
import com.group.SeatIn.security.ValidateToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class QrServiceTest {

    @Mock
    private ValidateToken validateToken;

    @InjectMocks
    private QrCodeService qrCodeService;



    @Test
    void generateQrCode_returnsNonEmptyByteArray() throws WriterException, IOException {
        String qrText = "SeatInTestQRCode";
        when(validateToken.validateToken("")).thenReturn(null);
        byte[] qrBytes = qrCodeService.generateQrCode(qrText, 250, 250, "");

        assertNotNull(qrBytes);
        assertTrue(qrBytes.length > 0);

    }





}
