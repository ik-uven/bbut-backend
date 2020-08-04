package org.ikuven.bbut.tracking.qr;

import net.glxn.qrgen.javase.QRCode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;

@Component
public class QrGenerator {

    private final Environment environment;

    public QrGenerator(Environment environment) {
        this.environment = environment;
    }

    public BufferedImage qrCodeImage(String barcode) throws IOException {

        ByteArrayOutputStream stream = QRCode
                .from(barcode)
                .withSize(250, 250)
                .stream();

        ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

        return ImageIO.read(bis);
    }

    public BufferedImage qrCodeImageForCurrentAddress() throws IOException {

        String port = environment.getProperty("server.port");

        String hostAddress = InetAddress.getLocalHost().getHostAddress();

        ByteArrayOutputStream stream = QRCode
                .from(String.format("http://%s:%s", hostAddress, port))
                .withSize(250, 250)
                .stream();

        ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

        return ImageIO.read(bis);
    }
}
