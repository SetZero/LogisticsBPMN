package rocks.magical.camunda.database.entities;

public class Customer {
    private final Integer customerId;
    private final String customerName;
    private final String customerApiKeyHash;

    public Customer(Integer customerId, String customerName, String customerApiKey) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerApiKeyHash = customerApiKey;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerApiKeyHash() {
        return customerApiKeyHash;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", customerApiKey='" + customerApiKeyHash + '\'' +
                '}';
    }
}
