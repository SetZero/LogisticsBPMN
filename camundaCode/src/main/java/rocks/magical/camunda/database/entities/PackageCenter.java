package rocks.magical.camunda.database.entities;

public class PackageCenter {
    private final Integer centerId;
    private final String name;
    private final String location;

    public PackageCenter(Integer centerId, String name, String location) {
        this.centerId = centerId;
        this.name = name;
        this.location = location;
    }

    public Integer getCenterId() {
        return centerId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "PackageCenter{" +
                "centerId=" + centerId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
