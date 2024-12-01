package com.example.shop_manager.Response;

import com.example.shop_manager.Entity.Inventory;
import com.example.shop_manager.Service.DatabaseConnection;
import com.example.shop_manager.Service.IInvetoryService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryDataBase extends JPanel implements IInvetoryService {
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

    public void loadProductsFromDatabase() {
        model.setRowCount(0);
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM product";
            ResultSet resultSet = connection.createStatement().executeQuery(sql);

            while (resultSet.next()) {
                Inventory inventory = Inventory.fromResultSet(resultSet);
                model.addRow(new Object[]{
                        inventory.getId(),
                        inventory.getName(),
                        inventory.getCategory(),
                        inventory.getPrice(),
                        inventory.getQuantity()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchProducts() {
        String nameKeyword = searchNameField.getText().trim();
        String categoryKeyword = searchCategoryField.getText().trim();

        model.setRowCount(0); // Clear table

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM product WHERE name LIKE ? AND category LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nameKeyword + "%");
            statement.setString(2, categoryKeyword + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Inventory inventory = Inventory.fromResultSet(resultSet);
                model.addRow(new Object[]{
                        inventory.getId(),
                        inventory.getName(),
                        inventory.getCategory(),
                        inventory.getPrice(),
                        inventory.getQuantity()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

