package com.example.shop_manager.Response;

import com.example.shop_manager.Entity.Customer;
import com.example.shop_manager.Service.DatabaseConnection;
import com.example.shop_manager.Service.ICustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDataBase extends JPanel implements ICustomerService<Customer> {

    private JTextField txtId, txtName, txtAddress, txtPhoneNumber, txtSearchId;
    private JTable table;
    private DefaultTableModel model;
    private List<Customer> customerList = new ArrayList<>();

    public CustomerDataBase() {
        setLayout(new BorderLayout());
        String[] columnNames = {"ID", "Name", "Address", "Phone Number"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        setupForm(panel);
        setupButtons(panel);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    txtId.setText(model.getValueAt(selectedRow, 0).toString());
                    txtName.setText(model.getValueAt(selectedRow, 1).toString());
                    txtAddress.setText(model.getValueAt(selectedRow, 2).toString());
                    txtPhoneNumber.setText(model.getValueAt(selectedRow, 3).toString());
                }
            }
        });

        add(panel, BorderLayout.EAST);

        // Thêm giao diện tìm kiếm
        setupSearchPanel();
    }

    private void setupForm(JPanel panel) {
        panel.add(new JLabel("ID:"));
        txtId = new JTextField();
        panel.add(txtId);

        panel.add(new JLabel("Name:"));
        txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        panel.add(txtAddress);

        panel.add(new JLabel("Phone Number:"));
        txtPhoneNumber = new JTextField();
        panel.add(txtPhoneNumber);
    }

    private void setupButtons(JPanel panel) {
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> addCustomer(new Customer(txtId.getText(), txtName.getText(), txtAddress.getText(),
                txtPhoneNumber.getText())));
        panel.add(btnAdd);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(e -> updateCustomer(new Customer(txtId.getText(), txtName.getText(),
                txtAddress.getText(), txtPhoneNumber.getText())));
        panel.add(btnUpdate);

        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String id = model.getValueAt(selectedRow, 0).toString();
                deleteCustomer(id);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        panel.add(btnDelete);

        JButton btnLoadData = new JButton("Load Data");
        btnLoadData.addActionListener(e -> {
            model.setRowCount(0);
            loadAllCustomers().forEach(this::addRowToTable);
        });
        panel.add(btnLoadData);
    }

    private void setupSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by ID:"));

        txtSearchId = new JTextField(10);
        searchPanel.add(txtSearchId);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> {
            String searchId = txtSearchId.getText().trim();
            if (searchId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Customer ID to search.", "Input Error", JOptionPane.WARNING_MESSAGE);
            } else {
                model.setRowCount(0);
                List<Customer> searchResults = searchCustomerById(searchId);
                if (searchResults.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No customer found with ID: " + searchId, "Search Result", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    searchResults.forEach(this::addRowToTable);
                }
            }
        });
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);
    }

    private void addRowToTable(Customer customer) {
        model.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getAddress(),
                customer.getPhoneNumber()
        });
    }

    @Override
    public void addCustomer(Customer customer) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Customer (id, name, address, phoneNumber) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, customer.getId());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getAddress());
            statement.setString(4, customer.getPhoneNumber());
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                customerList.add(customer);
                addRowToTable(customer);
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void updateCustomer(Customer customer) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "UPDATE Customer SET name = ?, address = ?, phoneNumber = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPhoneNumber());
            statement.setString(4, customer.getId());
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                int selectedRow = table.getSelectedRow();
                model.setValueAt(customer.getName(), selectedRow, 1);
                model.setValueAt(customer.getAddress(), selectedRow, 2);
                model.setValueAt(customer.getPhoneNumber(), selectedRow, 3);
                JOptionPane.showMessageDialog(this, "Customer updated successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void deleteCustomer(String id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM Customer WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, id);
                int rowsDeleted = statement.executeUpdate();

                if (rowsDeleted > 0) {
                    int selectedRow = table.getSelectedRow();
                    customerList.remove(selectedRow);
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public List<Customer> loadAllCustomers() {
        customerList.clear();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Customer";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phoneNumber")
                );
                customerList.add(customer);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return customerList;
    }

    @Override
    public List<Customer> searchCustomerById(String id) {
        List<Customer> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Customer WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phoneNumber")
                );
                result.add(customer);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }
}
