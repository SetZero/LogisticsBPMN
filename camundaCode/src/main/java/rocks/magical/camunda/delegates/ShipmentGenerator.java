package rocks.magical.camunda.delegates;

import com.google.gson.Gson;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.magical.camunda.database.entities.*;
import rocks.magical.camunda.database.utils.PackageUtil;

public class ShipmentGenerator implements JavaDelegate {

    @Autowired
    private PackageUtil packageUtil;

    Gson gson = new Gson();

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String startLocation = delegateExecution.getVariable("location").toString();
        String shipmentId = delegateExecution.getVariable("shipmentId").toString();
        String apiKey = delegateExecution.getVariable("apiKey").toString();
        String customerId = delegateExecution.getVariable("customerId").toString();
        Customer customer = packageUtil.getCustomerByKey(apiKey);
        // remove used variables
        delegateExecution.removeVariable("apiKey");
        delegateExecution.removeVariable("key");

        if(customer == null || !customer.getCustomerId().equals(Integer.valueOf(customerId))) {
            delegateExecution.setVariable("error", "ILLEGAL_ACCESS");
            return;
        }


        PackageCenter packageCenter = packageUtil.getNearestPackageCenter(startLocation);
        Driver driver = packageUtil.getBestDriverForPackageCenter(packageCenter);
        Vehicle vehicle = packageUtil.getVehicleForDriver(driver);
        Integer routeId = packageUtil.createRouteCandidate(packageCenter, driver, vehicle);
        packageUtil.addPackageToRoute(routeId, shipmentId, startLocation);
        packageUtil.updateShipmentState(Integer.parseInt(shipmentId), ShipmentStates.COLLECTION_REQUEST);
        //TODO: Mark shipment as shipped!

        delegateExecution.setVariable("routeId", routeId);
        delegateExecution.setVariable("packageCenter", gson.toJson(packageCenter));
        delegateExecution.setVariable("driver", gson.toJson(driver));
        delegateExecution.setVariable("vehicle", gson.toJson(vehicle));
        delegateExecution.setVariable("error", "NONE");
    }
}
