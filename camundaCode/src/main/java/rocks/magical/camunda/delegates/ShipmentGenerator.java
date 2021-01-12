package rocks.magical.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.magical.camunda.database.entities.Driver;
import rocks.magical.camunda.database.entities.PackageCenter;
import rocks.magical.camunda.database.entities.Vehicle;
import rocks.magical.camunda.database.utils.PackageUtil;

public class ShipmentGenerator implements JavaDelegate {

    @Autowired
    private PackageUtil packageUtil;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String startLocation = delegateExecution.getVariable("location").toString();

        PackageCenter packageCenter = packageUtil.getNearestPackageCenter(startLocation);
        Driver driver = packageUtil.getBestDriverForPackageCenter(packageCenter);
        Vehicle vehicle = packageUtil.getVehicleForDriver(driver);
        Integer routeId = packageUtil.createRouteCandidate(startLocation, packageCenter, driver, vehicle);

        delegateExecution.setVariable("routeId", routeId);
        delegateExecution.setVariable("packageCenter", packageCenter);
        delegateExecution.setVariable("driver", driver);
        delegateExecution.setVariable("vehicle", vehicle);
    }
}
