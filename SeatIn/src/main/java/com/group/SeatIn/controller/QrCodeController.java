package com.group.SeatIn.controller;

import com.group.SeatIn.model.QrRequest;
import com.group.SeatIn.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/ticket")
public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;

    @PostMapping(value = "/code-generation", produces = MediaType.IMAGE_PNG_VALUE)
    /**
     * Generates a QR code based on the provided request data.
     *
     * @param request a {@link QrRequest} object containing the data to encode in
     *                the QR code.
     *
     * @return a byte array representing the generated QR code image.
     *
     * @throws Exception if QR code generation fails due to invalid input or
     *                   internal errors.
     */
    public @ResponseBody byte[] generateQrCode(@RequestBody QrRequest request, @RequestHeader("Authorization") String token) throws Exception {
        return qrCodeService.generateQrCode(request.getText(), 300, 300, token);
    }
}
