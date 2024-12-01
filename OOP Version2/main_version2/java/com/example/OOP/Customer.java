package com.example.OOP;

import java.util.*;
public class Customer {
    private String id;               // Mã khách hàng
    private String name;             // Tên khách hàng
    private String phoneNumber;      // Số điện thoại
    private String email;            // Email
    private List<Order> purchaseHistory;  // Lịch sử mua hàng


    public Customer(String id, String name, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.purchaseHistory = new ArrayList<>();
    }

    // Thêm đơn hàng vào lịch sử mua hàng
    public void addPurchase(Order order) {
        purchaseHistory.add(order);
    }

    // Tính tổng số tiền mà khách hàng đã chi tiêu
    public double getTotalSpent() {
        double total = 0;
        for (Order order : purchaseHistory) {
            total += order.calculateTotal();
        }
        return total;
    }

    // Lấy thông tin khách hàng
    public String getInfo() {
        return "ID: " + id + ", Name: " + name + ", Phone: " + phoneNumber + ", Email: " + email;
    }

    public String getName() {
        return this.name;
    }
}

