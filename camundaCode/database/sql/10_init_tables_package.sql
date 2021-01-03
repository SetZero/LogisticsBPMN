-- -----------------------------------------------------
-- Schema schema_package
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema schema_package
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS schema_package;
SET SCHEMA 'schema_package';


CREATE TYPE vehicleEnum AS ENUM ('CAR', 'PLANE', 'SHIP');

-- -----------------------------------------------------
-- Table vehicle
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS vehicle
(
    vehicleId       INT         NOT NULL,
    vehicleVolumeM2 INT         NULL,
    vehicleType     vehicleEnum NULL,
    vehicleDesc     TEXT        NULL,
    maxWeightKg     DECIMAL     NULL,
    PRIMARY KEY (vehicleId)
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
    PRIMARY KEY (driverId)
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
    package_packageId INT       NOT NULL,
    destination       TEXT      NULL,
    start             TEXT      NULL,
    PRIMARY KEY (vehicle_vehicleId, driver_driverId, time, package_packageId),
    CONSTRAINT fk_vehicle_has_driver_vehicle
        FOREIGN KEY (vehicle_vehicleId)
            REFERENCES vehicle (vehicleId)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT fk_vehicle_has_driver_driver1
        FOREIGN KEY (driver_driverId)
            REFERENCES driver (driverId)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT fk_vehicle_has_driver_package1
        FOREIGN KEY (package_packageId)
            REFERENCES package (packageId)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);


INSERT INTO driver (driverId, firstname, lastname, camundaId) VALUES
('1', 'Heinrich ', 'Hertz', '-1'),
('2', 'Isaac ', 'Newton', '-1'),
('3', 'Albert', 'Einstein', '-1'),
('4', 'Max', 'Planck', '-1'),
('5', 'Werner', 'Heisenberg', '-1'),
('6', 'Max', 'Born', '-1'),
('7', 'Wilhelm Conrad', 'Röntgen', '-1'),
('8', 'Marie', 'Curie', '-1');


INSERT INTO vehicle (vehicleId, vehicleVolumeM2, vehicletype, vehicleDesc, maxWeightKg) VALUES
('1', '100', 'CAR', 'Koenigsegg Agera RS', '100'),
('2', '200', 'CAR', 'Bugatti Veyron 16.4 Super Sport', '100'),
('3', '300', 'CAR', 'Bugatti Chiron', '150'),
('4', '500', 'BOAT', 'Spirit of Australia', '400'),
('5', '400', 'PLANE', 'Lockheed SR-71 Blackbird', '100'),

