package rocks.magical.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.magical.camunda.database.entities.Coordinates;
import rocks.magical.camunda.database.utils.PackageUtil;
import rocks.magical.camunda.helper.LocationService;
import rocks.magical.camunda.helper.data.LocationContainer;

public class OrderPackages implements JavaDelegate {
    @Autowired
    private PackageUtil packageUtil;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String routeId = delegateExecution.getVariable("routeId").toString();
        String shipmentId = delegateExecution.getVariable("shipmentId").toString();
        Coordinates c = packageUtil.getPickupCoordinatesForShipment(Integer.valueOf(shipmentId));
        LocationContainer locationName = LocationService.getLocationName(c.getLatitude(), c.getLongitude());
        if(locationName != null) {
            delegateExecution.setVariable("locationName", locationName.getDisplayName());
        } else {
            delegateExecution.setVariable("locationName", "UNKNOWN LOCATION");
        }
    }
}
