package rocks.magical.camunda;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BarcodePDF417;
import connectjar.org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rocks.magical.camunda.database.utils.HashUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.init;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class ProcessUnitTest {

  @ClassRule
  @Rule
  public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

  private static final String PROCESS_DEFINITION_KEY = "PaketVersenden";

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  @Before
  public void setup() {
    init(rule.getProcessEngine());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = "packet_versenden.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = "packet_versenden.bpmn")
  public void testHappyPath() {
	  //ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
	  
	  // Now: Drive the process by API and assert correct behavior by rocks.magical.camunda-bpm-assert
  }

  @Test
  public void testBarcodeGenerator() {
    try {
      Path tempDirWithPrefix = Files.createTempDirectory("img_");
      BarcodePDF417 barcode = new BarcodePDF417();
      barcode.setErrorLevel(5);
      barcode.setCodeColumns(20);
      barcode.setCodeRows(20);
      barcode.setOptions(BarcodePDF417.PDF417_FIXED_RECTANGLE);
      barcode.setText("ABC");
      Image barcodeImage = barcode.getImage();

      java.awt.Image awtImage = barcode.createAwtImage(Color.BLACK, Color.WHITE);
      BufferedImage bImage= new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
      Graphics2D g = bImage.createGraphics();
      g.drawImage(awtImage, 0, 0, null);
      g.dispose();

      File barcodeFile = File.createTempFile("barcode_", ".tmp", tempDirWithPrefix.toFile());
      ImageIO.write(bImage, "png", barcodeFile);

      byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(barcodeFile));
      String base64 = new String(encoded, StandardCharsets.US_ASCII);
      System.out.println(base64);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testSHA3() {
    System.out.println(HashUtil.sha3("HACKME"));
  }
}
