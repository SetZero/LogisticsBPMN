package rocks.magical.camunda;

import com.google.gson.Gson;
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

    Gson gson = new Gson();

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String weight = delegateExecution.getVariable("weight").toString();
        String startLocation = delegateExecution.getVariable("location").toString();
        Map<String, Integer> dimensions = (Map<String, Integer>) delegateExecution.getVariable("packageDimensions");

        PackageCenter packageCenter = packageUtil.getNearestPackageCenter(startLocation);
        Driver driver = packageUtil.getBestDriverForPackageCenter(packageCenter);
        Vehicle vehicle = packageUtil.getVehicleForDriver(driver);
        Double price = (dimensions.get("w") * dimensions.get("h") * dimensions.get("d") * 0.01) + (Integer.parseInt(weight) * 0.1);
        Integer routeId = packageUtil.createRouteCandidate(startLocation, packageCenter, driver, vehicle);

        delegateExecution.setVariable("price", price);
        delegateExecution.setVariable("routeId", routeId);
        delegateExecution.setVariable("packageCenter", gson.toJson(packageCenter));
        delegateExecution.setVariable("driver", gson.toJson(driver));
        delegateExecution.setVariable("vehicle", gson.toJson(vehicle));
    }
}
