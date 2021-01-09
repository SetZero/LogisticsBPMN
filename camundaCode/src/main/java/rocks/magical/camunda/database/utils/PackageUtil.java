package rocks.magical.camunda.database.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import rocks.magical.camunda.database.entities.Driver;
import rocks.magical.camunda.database.entities.PackageCenter;
import rocks.magical.camunda.database.entities.Vehicle;

import java.util.Date;
import java.util.List;

public class PackageUtil {
    @Qualifier("jdbcPackage")
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * Finds the closest Package Center to the given coordinates
     *
     * @param location The Geo Location in WKT Format 4326. Coordinates in the Format "longitude[space]latitude", e.g. "7.740688458246426 49.440949204916286"
     * @return a Package Center Closest to the coordinates
     */
    public PackageCenter getNearestPackageCenter(String location) {
        if(jdbcTemplate == null) return null;
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

    public Driver getBestDriverForPackageCenter(PackageCenter p) {
        String query = "SELECT d.driverid, d.firstname, d.lastname, d.camundaid, d.homebase, pc.name\n" +
                "FROM packageCenter pc\n" +
                "INNER JOIN driver d ON pc.centerid = d.homebase\n" +
                "LEFT JOIN route r ON r.driver_driverid = d.driverid\n" +
                "WHERE ST_DWithin(location, r.destination, 20) OR r.destination IS NULL\n" +
                "ORDER BY ST_Distance(location, ST_GeomFromText(?, 4326))\n" +
                "LIMIT 1";
        String pointLocation = p.getLocation();
        List<Driver> driver = jdbcTemplate.query(query,
                ps -> ps.setString(1, pointLocation),
                (rs, i) -> new Driver(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
        if(driver.size() > 0)
            return driver.get(0);
        else
            return null;
    }

    public Vehicle getVehicleForDriver(Driver driver) {
        String query = "SELECT v.vehicleid, ST_AsText(vp.location),  v.vehicledesc, v.vehicletype, v.maxweightkg, v.vehiclevolumem2, vp.centerid\n" +
                "FROM\n" +
                "packagecenter vp\n" +
                "JOIN vehicle v ON v.packagecenter_centerid = vp.centerid\n" +
                "ORDER BY ST_Distance(vp.location, \n" +
                " (SELECT dp.location \n" +
                "  FROM driver d \n" +
                "  INNER JOIN packagecenter dp ON d.homebase = dp.centerid  \n" +
                "  WHERE driverid = ?)\n" +
                ")";
        Integer driverId = driver.getDriverId();
        List<Vehicle> vehicle = jdbcTemplate.query(query,
                ps -> ps.setInt(1, driverId),
                (rs, i) -> new Vehicle(rs.getInt(1), rs.getInt(6), rs.getString(4), rs.getString(3), rs.getDouble(5), rs.getInt(7)));
        if(vehicle.size() > 0)
            return vehicle.get(0);
        else
            return null;
    }

    public void createRouteCandidate(String startLocation, PackageCenter packageCenter, Driver driver, Vehicle vehicle) {
        String startPointLocation = "POINT(" + startLocation + ")";
        String query = "INSERT INTO route(vehicle_vehicleId, driver_driverId, time, destination, start, isConfirmed, isActive) VALUES (?, ?, ?, ST_GeomFromText(?, 4326), ST_GeomFromText(?, 4326), ?, ?)";
        jdbcTemplate.update(query, vehicle.getVehicleId(), driver.getDriverId(), new Date(System.currentTimeMillis()), startPointLocation, packageCenter.getLocation(), false, false);
    }
}
