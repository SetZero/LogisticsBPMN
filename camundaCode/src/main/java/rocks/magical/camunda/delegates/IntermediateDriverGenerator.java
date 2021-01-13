package rocks.magical.camunda.delegates;

import com.google.gson.Gson;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.magical.camunda.database.entities.Driver;
import rocks.magical.camunda.database.entities.PackageCenter;
import rocks.magical.camunda.database.entities.ShipmentStates;
import rocks.magical.camunda.database.entities.Vehicle;
import rocks.magical.camunda.database.utils.PackageUtil;

public class IntermediateDriverGenerator implements JavaDelegate {

    @Autowired
    private PackageUtil packageUtil;

    Gson gson = new Gson();

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String shipmentId = delegateExecution.getVariable("shipmentId").toString();
        String targetLocation = delegateExecution.getVariable("targetLocation").toString();

        PackageCenter packageCenter = packageUtil.getNearestPackageCenter(targetLocation);
        Driver driver = packageUtil.getBestDriverForPackageCenter(packageCenter);
        Vehicle vehicle = packageUtil.getVehicleForDriver(driver);
        Integer routeId = packageUtil.createRouteCandidate(packageCenter, driver, vehicle);
        packageUtil.addPackageToRoute(routeId, shipmentId, targetLocation);
        packageUtil.updateShipmentState(Integer.parseInt(shipmentId), ShipmentStates.IN_TRANSIT);

        delegateExecution.setVariable("routeId", routeId);
        delegateExecution.setVariable("targetPackageCenterId", packageCenter.getCenterId());
        delegateExecution.setVariable("targetPackageCenterName", packageCenter.getName());
        delegateExecution.setVariable("targetPackageCenterLocation", packageCenter.getLocation());
        delegateExecution.setVariable("driver", gson.toJson(driver));
        delegateExecution.setVariable("vehicle", gson.toJson(vehicle));
        delegateExecution.setVariable("error", "NONE");
    }
}
