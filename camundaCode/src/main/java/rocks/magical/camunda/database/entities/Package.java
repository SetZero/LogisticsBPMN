package rocks.magical.camunda.database.entities;

import java.util.Map;

public class Package {
    private Integer packageId;
    private Double weight;
    private Map<String, Integer> dimensions;
    /**
     * long lat
     */
    private String targetLocation;
    private String startLocation;

    public Package() {
    }

    public Package(Integer packageId, Double weight, Map<String, Integer> dimensions, String targetLocation, String startLocation) {
        this.packageId = packageId;
        this.weight = weight;
        this.dimensions = dimensions;
        this.targetLocation = targetLocation;
        this.startLocation = startLocation;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Map<String, Integer> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Map<String, Integer> dimensions) {
        this.dimensions = dimensions;
    }

    public String getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }
}
