package rocks.magical.camunda;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;
import org.camunda.bpm.engine.ProcessEngine;

@ProcessApplication
public class CamundaBpmnProcessApplication extends ServletProcessApplication {
    public static final String PROCESS_DEFINITION_KEY = "PaketVersenden";

    @PostDeploy
    public void onDeploymentFinished(ProcessEngine processEngine) {

    }
}
