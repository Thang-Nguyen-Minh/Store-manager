package com.example.OOP;

import java.util.ArrayList;
import java.util.List;
public class Report {
  private double totalRevenue;     // Tổng doanh thu
  private List<Order> orders;      // Danh sách đơn hàng đã hoàn thành
  private double expenses;
  private List<Product> products;// Chi phí vận hành

  public Report() {
    this.totalRevenue = 0;
    this.orders = new ArrayList<>();
    this.expenses = 0;
  }

  // Thêm đơn hàng vào báo cáo
  public void addOrder(Order order) {
    orders.add(order);
    totalRevenue += order.calculateTotal();
  }

  public void addExpenses(double expenses) {
    for(Product product : products) {
      this.expenses += product.getCost();
    }
    this.expenses += expenses;
  }

  // Tính lợi nhuận (Doanh thu - Chi phí)
  public double calculateProfit() {
    return totalRevenue - expenses;
  }

  // Tạo báo cáo doanh thu hàng ngày
  public void generateDailyReport() {
    // Giả định in ra báo cáo hàng ngày
    System.out.println("Daily Revenue: " + totalRevenue);
  }

  // Tạo báo cáo doanh thu hàng tháng
  public void generateMonthlyReport() {
    // Giả định in ra báo cáo hàng tháng
    System.out.println("Monthly Revenue: " + totalRevenue);
  }

  // Cập nhật chi phí vận hành
  public void addExpense(double expense) {
    this.expenses += expense;
  }

  // Lấy báo cáo chi tiết
  public String getReport() {
    return "Total Revenue: " + totalRevenue + ", Expenses: " + expenses + ", Profit: " + calculateProfit();
  }
}
