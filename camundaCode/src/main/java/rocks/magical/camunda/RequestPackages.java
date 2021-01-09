package rocks.magical.camunda;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import rocks.magical.camunda.database.entities.Driver;
import rocks.magical.camunda.database.entities.PackageCenter;
import rocks.magical.camunda.database.utils.PackageUtil;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.util.List;
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
        System.out.println(packageCenter);
        System.out.println("weight:" + weight + ", location: " + location + ", dimensions: " + dimensions);
    }
}
