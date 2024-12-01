package com.example.OOP;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private String id;                          // Mã đơn hàng
    private Customer customer;                  // Thông tin khách hàng
    private Map<Product, Integer> products;     // Sản phẩm và số lượng sản phẩm
    private double totalPrice;                  // Tổng số tiền
    private Date date;                          // Ngày tạo đơn hàng
    private String status;                      // Trạng thái đơn hàng

    public Order(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
        this.products = new HashMap<>();
        this.totalPrice = 0;
        this.date = new Date();
        this.status = "Pending";  // Trạng thái ban đầu: Đang chờ xử lý
    }

    // Thêm sản phẩm vào đơn hàng
    public void addProduct(Product product, int quantity) {
        products.put(product, quantity);
    }

    // Tính tổng số tiền đơn hàng
    public double calculateTotal() {
        totalPrice = 0;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        }
        return totalPrice;
    }

    // Cập nhật trạng thái đơn hàng
    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    // Lấy thông tin đơn hàng
    public String getInfo() {
        return "Order ID: " + id + ", Customer: " + customer.getName() + ", Total Price: " + totalPrice + ", Status: " + status;
    }
}

