package rocks.magical.camunda;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodePDF417;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rocks.magical.camunda.database.utils.PackageUtil;

import java.util.Arrays;

@Component
public class CreateLabel implements JavaDelegate {
    @Autowired
    private PackageUtil packageUtil;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String packageCenter = execution.getVariable("packageCenter").toString();
        String driver = execution.getVariable("driver").toString();
        String vehicle = execution.getVariable("vehicle").toString();
        String price = execution.getVariable("price").toString();
        String routeId = execution.getVariable("routeId").toString();

        System.out.println("Called create Label: \n" + packageCenter + "\nprice: " + price);

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setErrorLevel(5);
        barcode.setCodeColumns(30);
        barcode.setCodeRows(5);
        barcode.setOptions(BarcodePDF417.PDF417_FIXED_RECTANGLE);
        barcode.setText(routeId);
        Image barcodeImage = barcode.getImage();

        System.out.println(Arrays.toString(barcodeImage.getRawData()));
    }
}
