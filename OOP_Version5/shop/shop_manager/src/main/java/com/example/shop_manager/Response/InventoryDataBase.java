package com.example.shop_manager.Response;

import com.example.shop_manager.Main.DatabaseConnection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class
InventoryDataBase extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchNameField;
    private JTextField searchCategoryField;

    public InventoryDataBase() {
        setLayout(new BorderLayout());
        String[] columnNames = {"Product ID", "Product Name", "Category", "Price", "Quantity"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        searchPanel.add(new JLabel("Search by Product Name:"));
        searchNameField = new JTextField();
        searchPanel.add(searchNameField);

        searchPanel.add(new JLabel("Search by Category:"));
        searchCategoryField = new JTextField();
        searchPanel.add(searchCategoryField);

        add(searchPanel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel();
        JButton btnLoad = new JButton("Load All Products");
        controlPanel.add(btnLoad);
        btnLoad.addActionListener(e -> loadProductsFromDatabase());

        add(controlPanel, BorderLayout.SOUTH);

        // Add listeners for live search
        searchNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchProducts();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchProducts();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchProducts();

            }
        });

        searchCategoryField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchProducts();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchProducts();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchProducts();
            }
        });
    }

    private void loadProductsFromDatabase() {
        model.setRowCount(0);
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM product";
            ResultSet resultSet = connection.createStatement().executeQuery(sql);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                model.addRow(new Object[]{id, name, category, price, quantity});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchProducts() {
        String nameKeyword = searchNameField.getText().trim();
        String categoryKeyword = searchCategoryField.getText().trim();

        model.setRowCount(0); // Clear table

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM product WHERE name LIKE ? AND category LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nameKeyword + "%"); // Search by starting characters of name
            statement.setString(2, categoryKeyword + "%"); // Search by starting characters of category
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                model.addRow(new Object[]{id, name, category, price, quantity});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
