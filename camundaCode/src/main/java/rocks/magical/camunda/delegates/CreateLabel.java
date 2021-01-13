package rocks.magical.camunda.delegates;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BarcodePDF417;
import connectjar.org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rocks.magical.camunda.database.entities.ShipmentStates;
import rocks.magical.camunda.database.utils.PackageUtil;
import rocks.magical.camunda.helper.Base64Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CreateLabel implements JavaDelegate {
    Path tempDirWithPrefix;
    @Autowired
    private PackageUtil packageUtil;

    public CreateLabel() {
        try {
            this.tempDirWithPrefix = Files.createTempDirectory("img_");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String price = execution.getVariable("price").toString();
        String shipmentId = execution.getVariable("shipmentId").toString();

        String barcode = generateBarcode(shipmentId);
        packageUtil.setBarCodeBase64ForShipment(Integer.valueOf(shipmentId), barcode);
        packageUtil.updateShipmentState(Integer.parseInt(shipmentId), ShipmentStates.INFO_RECEIVED);

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
        BufferedImage bImage = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bImage.createGraphics();
        g.drawImage(awtImage, 0, 0, null);
        g.dispose();

        File barcodeFile = File.createTempFile("barcode_", ".tmp", tempDirWithPrefix.toFile());
        ImageIO.write(bImage, "png", barcodeFile);

        byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(barcodeFile));
        return new String(encoded, StandardCharsets.US_ASCII);
    }
}
