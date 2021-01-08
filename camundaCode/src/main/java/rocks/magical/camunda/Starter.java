package rocks.magical.camunda;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public class Starter implements InitializingBean {

    @Autowired
    private RuntimeService runtimeService;

    public void afterPropertiesSet() throws Exception {
        runtimeService.startProcessInstanceByKey("packageProcess");
    }

    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }
}
