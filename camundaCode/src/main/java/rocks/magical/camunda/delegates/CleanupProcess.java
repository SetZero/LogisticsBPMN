package rocks.magical.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class CleanupProcess implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String errorInfo = execution.getVariable("error").toString();
        System.out.println("Failed with error: " + errorInfo);
    }
}
