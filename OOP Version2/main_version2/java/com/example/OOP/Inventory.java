package com.example.OOP;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<String, Product> products; // Danh sách sản phẩm trong kho (theo mã sản phẩm)

    public Inventory() {
        this.products = new HashMap<>();
    }

    // Thêm sản phẩm mới vào kho
    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    // Xóa sản phẩm khỏi kho
    public void removeProduct(String productId) {
        products.remove(productId);
    }

    // Kiểm tra số lượng hàng tồn kho của 1 sản phẩm
    public int checkStock(String productId) {
        Product product = products.get(productId);
        if (product != null) {
            return product.getQuantity();
        }
        return 0;
    }

    // Cập nhật thông tin sản phẩm
    public void updateProductInfo(String productId, Product newInfo) {
        products.put(productId, newInfo);
    }

    // Lấy thông tin kho hàng
    public String getInventoryInfo() {
        StringBuilder info = new StringBuilder();
        for (Product product : products.values()) {
            info.append(product.getInfo()).append("\n");
        }
        return info.toString();
    }
}
