package com.proyecto.easytakeaway.utilidades;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class QRGenerator {

    public static void generarQR(String datos, String path, String extension) {

        log.info("Generando QR de: "+ datos + " en: " + path);

        int size = 300;
        String fileType = extension;
        path += "." + fileType;
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(datos, BarcodeFormat.QR_CODE, size, size, hints);

            BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            File qrCodeFile = new File(path);
            ImageIO.write(bufferedImage, fileType, qrCodeFile);

            log.info("QR generado correctamente " + qrCodeFile.getAbsolutePath());

        } catch (WriterException | IOException e) {
            log.error("Error al generar el código QR: " + e.getMessage());
        }
    }




}
