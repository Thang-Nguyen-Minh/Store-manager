package com.example.shop_manager.Response;

import com.example.shop_manager.Main.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ReportDataBase extends JPanel {
    private JTextField txtCustomerID, txtOrderID;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotalAmount;

    public ReportDataBase() {
        setLayout(new BorderLayout());

        String[] columnNames = {"Invoice ID", "Customer ID", "Customer Name", "Product ID", "Price", "Quantity", "Total"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1));

        JPanel panelRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnPrintInvoices = new JButton("Print All Invoices");
        btnPrintInvoices.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printAllInvoices();
            }
        });
        panelRow1.add(btnPrintInvoices);


        JPanel panelRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelRow2.add(new JLabel("Enter Customer ID:"));
        txtCustomerID = new JTextField(10);
        panelRow2.add(txtCustomerID);
        JButton btnSearchInvoices = new JButton("Search by Customer ID");
        btnSearchInvoices.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchInvoicesByCustomerID();
            }
        });
        panelRow2.add(btnSearchInvoices);

        panelRow2.add(new JLabel("Enter Order ID:"));
        txtOrderID = new JTextField(10);
        panelRow2.add(txtOrderID);
        JButton btnSearchByOrderID = new JButton("Search by Order ID");
        btnSearchByOrderID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchInvoicesByOrderID();
            }
        });
        panelRow2.add(btnSearchByOrderID);
        lblTotalAmount = new JLabel("Total Amount: 0.0");
        panelRow2.add(lblTotalAmount);
        controlPanel.add(panelRow1);
        controlPanel.add(panelRow2);
        add(controlPanel, BorderLayout.SOUTH);
    }


    private void printAllInvoices() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = """
            SELECT o.id AS invoice_id, 
                   c.id AS customer_id, 
                   c.name AS customer_name, 
                   p.id AS product_id, 
                   p.price, 
                   op.quantity, 
                   (p.price * op.quantity) AS total 
            FROM `Order` o
            LEFT JOIN Order_Customer oc ON o.id = oc.id_order
            LEFT JOIN Customer c ON oc.id_customer = c.id
            LEFT JOIN Order_Product op ON o.id = op.order_id
            LEFT JOIN Product p ON op.product_id = p.id
            """;

            ResultSet resultSet = connection.createStatement().executeQuery(sql);

            model.setRowCount(0);

            double totalAmount = 0.0;
            while (resultSet.next()) {
                String invoiceID = resultSet.getString("invoice_id");
                String customerID = resultSet.getString("customer_id");
                String customerName = resultSet.getString("customer_name");
                String productID = resultSet.getString("product_id");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                double total = Double.parseDouble(String.format("%.2f", price * quantity));

                model.addRow(new Object[]{invoiceID, customerID, customerName, productID, price, quantity, total});
                totalAmount += total;
            }

            lblTotalAmount.setText("Total Amount: " + totalAmount);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error database: " + ex.getMessage());
        }
    }

    private void searchInvoicesByCustomerID() {
        String customerID = txtCustomerID.getText().trim();
        if (customerID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Customer ID.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = """
            SELECT o.id AS invoice_id, 
                   c.id AS customer_id, 
                   c.name AS customer_name, 
                   p.id AS product_id, 
                   p.price, 
                   op.quantity, 
                   (p.price * op.quantity) AS total 
            FROM `Order` o
            LEFT JOIN Order_Customer oc ON o.id = oc.id_order
            LEFT JOIN Customer c ON oc.id_customer = c.id
            LEFT JOIN Order_Product op ON o.id = op.order_id
            LEFT JOIN Product p ON op.product_id = p.id
            WHERE c.id = ?
            """;

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, customerID);
            ResultSet resultSet = preparedStatement.executeQuery();
            model.setRowCount(0);
            double totalAmount = 0.0;
            boolean hasData = false;

            while (resultSet.next()) {
                hasData = true;

                String invoiceID = resultSet.getString("invoice_id");
                String customerIDResult = resultSet.getString("customer_id");
                String customerName = resultSet.getString("customer_name");
                String productID = resultSet.getString("product_id");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                double total = Double.parseDouble(String.format("%.2f", price * quantity));

                model.addRow(new Object[]{invoiceID, customerIDResult, customerName, productID, price, quantity, total});

                totalAmount += total;
            }
            if (!hasData) {
                JOptionPane.showMessageDialog(this, "No data found for Customer ID: " + customerID);
            }
            lblTotalAmount.setText("Total Amount: " + totalAmount);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data from database: " + ex.getMessage());
        }
    }

    private void searchInvoicesByOrderID() {
        String orderID = txtOrderID.getText().trim();
        if (orderID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Order ID.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = """
            SELECT o.id AS invoice_id, 
                   c.id AS customer_id, 
                   c.name AS customer_name, 
                   p.id AS product_id, 
                   p.price, 
                   op.quantity, 
                   (p.price * op.quantity) AS total 
            FROM `Order` o
            LEFT JOIN Order_Customer oc ON o.id = oc.id_order
            LEFT JOIN Customer c ON oc.id_customer = c.id
            LEFT JOIN Order_Product op ON o.id = op.order_id
            LEFT JOIN Product p ON op.product_id = p.id
            WHERE o.id = ?
            """;

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, orderID);
            ResultSet resultSet = preparedStatement.executeQuery();
            model.setRowCount(0);
            double totalAmount = 0.0;
            boolean hasData = false;

            while (resultSet.next()) {
                hasData = true;

                String invoiceID = resultSet.getString("invoice_id");
                String customerIDResult = resultSet.getString("customer_id");
                String customerName = resultSet.getString("customer_name");
                String productID = resultSet.getString("product_id");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                double total = Double.parseDouble(String.format("%.2f", price * quantity));

                model.addRow(new Object[]{invoiceID, customerIDResult, customerName, productID, price, quantity, total});

                totalAmount += total;
            }
            if (!hasData) {
                JOptionPane.showMessageDialog(this, "No data found for Order ID: " + orderID);
            }
            lblTotalAmount.setText("Total Amount: " + totalAmount);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data from database: " + ex.getMessage());
        }
    }
}
