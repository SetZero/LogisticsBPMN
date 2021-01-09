-- -----------------------------------------------------
-- Schema schema_package
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema schema_package
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS schema_package;
SET SCHEMA 'schema_package';

DROP EXTENSION postgis CASCADE;
CREATE EXTENSION postgis;


CREATE TYPE vehicleEnum AS ENUM ('CAR', 'PLANE', 'SHIP');

-- -----------------------------------------------------
-- Table packageCenter
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS packageCenter
(
    centerId INT      NOT NULL,
    name     TEXT     NULL,
    location GEOMETRY NULL,
    PRIMARY KEY (centerId)
);

-- -----------------------------------------------------
-- Table vehicle
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS vehicle
(
    vehicleId              INT         NOT NULL,
    vehicleVolumeM2        INT         NULL,
    vehicleType            vehicleEnum NULL,
    vehicleDesc            TEXT        NULL,
    maxWeightKg            DECIMAL     NULL,
    packageCenter_centerId INT         NULL,
    PRIMARY KEY (vehicleId),
    FOREIGN KEY (packageCenter_centerId)
        REFERENCES packageCenter (centerId)
);

-- -----------------------------------------------------
-- Table driver
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS driver
(
    driverId  INT         NOT NULL,
    firstname TEXT        NULL,
    lastname  TEXT        NULL,
    camundaId VARCHAR(45) NULL,
    homeBase  INT         NOT NULL,
    PRIMARY KEY (driverId),
    FOREIGN KEY (homeBase)
        REFERENCES packageCenter (centerId)
);


-- -----------------------------------------------------
-- Table package
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS package
(
    packageId INT     NOT NULL,
    weigthKg  DECIMAL NULL,
    volumeM2  DECIMAL NULL,
    PRIMARY KEY (packageId)
);


-- -----------------------------------------------------
-- Table route
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS route
(
    vehicle_vehicleId INT       NOT NULL,
    driver_driverId   INT       NOT NULL,
    time              TIMESTAMP NOT NULL,
    destination       TEXT      NULL,
    start             TEXT      NULL,
    isConfirmed       BOOLEAN   NOT NULL,
    isActive          BOOLEAN   NOT NULL,
    PRIMARY KEY (vehicle_vehicleId, driver_driverId, time),
    CONSTRAINT fk_vehicle_has_driver_vehicle
        FOREIGN KEY (vehicle_vehicleId)
            REFERENCES vehicle (vehicleId)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT fk_vehicle_has_driver_driver1
        FOREIGN KEY (driver_driverId)
            REFERENCES driver (driverId)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS route_has_package
(
    package_packageId       INT       NOT NULL,
    route_vehicle_vehicleId INT       NOT NULL,
    route_driver_driverId   INT       NOT NULL,
    route_time              TIMESTAMP NOT NULL,
    PRIMARY KEY (package_packageId, route_vehicle_vehicleId, route_driver_driverId, route_time),
    FOREIGN KEY (package_packageId)
        REFERENCES package (packageId)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT fk_package_has_route_route1
        FOREIGN KEY (route_vehicle_vehicleId, route_driver_driverId, route_time)
            REFERENCES route (vehicle_vehicleId, driver_driverId, time)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

INSERT INTO packageCenter(centerId, name, location)
VALUES ('1', 'Berlin Hauptverwaltung', ST_GeomFromText('POINT (13.38282052168627 52.49815983382565)', 4326)),
       ('2', 'Frankfurt Verteilerzentrum', ST_GeomFromText('POINT (8.674983331432774 50.11362977894371)', 4326)),
       ('3', 'Saarbrücken Verteilerzentrum', ST_GeomFromText('POINT (6.99612979628763 49.24030257903976)', 4326)),
       ('4', 'München Verteilerzentrum', ST_GeomFromText('POINT (11.594443317082792 48.15346833909564)', 4326)),
       ('5', 'Stuttgart Verteilerzentrum', ST_GeomFromText('POINT (9.153685947100337 48.7979992045862)', 4326)),
       ('6', 'Speyer Verteilerzentrum', ST_GeomFromText('POINT (8.406333880980359 49.309277440232826)', 4326));

INSERT INTO driver (driverId, firstname, lastname, camundaId, homeBase)
VALUES ('1', 'Heinrich ', 'Hertz', '-1', '1'),
       ('2', 'Isaac ', 'Newton', '-1', '5'),
       ('3', 'Albert', 'Einstein', '-1', '2'),
       ('4', 'Max', 'Planck', '-1', '2'),
       ('5', 'Werner', 'Heisenberg', '-1', '4'),
       ('6', 'Max', 'Born', '-1', '3'),
       ('7', 'Wilhelm Conrad', 'Röntgen', '-1', '3'),
       ('8', 'Marie', 'Curie', '-1', '4');

INSERT INTO vehicle (vehicleId, vehicleVolumeM2, vehicletype, vehicleDesc, maxWeightKg, packageCenter_centerId)
VALUES ('1', '100', 'CAR', 'Koenigsegg Agera RS', '100', '1'),
       ('2', '200', 'CAR', 'Bugatti Veyron 16.4 Super Sport', '100', '1'),
       ('3', '300', 'CAR', 'Bugatti Chiron', '150', '2'),
       ('4', '500', 'SHIP', 'Spirit of Australia', '400', '3'),
       ('5', '400', 'PLANE', 'Lockheed SR-71 Blackbird', '100', '4');
