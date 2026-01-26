package org.emiloanwithbill.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class PdfCreation {

    public String generateEmiBillPdf(String customerName,
                                     String password) throws Exception {

        String outputPath = "EMI_Bill_" + customerName.replace(" ", "_") + ".pdf";

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        PDPageContentStream content = new PDPageContentStream(doc, page);

        float pageWidth = page.getMediaBox().getWidth();

        float y = 760;


        // Title
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 26);
        content.newLineAtOffset(150, y);
        content.showText("Loan EMI Reminder");
        content.endText();

        y -= 50;


        // QR Code with URL
        String url = "https://bluescopetech.com/";

        PDImageXObject qrImage = generateQrCode(doc, url);

        float qrSize = 200;
        float xCenter = (pageWidth - qrSize) / 2;

        content.drawImage(qrImage, xCenter, y - qrSize, qrSize, qrSize);

        y -= (qrSize + 60);


        //clickable link for URI
        var action = new org.apache.pdfbox.pdmodel.interactive.action.PDActionURI();
        action.setURI(url);

        content.close();

        // PDF Password Protection
        AccessPermission ap = new AccessPermission();
        StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, ap);
        spp.setEncryptionKeyLength(128);
        doc.protect(spp);

        doc.save(outputPath);
        doc.close();

        return outputPath;
    }

    // QR Code Generator
    private PDImageXObject generateQrCode(PDDocument doc, String text) throws Exception {

        int size = 300;

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size);

        BufferedImage qr = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                qr.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qr, "png", baos);

        return PDImageXObject.createFromByteArray(doc, baos.toByteArray(), "QR");
    }
}
