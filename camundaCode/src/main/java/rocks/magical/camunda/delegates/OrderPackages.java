package rocks.magical.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class OrderPackages implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String routeId = delegateExecution.getVariable("routeId").toString();

    }
}
