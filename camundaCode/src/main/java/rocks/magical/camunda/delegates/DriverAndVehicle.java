package rocks.magical.camunda.delegates;

import com.google.gson.Gson;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BarcodePDF417;
import connectjar.org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rocks.magical.camunda.database.entities.Driver;
import rocks.magical.camunda.database.entities.PackageCenter;
import rocks.magical.camunda.database.entities.ShipmentStates;
import rocks.magical.camunda.database.entities.Vehicle;
import rocks.magical.camunda.database.utils.PackageUtil;
import rocks.magical.camunda.helper.Base64Image;
import rocks.magical.camunda.helper.LocationService;
import rocks.magical.camunda.helper.data.LocationContainer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Component
public class DriverAndVehicle implements JavaDelegate {

    @Autowired
    private PackageUtil packageUtil;

    Gson gson = new Gson();

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        //Map<String, Boolean> packetsToShip = (Map<String, Boolean>) delegateExecution.getVariable("packageMap");
        String packageCenterLocation = delegateExecution.getVariable("targetPackageCenterLocation").toString();
        String[] targetLocation = delegateExecution.getVariable("targetLocation").toString().split("\\s+");
        delegateExecution.removeVariable("targetPackageCenterLocation");
        String shipmentId = delegateExecution.getVariable("shipmentId").toString();
        //
        Driver driver = packageUtil.getBestDriverForPackageCenter(packageCenterLocation);
        Vehicle vehicle = packageUtil.getVehicleForDriver(driver);
        packageUtil.updateShipmentState(Integer.parseInt(shipmentId), ShipmentStates.OUT_FOR_DELIVERY);
        LocationContainer customerAddr = LocationService.getLocationName(
                Double.parseDouble(targetLocation[1]),
                Double.parseDouble(targetLocation[0]));

        delegateExecution.setVariable("driver", gson.toJson(driver));
        delegateExecution.setVariable("vehicle", gson.toJson(vehicle));
        if(customerAddr != null)
            delegateExecution.setVariable("customerAddr", customerAddr.getDisplayName());
        else
            delegateExecution.setVariable("customerAddr", "UNKNOWN");
    }


}
