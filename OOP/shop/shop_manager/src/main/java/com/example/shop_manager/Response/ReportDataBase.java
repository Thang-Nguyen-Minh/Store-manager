package com.example.shop_manager.Response;

import com.example.shop_manager.Entity.Report;
import com.example.shop_manager.Service.DatabaseConnection;
import com.example.shop_manager.Service.IReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class ReportDataBase extends JPanel implements IReportService {
    private JTextField txtCustomerID;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotalAmount;
    private ArrayList<Report> reportList = new ArrayList<>();

    public ReportDataBase() {
        setLayout(new BorderLayout());

        String[] columnNames = {"Invoice ID", "Customer ID", "Customer Name", "Product ID", "Price", "Quantity", "Total"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        JPanel panelRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnPrintInvoices = new JButton("Print All Invoices");
        btnPrintInvoices.addActionListener(e -> printAllInvoices());
        panelRow1.add(btnPrintInvoices);

        JPanel panelRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelRow2.add(new JLabel("Enter Customer ID:"));

        txtCustomerID = new JTextField(10);
        panelRow2.add(txtCustomerID);

        JButton btnSearchInvoices = new JButton("Search");
        btnSearchInvoices.addActionListener(e -> searchInvoices());
        panelRow2.add(btnSearchInvoices);

        lblTotalAmount = new JLabel("Total Amount: 0.0");
        panelRow2.add(lblTotalAmount);

        controlPanel.add(panelRow1);
        controlPanel.add(panelRow2);
        add(controlPanel, BorderLayout.SOUTH);
    }

    public void printAllInvoices() {
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
            reportList.clear();
            double totalAmount = 0;

            while (resultSet.next()) {
                Report report = new Report(
                        resultSet.getString("invoice_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("product_id"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("total")
                );
                reportList.add(report);

                model.addRow(new Object[]{
                        report.getOrderId(),
                        report.getCustomerId(),
                        report.getCustomerName(),
                        report.getProductId(),
                        report.getPrice(),
                        report.getQuantity(),
                        report.getTotal()
                });
                totalAmount += report.getTotal();
            }
            lblTotalAmount.setText("Total Amount: " + (long)Math.round(totalAmount));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage());
        }
    }

    public void searchInvoices() {
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
            reportList.clear();
            double totalAmount = 0;

            while (resultSet.next()) {
                Report report = new Report(
                        resultSet.getString("invoice_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("product_id"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("total")
                );
                reportList.add(report);

                model.addRow(new Object[]{
                        report.getOrderId(),
                        report.getCustomerId(),
                        report.getCustomerName(),
                        report.getProductId(),
                        report.getPrice(),
                        report.getQuantity(),
                        report.getTotal()
                });
                totalAmount += report.getTotal();
            }
            lblTotalAmount.setText("Total Amount: " +(long) Math.round(totalAmount));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage());
        }
    }
}
