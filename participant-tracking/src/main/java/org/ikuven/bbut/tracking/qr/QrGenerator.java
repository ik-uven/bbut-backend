package org.ikuven.bbut.tracking.qr;

import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;

@Slf4j
@Component
public class QrGenerator {

    private final Environment environment;
    private final PublicAddressClient publicAddressClient;

    public QrGenerator(Environment environment, PublicAddressClient publicAddressClient) {
        this.environment = environment;
        this.publicAddressClient = publicAddressClient;
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

        String ipAddress = publicAddressClient.publicIp();
        String port = environment.getProperty("server.port");

        String hostAddress = ipAddress != null ? ipAddress : InetAddress.getLocalHost().getHostAddress();

        log.info("ip: " + hostAddress + " port: " + port);

        ByteArrayOutputStream stream = QRCode
                .from(String.format("http://%s:%s", hostAddress, port))
                .withSize(250, 250)
                .stream();

        ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

        return ImageIO.read(bis);
    }
}
