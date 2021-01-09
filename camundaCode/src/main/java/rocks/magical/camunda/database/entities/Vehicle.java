package rocks.magical.camunda.database.entities;

public class Vehicle {
    Integer vehicleId;
    Integer vehicleVolumeM2;
    String vehicleType;
    String vehicleDesc;
    Double maxWeightKg;
    Integer packageCenter_centerId;

    public Vehicle(Integer vehicleId, Integer vehicleVolumeM2, String vehicleType, String vehicleDesc, Double maxWeightKg, Integer packageCenter_centerId) {
        this.vehicleId = vehicleId;
        this.vehicleVolumeM2 = vehicleVolumeM2;
        this.vehicleType = vehicleType;
        this.vehicleDesc = vehicleDesc;
        this.maxWeightKg = maxWeightKg;
        this.packageCenter_centerId = packageCenter_centerId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public Integer getVehicleVolumeM2() {
        return vehicleVolumeM2;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getVehicleDesc() {
        return vehicleDesc;
    }

    public Double getMaxWeightKg() {
        return maxWeightKg;
    }

    public Integer getPackageCenter_centerId() {
        return packageCenter_centerId;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + vehicleId +
                ", vehicleVolumeM2=" + vehicleVolumeM2 +
                ", vehicleType='" + vehicleType + '\'' +
                ", vehicleDesc='" + vehicleDesc + '\'' +
                ", maxWeightKg=" + maxWeightKg +
                ", packageCenter_centerId=" + packageCenter_centerId +
                '}';
    }
}
