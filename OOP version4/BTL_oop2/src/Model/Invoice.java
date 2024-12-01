package Model;

public class Invoice {
    private String customerID;
    private String productID;
    private int quantity;
    private double totalAmount;

    public Invoice(String customerID, String productID, int quantity, double totalAmount) {
        this.customerID = customerID;
        this.productID = productID;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}

