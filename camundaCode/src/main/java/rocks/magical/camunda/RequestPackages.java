package rocks.magical.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rocks.magical.camunda.database.entities.Driver;
import rocks.magical.camunda.database.entities.PackageCenter;
import rocks.magical.camunda.database.entities.Vehicle;
import rocks.magical.camunda.database.utils.PackageUtil;

import java.util.Map;

@Component
public class RequestPackages implements JavaDelegate {
    @Autowired
    private PackageUtil packageUtil;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String weight = delegateExecution.getVariable("weight").toString();
        String startLocation = delegateExecution.getVariable("location").toString();
        Map<String, Integer> dimensions = (Map<String, Integer>) delegateExecution.getVariable("packageDimensions");

        PackageCenter packageCenter = packageUtil.getNearestPackageCenter(startLocation);
        Driver driver = packageUtil.getBestDriverForPackageCenter(packageCenter);
        Vehicle vehicle = packageUtil.getVehicleForDriver(driver);
        Double price = (dimensions.get("w") * dimensions.get("h") * dimensions.get("d") * 0.01) + (Integer.parseInt(weight) * 0.1);
        packageUtil.createRouteCandidate(startLocation, packageCenter, driver, vehicle);

        System.out.println(packageCenter);
        System.out.println(driver);
        System.out.println(vehicle);
        System.out.println("weight:" + weight + ", location: " + startLocation + ", dimensions: " + dimensions);

        delegateExecution.setVariable("price", price);
    }
}
