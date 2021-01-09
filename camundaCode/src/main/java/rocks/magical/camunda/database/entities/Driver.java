package rocks.magical.camunda.database.entities;

public class Driver {
    private Integer driverId;
    private String firstname;
    private String lastname;
    private String camundaId;

    public Driver() {
    }

    public Driver(Integer driverId, String firstname, String lastname, String camundaId) {
        this.driverId = driverId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.camundaId = camundaId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCamundaId() {
        return camundaId;
    }

    public void setCamundaId(String camundaId) {
        this.camundaId = camundaId;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "driverId=" + driverId +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", camundaId='" + camundaId + '\'' +
                '}';
    }
}
