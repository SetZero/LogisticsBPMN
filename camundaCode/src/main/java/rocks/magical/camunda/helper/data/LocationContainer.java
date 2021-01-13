package rocks.magical.camunda.helper.data;

public class LocationContainer {
    private String displayName;

    public LocationContainer() {
    }

    public LocationContainer(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "LocationContainer{" +
                "displayName='" + displayName + '\'' +
                '}';
    }
}
