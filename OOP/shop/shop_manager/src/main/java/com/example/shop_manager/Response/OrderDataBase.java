package com.example.shop_manager.Response;

import com.example.shop_manager.Entity.Order;
import com.example.shop_manager.Service.DatabaseConnection;
import com.example.shop_manager.Service.IOrderManager;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderDataBase extends JPanel implements IOrderManager {
    private JTextField txtCustomerId, txtProductId, txtQuantity, txtOrderId;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private ArrayList<Order> orderList = new ArrayList<>();
    private HashMap<String, String> customerMap = new HashMap<>();
    private HashMap<String, Double> productMap = new HashMap<>();
    private HashMap<String, Integer> product_quantity = new HashMap<>();

    public OrderDataBase() {
        setLayout(new BorderLayout());

        // Table columns
        String[] columnNames = {"Order ID", "Customer ID", "Customer Name", "Product ID", "Price", "Quantity", "Total"};
        tableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        inputPanel.add(new JLabel("Order ID:"));
        txtOrderId = new JTextField();
        inputPanel.add(txtOrderId);

        inputPanel.add(new JLabel("Customer ID:"));
        txtCustomerId = new JTextField();
        inputPanel.add(txtCustomerId);

        inputPanel.add(new JLabel("Product ID:"));
        txtProductId = new JTextField();
        inputPanel.add(txtProductId);

        inputPanel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        inputPanel.add(txtQuantity);

        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> addOrder());
        inputPanel.add(btnAdd);

        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(e -> deleteOrder());
        inputPanel.add(btnDelete);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(e -> updateOrder());
        inputPanel.add(btnUpdate);

        JButton btnLoadData = new JButton("LoadData");
        btnLoadData.addActionListener(e -> loadData());
        inputPanel.add(btnLoadData);

        orderTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow != -1) {
                    String id = orderTable.getValueAt(selectedRow, 0).toString();
                    String customerId = orderTable.getValueAt(selectedRow, 1).toString();
                    String productId = orderTable.getValueAt(selectedRow, 3).toString();
                    String quantity = orderTable.getValueAt(selectedRow, 5).toString();

                    txtOrderId.setText(id);
                    txtCustomerId.setText(customerId);
                    txtProductId.setText(productId);
                    txtQuantity.setText(quantity);
                }
            }
        });

        add(inputPanel, BorderLayout.EAST);
    }

    @Override
    public void addOrder() {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                String orderId = txtOrderId.getText().trim();
                String customerId = txtCustomerId.getText().trim();
                String productId = txtProductId.getText().trim();
                int quantity = Integer.parseInt(txtQuantity.getText().trim());

                // Kiểm tra số lượng phải lớn hơn 0
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
                    return;
                }
                // Kiểm tra customer_id có tồn tại hay không
                boolean isCustomerValid = false;
                while (!isCustomerValid) {
                    String checkCustomer = "SELECT * FROM Customer WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(checkCustomer)) {
                        stmt.setString(1, customerId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                isCustomerValid = true; // Tìm thấy Customer ID
                            } else {
                                JOptionPane.showMessageDialog(this, "Customer ID not found. Please try again.");
                                customerId = JOptionPane.showInputDialog(this, "Enter Customer ID:");
                                if (customerId == null || customerId.trim().isEmpty()) {
                                    JOptionPane.showMessageDialog(this, "Customer ID cannot be empty.");
                                }
                            }
                        }
                    }
                }
                // Kiểm tra product_id có tồn tại hay không
                boolean isProductValid = false;
                while (!isProductValid) {
                    String checkProduct = "SELECT * FROM Product WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(checkProduct)) {
                        stmt.setString(1, productId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                isProductValid = true;
                            } else {
                                JOptionPane.showMessageDialog(this, "Product ID not found. Please try again.");
                                productId = JOptionPane.showInputDialog(this, "Enter Product ID:");
                                if (productId == null || productId.trim().isEmpty()) {
                                    JOptionPane.showMessageDialog(this, "Product ID cannot be empty.");
                                }
                            }
                        }
                    }
                }
                // Kiểm tra xem Order ID có tồn tại không
                String checkOrder = "SELECT id FROM `Order` WHERE id = ?";
                try (PreparedStatement checkOrderStmt = conn.prepareStatement(checkOrder)) {
                    checkOrderStmt.setString(1, orderId);
                    try (ResultSet rs = checkOrderStmt.executeQuery()) {
                        if (!rs.next()) {
                            // Nếu Order ID không tồn tại, thêm một Order mới
                            String insertOrder = "INSERT INTO `Order` (id, totalPrice, status) VALUES (?, 0, '1')";
                            try (PreparedStatement insertOrderStmt = conn.prepareStatement(insertOrder)) {
                                insertOrderStmt.setString(1, orderId);
                                insertOrderStmt.executeUpdate();
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(this, "Order ID is already in use.");
                            return;
                        }
                    }
                }
                //Thêm thông tin vào Order_customer
                String insertOrder = "INSERT INTO Order_Customer (id_order, id_customer) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertOrder)) {
                    stmt.setString(1, orderId);
                    stmt.setString(2, customerId);
                    stmt.executeUpdate();

                } catch (SQLException e) {
                    conn.rollback();
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error while inserting order.");
                }
                // Kiểm tra sản phẩm tồn tại và số lượng đủ không và thêm sản phẩm vào order_product,cập nhập
                // các thông tin liên quan
                String checkProduct = "SELECT quantity, price FROM Product WHERE id = ?";
                try (PreparedStatement checkProductStmt = conn.prepareStatement(checkProduct)) {
                    checkProductStmt.setString(1, productId);
                    try (ResultSet rs = checkProductStmt.executeQuery()) {
                        if (rs.next()) {
                            int availableQuantity = rs.getInt("quantity");
                            double productPrice = rs.getDouble("price");
                            if (availableQuantity < quantity) {
                                JOptionPane.showMessageDialog(this, "Not enough product in stock.");
                                return;
                            }

                            // Thêm sản phẩm vào Order_Product
                            String insertOrderProduct = "INSERT INTO Order_Product (order_id, product_id, quantity) VALUES (?, ?, ?)";
                            try (PreparedStatement insertOrderProductStmt = conn.prepareStatement(insertOrderProduct)) {
                                insertOrderProductStmt.setString(1, orderId);
                                insertOrderProductStmt.setString(2, productId);
                                insertOrderProductStmt.setInt(3, quantity);
                                insertOrderProductStmt.executeUpdate();
                            }

                            // Trừ số lượng trong kho
                            String updateProductQuantity = "UPDATE Product SET quantity = quantity - ? WHERE id = ?";
                            try (PreparedStatement updateProductStmt = conn.prepareStatement(updateProductQuantity)) {
                                updateProductStmt.setInt(1, quantity);
                                updateProductStmt.setString(2, productId);
                                updateProductStmt.executeUpdate();
                            }

                            // Cập nhật tổng giá trị đơn hàng
                            String updateOrderPrice= "UPDATE `Order` SET totalPrice = totalPrice + ? WHERE id = ?";
                            try (PreparedStatement updateOrderPriceStmt = conn.prepareStatement(updateOrderPrice)) {
                                updateOrderPriceStmt.setDouble(1, productPrice*quantity);
                                updateOrderPriceStmt.setString(2, orderId);
                                updateOrderPriceStmt.executeUpdate();
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Product ID not found.");
                            return;
                        }
                    }
                }
                conn.commit();
                JOptionPane.showMessageDialog(this, "Order updated successfully!");

            } catch (Exception e) {
                e.printStackTrace();
                try (Connection conn = DatabaseConnection.getConnection()) {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        loadData(); // Tải lại dữ liệu sau khi thêm.
    }

    @Override
    public void deleteOrder() {
        String orderId = txtOrderId.getText().trim();
        if (orderId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order ID cannot be empty.");
            return;
        }

        // Lấy dòng được chọn trong bảng
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow != -1) {
            // Hiển thị hộp thoại xác nhận xóa
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this order?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            // Nếu người dùng chọn "Yes", thực hiện xóa
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    conn.setAutoCommit(false);

                    // Xóa dữ liệu liên quan trong bảng Order_Product
                    String deleteOrderProduct = "DELETE FROM Order_Product WHERE order_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteOrderProduct)) {
                        stmt.setString(1, orderId);
                        stmt.executeUpdate();
                    }

                    // Xóa dữ liệu liên quan trong bảng Order_Customer
                    String deleteOrderCustomer = "DELETE FROM Order_Customer WHERE id_order = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteOrderCustomer)) {
                        stmt.setString(1, orderId);
                        stmt.executeUpdate();
                    }

                    // Xóa dữ liệu trong bảng Order
                    String deleteOrder = "DELETE FROM `Order` WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteOrder)) {
                        stmt.setString(1, orderId);
                        int rowsAffected = stmt.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Order has been successfully deleted.");
                            // Xóa dòng tương ứng khỏi bảng JTable
                            tableModel.removeRow(selectedRow);
                        } else {
                            JOptionPane.showMessageDialog(this, "Order ID not found.");
                        }
                    }

                    conn.commit();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No order selected.");
        }
        loadData(); // Tải lại dữ liệu sau khi xóa.
    }

    @Override
    public void updateOrder() {
        int selectedRow = orderTable.getSelectedRow();
        String customerId = txtCustomerId.getText().trim();
        String productId = txtProductId.getText().trim();
        String orderId = txtOrderId.getText().trim();
        int quantity = Integer.parseInt(txtQuantity.getText().trim());

        if (selectedRow != -1) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                Double price = productMap.get(productId);

                if (price == null) {
                    String query = "SELECT price,quantity FROM Product WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, productId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        price = rs.getDouble("price");
                        quantity = rs.getInt("quantity");
                        productMap.put(productId, price);
                        product_quantity.put(productId,quantity);
                    } else {
                        JOptionPane.showMessageDialog(this, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                double total = price * quantity;
                int quantity_productleft = product_quantity.get(productId);
                int quantity_input = Integer.parseInt(txtQuantity.getText().trim());
                String sqlUpdateOrder = "UPDATE `Order` SET totalprice = ? WHERE id = ?";
                try (PreparedStatement statementOrder = conn.prepareStatement(sqlUpdateOrder)) {
                    statementOrder.setDouble(1, total);
                    statementOrder.setString(2, orderId);

                    int rowsUpdatedOrder = statementOrder.executeUpdate();
                    if (rowsUpdatedOrder <= 0) {
                        JOptionPane.showMessageDialog(this, "Order not found or update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                int old_quantity_order;
                String query = "SELECT quantity FROM Order_product WHERE order_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, orderId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    old_quantity_order=rs.getInt("quantity");
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Cập nhật quantity trong bảng Product_Order
                String sqlUpdateProduct_Order = "UPDATE Order_Product SET quantity = ? WHERE order_id= ?";
                try (PreparedStatement statementProduct = conn.prepareStatement(sqlUpdateProduct_Order)) {
                    statementProduct.setInt(1, quantity_input);
                    statementProduct.setString(2, orderId);

                    int rowsUpdatedProduct = statementProduct.executeUpdate();
                    if (rowsUpdatedProduct <= 0) {
                        JOptionPane.showMessageDialog(this, "Product update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                String sqlUpdateProduct = "UPDATE Product SET quantity = ? WHERE id= ?";
                try (PreparedStatement statementProduct = conn.prepareStatement(sqlUpdateProduct)) {
                    statementProduct.setInt(1,quantity_productleft-(quantity_input-old_quantity_order));
                    statementProduct.setString(2, productId);

                    int rowsUpdatedProduct = statementProduct.executeUpdate();
                    if (rowsUpdatedProduct <= 0) {
                        JOptionPane.showMessageDialog(this, "Product update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                conn.commit();

                tableModel.setValueAt(customerId, selectedRow, 1);
                tableModel.setValueAt(productId, selectedRow, 3);
                tableModel.setValueAt(price, selectedRow, 4);
                tableModel.setValueAt(quantity, selectedRow, 5);
                tableModel.setValueAt(total, selectedRow, 6);

                JOptionPane.showMessageDialog(this, "Order and product quantity updated successfully!");
                clearFields();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to update");
        }
        loadData(); // Tải lại dữ liệu sau khi cập nhật.
    }

    @Override
    public void loadData() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ trong bảng.
        orderList.clear(); // Xóa danh sách cũ.

        String sql = """
                SELECT
                    o.id AS order_id,
                    o.status,
                    c.id AS customer_id,
                    c.name AS customer_name,
                    p.id AS product_id,
                    p.price,
                    op.quantity,
                    (p.price * op.quantity) AS total
                FROM
                    `Order` o
                JOIN
                    Order_Customer oc ON o.id = oc.id_order
                JOIN
                    Customer c ON oc.id_customer = c.id
                JOIN
                    Order_Product op ON o.id = op.order_id
                JOIN
                    Product p ON op.product_id = p.id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = new Order(
                        rs.getString("order_id"),
                        rs.getString("customer_id"),
                        rs.getString("product_id"),
                        rs.getInt("quantity")
                );
                // Tính toán và làm tròn giá trị tổng
                double rawTotal = rs.getDouble("total");
                long roundedTotal =(long)Math.round(rawTotal);
                orderList.add(order);
                tableModel.addRow(new Object[]{
                        order.getOrderId(),
                        order.getCustomerId(),
                        rs.getString("customer_name"),
                        order.getProductId(),
                        rs.getDouble("price"),
                        order.getQuantity(),
                        roundedTotal
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }

    @Override
    public void clearFields() {
        txtOrderId.setText("");
        txtCustomerId.setText("");
        txtProductId.setText("");
        txtQuantity.setText("");
    }
}

