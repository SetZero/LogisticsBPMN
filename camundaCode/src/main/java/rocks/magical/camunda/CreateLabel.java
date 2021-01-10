package rocks.magical.camunda;

import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BarcodePDF417;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import connectjar.org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rocks.magical.camunda.database.utils.PackageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Component
public class CreateLabel implements JavaDelegate {
    @Autowired
    private PackageUtil packageUtil;
    Path tempDirWithPrefix;

    public CreateLabel() {
        try {
            this.tempDirWithPrefix = Files.createTempDirectory("img_");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String packageCenter = execution.getVariable("packageCenter").toString();
        String driver = execution.getVariable("driver").toString();
        String vehicle = execution.getVariable("vehicle").toString();
        String price = execution.getVariable("price").toString();
        String routeId = execution.getVariable("routeId").toString();

        System.out.println("Called create Label: \n" + packageCenter + "\nprice: " + price);

        String barcode = generateBarcode(routeId);
        // execution.setVariable("barcode", barcode); <- todo: camunda only accepts up to 4000 chars
        execution.setVariable("price", price);
    }

    private String generateBarcode(String routeId) throws BadElementException, IOException {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setErrorLevel(5);
        barcode.setCodeColumns(20);
        barcode.setCodeRows(20);
        barcode.setOptions(BarcodePDF417.PDF417_FIXED_RECTANGLE);
        barcode.setText(routeId);
        Image barcodeImage = barcode.getImage();

        java.awt.Image awtImage = barcode.createAwtImage(Color.BLACK, Color.WHITE);
        BufferedImage bImage= new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bImage.createGraphics();
        g.drawImage(awtImage, 0, 0, null);
        g.dispose();

        File barcodeFile = File.createTempFile("barcode_", ".tmp", tempDirWithPrefix.toFile());
        ImageIO.write(bImage, "png", barcodeFile);

        byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(barcodeFile));
        return new String(encoded, StandardCharsets.US_ASCII);
    }
}
