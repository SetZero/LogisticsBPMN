package rocks.magical.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class RequestPackages implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String weight = delegateExecution.getVariable("weight").toString();
        System.out.println("weight:" + weight);
    }
}
