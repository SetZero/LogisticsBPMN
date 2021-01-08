package rocks.magical.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import rocks.magical.camunda.database.entities.Driver;

@Component
public class RequestPackages implements JavaDelegate {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("Spring Bean invoked.");

        /*if(jdbcTemplate != null) {
            jdbcTemplate.query("SELECT * FROM schema_package.driver", (rs, rowNum) -> new Driver(rs.getInt("driverId"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("camundaId")));
        }
        System.out.println("JDBC: " + jdbcTemplate);
        String weight = delegateExecution.getVariable("weight").toString();
        System.out.println("weight:" + weight);*/
    }
}
