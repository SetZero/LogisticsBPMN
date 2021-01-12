package rocks.magical.camunda.delegates;

import com.google.gson.Gson;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rocks.magical.camunda.database.entities.Customer;
import rocks.magical.camunda.database.entities.Driver;
import rocks.magical.camunda.database.entities.PackageCenter;
import rocks.magical.camunda.database.entities.Vehicle;
import rocks.magical.camunda.database.utils.PackageUtil;

import java.util.Map;

@Component
public class RequestPackages implements JavaDelegate {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private PackageUtil packageUtil;


    @Deprecated
    private void expectNextPackage() {
        runtimeService.createSignalEvent("expectNextPackage")
                .send();
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String weight = delegateExecution.getVariable("weight").toString();
        String startLocation = delegateExecution.getVariable("location").toString();
        String targetLocation = delegateExecution.getVariable("targetLocation").toString();
        String apiKey = delegateExecution.getVariable("apiKey").toString();
        Map<String, Integer> dimensions = (Map<String, Integer>) delegateExecution.getVariable("packageDimensions");
        // remove used variables
        delegateExecution.removeVariable("apiKey");
        delegateExecution.removeVariable("key");

        Customer customer = packageUtil.getCustomerByKey(apiKey);
        if(customer == null) {
            delegateExecution.setVariable("error", "ILLEGAL_ACCESS");
            return;
        }

        double price = Math.round(((dimensions.get("w") * dimensions.get("h") * dimensions.get("d") * 0.01) + Integer.parseInt(weight) * 0.1) * 100.0) / 100.0;
        Integer shipmentId = packageUtil.createShipment(customer.getCustomerId(), startLocation, targetLocation, weight, dimensions, price, delegateExecution.getProcessInstanceId());
        if(shipmentId == null) {
            delegateExecution.setVariable("error", "MALFORMED_REQUEST");
            return;
        }
        /*PackageCenter packageCenter = packageUtil.getNearestPackageCenter(startLocation);
        Driver driver = packageUtil.getBestDriverForPackageCenter(packageCenter);
        Vehicle vehicle = packageUtil.getVehicleForDriver(driver);
        Integer routeId = packageUtil.createRouteCandidate(startLocation, packageCenter, driver, vehicle);*/

        delegateExecution.setVariable("price", price);
        delegateExecution.setVariable("shipmentId", shipmentId);
        delegateExecution.setVariable("error", "NONE"); //f you camunda
        delegateExecution.setVariable("customerId", customer.getCustomerId());
        delegateExecution.setVariable("customerName", customer.getCustomerName());
        /*delegateExecution.setVariable("routeId", routeId);
        delegateExecution.setVariable("packageCenter", gson.toJson(packageCenter));
        delegateExecution.setVariable("driver", gson.toJson(driver));
        delegateExecution.setVariable("vehicle", gson.toJson(vehicle));*/
    }
}
