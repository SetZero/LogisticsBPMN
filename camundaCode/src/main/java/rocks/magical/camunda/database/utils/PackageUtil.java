package rocks.magical.camunda.database.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import rocks.magical.camunda.database.entities.PackageCenter;

import java.util.List;

public class PackageUtil {
    @Qualifier("jdbcPackage")
    @Autowired
    JdbcTemplate jdbcTemplate;

    public PackageCenter getNearestPackageCenter(String location) {
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
