package rocks.magical.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.magical.camunda.database.entities.ShipmentStates;
import rocks.magical.camunda.database.utils.PackageUtil;

public class CleanupProcess implements JavaDelegate {
    @Autowired
    private PackageUtil packageUtil;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String errorInfo = execution.getVariable("error").toString();
        Object shipmentId = execution.getVariable("shipmentId");
        System.out.println("Failed with error: " + errorInfo);
        switch (errorInfo) {
            case "ILLEGAL_ACCESS":
            case "MALFORMED_REQUEST":
                System.out.println("Illegal access or malformed request!");
                break;
            case "NONE":
            case "IMPOSSIBLE_TASK":
                System.out.println("Impossible Task, needs cleanup...");
                cancelShipment(shipmentId.toString(), errorInfo);
                break;
            case "CANCEL_SHIPMENT":
                System.out.println("Shipment canceled, needs cleanup...");
                deleteShipment(shipmentId.toString());
                break;
            case "UNKNOWN_ID":
                System.out.println("Unknown Request ID, maybe too old? needs cleanup...");
                deleteShipment(shipmentId.toString());
                break;
        }
    }

    private void cancelShipment(String shipmentId, String reason) {
        switch (reason) {
            case "NONE":
            case "IMPOSSIBLE_TASK":
                packageUtil.updateShipmentState(Integer.parseInt(shipmentId), ShipmentStates.DECLINED);
                break;
        }
    }

    private void deleteShipment(String shipmentId) {
        packageUtil.deleteShipment(shipmentId);
    }
}
