package rocks.magical.camunda.database.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import rocks.magical.camunda.database.entities.*;
import rocks.magical.camunda.database.entities.Package;

import javax.print.attribute.standard.Destination;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        if (jdbcTemplate == null) return null;
        String pointLocation = "POINT(" + location + ")";
        PackageCenter pkg = null;
        List<PackageCenter> packageCenter = jdbcTemplate.query("SELECT centerId, name, ST_AsText(location) FROM packageCenter ORDER BY ST_Distance(location, ST_GeomFromText(?, 4326)) LIMIT 1",
                ps -> ps.setString(1, pointLocation),
                (rs, i) -> new PackageCenter(rs.getInt(1), rs.getString(2), rs.getString(3)));
        if (packageCenter.size() > 0) {
            pkg = packageCenter.get(0);
        }
        return pkg;
    }


    /**
     * Finds the best driver for a route from package center p
     *
     * @param p the packagecenter to start the route from
     * @return the most fitting driver for this package center
     */
    public Driver getBestDriverForPackageCenter(PackageCenter p) {
        String query = "SELECT d.driverid, d.firstname, d.lastname, d.camundaid, d.homebase, pc.name\n" +
                "FROM packageCenter pc\n" +
                "INNER JOIN driver d ON pc.centerid = d.homebase\n" +
                "LEFT JOIN route r ON r.driver_driverid = d.driverid\n" +
                "WHERE ST_DWithin(location, r.destination, 1) OR r.destination IS NULL\n" +
                "ORDER BY ST_Distance(location, ST_GeomFromText(?, 4326))\n" +
                "LIMIT 1";
        String pointLocation = p.getLocation();
        List<Driver> driver = jdbcTemplate.query(query,
                ps -> ps.setString(1, pointLocation),
                (rs, i) -> new Driver(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
        if (driver.size() > 0)
            return driver.get(0);
        else
            return null;
    }

    /**
     * Finds the best driver for a route from package center p
     *
     * @param pointLocation the packagecenter to start the route from
     * @return the most fitting driver for this package center
     */
    public Driver getBestDriverForPackageCenter(String pointLocation) {
        String query = "SELECT d.driverid, d.firstname, d.lastname, d.camundaid, d.homebase, pc.name\n" +
                "FROM packageCenter pc\n" +
                "INNER JOIN driver d ON pc.centerid = d.homebase\n" +
                "LEFT JOIN route r ON r.driver_driverid = d.driverid\n" +
                "WHERE ST_DWithin(location, r.destination, 1) OR r.destination IS NULL\n" +
                "ORDER BY ST_Distance(location, ST_GeomFromText(?, 4326))\n" +
                "LIMIT 1";

        List<Driver> driver = jdbcTemplate.query(query,
                ps -> ps.setString(1, pointLocation),
                (rs, i) -> new Driver(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
        if (driver.size() > 0)
            return driver.get(0);
        else
            return null;
    }

//    /**
//     * Finds the best sequence of packages
//     * @param pointLocation the packagecenter to start the route from
//     * @return sorted List of packages
//     */
//    public List<Package> getSortedPackageListForDelivery(String pointLocation){
//        String query = "SELECT d.destination\n" +
//                "FROM destination d\n" +
//                "WHERE ST_DWithin(location, d.destination, 1) OR d.destination IS NULL\n" +
//                "ORDER BY ST_Distance(location, ST_GeomFromText(?, 4326))\n" +
//                "LIMIT 1";
//        List<Package> packages = jdbcTemplate.query(query,
//                ps -> ps.setString(1, pointLocation),
//                (rs, i) -> new Package(.....));
//        if (packages.size() > 0)
//            return packages;
//        else
//            return null;
//    }


    /**
     * TODO: check if vehicle is already in use
     * Finds the nearest Vehicle for driver
     *
     * @param driver the driver
     * @return nearest vehicle
     */
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
                ")\n" +
                "LIMIT 1";
        Integer driverId = driver.getDriverId();
        List<Vehicle> vehicle = jdbcTemplate.query(query,
                ps -> ps.setInt(1, driverId),
                (rs, i) -> new Vehicle(rs.getInt(1), rs.getInt(6), rs.getString(4), rs.getString(3), rs.getDouble(5), rs.getInt(7)));
        if (vehicle.size() > 0)
            return vehicle.get(0);
        else
            return null;
    }

    /**
     * Save route information as candidate to the database
     * This will mark the route as not yet confirmed
     *
     * @param startLocation
     * @param packageCenter
     * @param driver
     * @param vehicle
     */
    public Integer createRouteCandidate(PackageCenter packageCenter, Driver driver, Vehicle vehicle) {
        Date currentDate = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String dateString = format.format(currentDate);

        String activeRoutes = "SELECT routeId FROM route r WHERE r.vehicle_vehicleid = ? AND r.driver_driverid = ? AND to_char(r.time, 'YYYY-MM-dd') = ?";
        List<Integer> activeRouteId = jdbcTemplate.query(activeRoutes,
                ps -> {
                    ps.setInt(1, vehicle.getVehicleId());
                    ps.setInt(2, driver.getDriverId());
                    ps.setString(3, dateString);
                },
                (rs, i) -> rs.getInt(1));

        if (activeRouteId.size() <= 0) {
            String query = "INSERT INTO route(vehicle_vehicleId, driver_driverId, time, destination, isConfirmed, isActive)\n" +
                    "VALUES\n" +
                    " (?, ?, ?, ST_GeomFromText(?, 4326), ?, ?)\n" +
                    "ON CONFLICT(vehicle_vehicleId, driver_driverId, time)\n" +
                    "DO UPDATE SET vehicle_vehicleId = route.vehicle_vehicleId  RETURNING routeId";

            activeRouteId.addAll(jdbcTemplate.query(query, ps -> {
                ps.setInt(1, vehicle.getVehicleId());
                ps.setInt(2, driver.getDriverId());
                ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                ps.setString(4, packageCenter.getLocation());
                ps.setBoolean(5, false);
                ps.setBoolean(6, false);
            }, (rs, i) -> rs.getInt(1)));
        }

        if (activeRouteId.size() > 0)
            return activeRouteId.get(0);
        else
            return null;
    }

    /**
     * Get the customer which has the given API key
     * @param apiKey key which is used for the customer search
     * @return A customer object which belongs to the given key
     */
    public Customer getCustomerByKey(String apiKey) {
        String query = "SELECT c.customerId, c.customerName, c.customerApiKey FROM customer c WHERE c.customerApiKey = ?";
        List<Customer> driver = jdbcTemplate.query(query,
                ps -> ps.setString(1, HashUtil.sha3(apiKey)),
                (rs, i) -> new Customer(rs.getInt(1), rs.getString(2), rs.getString(3)));
        if (driver.size() > 0)
            return driver.get(0);
        else
            return null;
    }

    /**
     * Creates a Shipment and with a package attached to it
     * @param customerId the customer who wants the shipment to be fulfilled
     * @param startLocation the location where the shipment starts
     * @param targetLocation the destination of the shipment
     * @param weight weight of the package
     * @param dimensions dimensions of the package
     * @param price
     * @param processInstanceId
     * @return Shipment Id
     */
    public Integer createShipment(Integer customerId, String startLocation, String targetLocation, String weight, Map<String, Integer> dimensions, Double price, String processInstanceId) {
        double volume = dimensions.get("w") * dimensions.get("h") * dimensions.get("d");

        try {
            String query = "INSERT INTO package(weightKg, volumeM2) VALUES (?, ?) RETURNING packageId";
            List<Integer> pkg = jdbcTemplate.query(query,
                    ps -> {
                        ps.setInt(1, Integer.parseInt(weight));
                        ps.setDouble(2, volume);
                    },
                    (rs, i) -> rs.getInt(1));

            if (pkg.size() > 0) {
                String startPointLocation = "POINT(" + startLocation + ")";
                String targetPointLocation = "POINT(" + targetLocation + ")";
                Integer packageId = pkg.get(0);
                String shipmentQuery = "INSERT INTO shipmentInfo(customerId, packageId, startLocation, destination, attachedProcessInstance, price) VALUES (?, ?, ST_GeomFromText(?, 4326), ST_GeomFromText(?, 4326), ?, ?) RETURNING shipmentId";
                List<Integer> shipmentId = jdbcTemplate.query(shipmentQuery,
                        ps -> {
                            ps.setInt(1, customerId);
                            ps.setInt(2, packageId);
                            ps.setString(3, startPointLocation);
                            ps.setString(4, targetPointLocation);
                            ps.setString(5, processInstanceId);
                            ps.setDouble(6, price);
                        },
                        (rs, i) -> rs.getInt(1));
                if (shipmentId.size() > 0)
                    return shipmentId.get(0);
            }
        }catch (DataAccessException e) {
            System.out.println("Unable to get data: " + e.getLocalizedMessage());
        }
        return null;
    }

    public void updateShipmentState(int shipmentId, ShipmentStates state) {
        String query = "UPDATE shipmentInfo SET state = ?::shipmentstateenum WHERE shipmentId = ?";
        jdbcTemplate.update(query, state.toString(), shipmentId);
    }

    public Integer getShipmentsIdInPackageCenter(PackageCenter center) {
        return -1;
    }

    public void setBarCodeBase64ForShipment(Integer shipmentId, String barcode) {
        String query = "UPDATE shipmentInfo SET barcode = ? WHERE shipmentId = ?";
        jdbcTemplate.update(query, barcode, shipmentId);
    }

    public void addPackageToRoute(Integer routeId, String shipmentId, String pickupLocation) {
        String startPointLocation = "POINT(" + pickupLocation + ")";
        String query = "INSERT INTO route_has_package(package_packageId, routeId, pickupLocation) VALUES ((SELECT packageId FROM shipmentInfo WHERE shipmentId = ?), ?, ST_GeomFromText(?, 4326))";
        jdbcTemplate.update(query, Integer.parseInt(shipmentId), routeId, startPointLocation);
    }

    public Coordinates getPickupCoordinatesForShipment(Integer shipmentId) {
        String query = "SELECT ST_X(pickuplocation) x, ST_Y(pickuplocation) y FROM\n" +
                "route_has_package rp\n" +
                "INNER JOIN shipmentinfo si ON si.packageid = rp.package_packageid\n" +
                "WHERE si.shipmentid = ?";
        List<Coordinates> coordinates = jdbcTemplate.query(query,
                ps -> ps.setInt(1, shipmentId),
                (rs, i) -> new Coordinates(rs.getDouble(1), rs.getDouble(2)));
        if (coordinates.size() > 0)
            return coordinates.get(0);
        else
            return null;
    }

    public void deleteShipment(String shipmentId) {
        String query = "DELETE FROM shipmentInfo WHERE shipmentId = ?";
        jdbcTemplate.update(query, Integer.parseInt(shipmentId));
    }
}
