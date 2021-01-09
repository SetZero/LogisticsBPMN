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

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Component
public class RequestPackages implements JavaDelegate {
    @Qualifier("jdbcPackage")
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("Spring Bean invoked.");
        String weight = delegateExecution.getVariable("weight").toString();
        String location = delegateExecution.getVariable("location").toString();
        Map<String, Integer> dimensions = (Map<String, Integer>) delegateExecution.getVariable("packageDimensions");
        System.out.println(dimensions);

        if(jdbcTemplate != null) {
            List<Driver> res = jdbcTemplate.query("SELECT * FROM driver", (rs, rowNum) -> new Driver(rs.getInt("driverId"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("camundaId")));
            System.out.println(res);
        }
        System.out.println("weight:" + weight + ", location: " + location + ", dimensions: " + dimensions);
    }
}
