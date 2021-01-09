package rocks.magical.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rocks.magical.camunda.database.entities.Driver;
import rocks.magical.camunda.database.entities.PackageCenter;
import rocks.magical.camunda.database.utils.PackageUtil;

import java.util.Map;

@Component
public class RequestPackages implements JavaDelegate {
    @Autowired
    private PackageUtil packageUtil;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("Spring Bean invoked.");
        String weight = delegateExecution.getVariable("weight").toString();
        String location = delegateExecution.getVariable("location").toString();
        Map<String, Integer> dimensions = (Map<String, Integer>) delegateExecution.getVariable("packageDimensions");


        PackageCenter packageCenter = packageUtil.getNearestPackageCenter(location);
        Driver driver = packageUtil.getBestDriverForPackageCenter(packageCenter);
        System.out.println(packageCenter);
        System.out.println(driver);
        System.out.println("weight:" + weight + ", location: " + location + ", dimensions: " + dimensions);
    }
}
