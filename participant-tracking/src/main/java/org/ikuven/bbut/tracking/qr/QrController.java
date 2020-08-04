package org.ikuven.bbut.tracking.qr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/api/qrcodes")
public class QrController {

    private final QrGenerator qrGenerator;

    @Autowired
    public QrController(QrGenerator qrGenerator) {
        this.qrGenerator = qrGenerator;
    }

    @PostMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> createQrCode(@RequestBody String barcode) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qrGenerator.qrCodeImage(barcode));
    }

    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> getQrCodeForCurrentAddress() throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qrGenerator.qrCodeImageForCurrentAddress());
    }
}
