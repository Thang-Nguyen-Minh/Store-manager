package Model;


import java.util.ArrayList;

public class SalesManagement {
    private String customerID;
    private String productID;
    private int quantity;
    private double price;

    public SalesManagement(String customerID, String productID, int quantity, double price) {
        this.customerID = customerID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double calculateTotal() {
        return quantity * price;
    }
}
