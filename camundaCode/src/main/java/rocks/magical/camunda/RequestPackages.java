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


        if(jdbcTemplate != null) {
            PackageCenter packageCenter = getNearestPackageCenter(location);
            System.out.println(packageCenter);
        }
        System.out.println("weight:" + weight + ", location: " + location + ", dimensions: " + dimensions);
    }

    private PackageCenter getNearestPackageCenter(String location) {
        String pointLocation = "POINT(" + location + ")";
        PackageCenter pkg = null;
        List<PackageCenter> packageCenter = jdbcTemplate.query("SELECT centerId, name, ST_AsText(location) FROM packageCenter ORDER BY ST_Distance(location, ST_GeomFromText(?, 4326)) LIMIT 1",
                ps -> ps.setString(1, pointLocation),
                (rs, i) -> new PackageCenter(rs.getInt(1), rs.getString(2), rs.getString(3)));
        if(packageCenter.size() > 0) {
            pkg = packageCenter.get(0);
        }
        return pkg;
    }
}
