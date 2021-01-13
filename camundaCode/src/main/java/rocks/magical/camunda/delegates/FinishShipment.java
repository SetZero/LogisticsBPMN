package rocks.magical.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.magical.camunda.database.entities.ShipmentStates;
import rocks.magical.camunda.database.utils.PackageUtil;

public class FinishShipment implements JavaDelegate {
    @Autowired
    private PackageUtil packageUtil;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String shipmentId = delegateExecution.getVariable("shipmentId").toString();
        packageUtil.updateShipmentState(Integer.parseInt(shipmentId), ShipmentStates.DELIVERED);

    }
}
