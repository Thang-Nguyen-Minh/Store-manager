package com.example.shop_manager.Response;

import com.example.shop_manager.Entity.Product;
import com.example.shop_manager.Service.DatabaseConnection;
import com.example.shop_manager.Service.IProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDataBase extends JPanel implements IProductService<Product> {

    private JTextField txtName, txtPrice, txtQuantity;
    private JComboBox<String> cmbCategory;
    private JTable table;
    private DefaultTableModel model;
    private List<Product> productList = new ArrayList<>();

    public ProductDataBase() {
        setLayout(new BorderLayout());

        String[] columnNames = {"Product ID", "Product Name", "Category", "Price", "Quantity"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(6, 2)); // Điều chỉnh layout phù hợp với các trường còn lại
        setupForm(panel);
        setupButtons(panel);
        setupSearchPanel();

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    txtName.setText(model.getValueAt(selectedRow, 1).toString());
                    cmbCategory.setSelectedItem(model.getValueAt(selectedRow, 2).toString());
                    txtPrice.setText(model.getValueAt(selectedRow, 3).toString());
                    txtQuantity.setText(model.getValueAt(selectedRow, 4).toString());
                }
            }
        });

        add(panel, BorderLayout.EAST);
    }

    private void setupForm(JPanel panel) {
        panel.add(new JLabel("Product Name:"));
        txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Category:"));
        cmbCategory = new JComboBox<>(new String[]{"Electronics", "Home Appliances", "Food"});
        panel.add(cmbCategory);

        panel.add(new JLabel("Price:"));
        txtPrice = new JTextField();
        panel.add(txtPrice);

        panel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        panel.add(txtQuantity);
    }

    private void setupButtons(JPanel panel) {
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> {
            Product product = new Product(0, txtName.getText(), (String) cmbCategory.getSelectedItem(),
                    Double.parseDouble(txtPrice.getText()), Integer.parseInt(txtQuantity.getText()));
            addProduct(product);
        });
        panel.add(btnAdd);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                Product product = new Product(
                        Integer.parseInt(model.getValueAt(selectedRow, 0).toString()),
                        txtName.getText(),
                        (String) cmbCategory.getSelectedItem(),
                        Double.parseDouble(txtPrice.getText()),
                        Integer.parseInt(txtQuantity.getText())
                );
                updateProduct(product);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to update!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        panel.add(btnUpdate);

        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                deleteProduct(id);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        panel.add(btnDelete);

        JButton btnLoadData = new JButton("Load Data");
        btnLoadData.addActionListener(e -> {
            model.setRowCount(0);
            loadData().forEach(this::addRowToTable);
        });
        panel.add(btnLoadData);
    }

    private void setupSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("Search by Name:"));
        JTextField txtSearchName = new JTextField(10);
        searchPanel.add(txtSearchName);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchProductByName(txtSearchName.getText()));
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.NORTH);
    }

    private void addRowToTable(Product product) {
        model.addRow(new Object[]{
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getQuantity()
        });
    }

    @Override
    public void addProduct(Product product) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Product (name, category, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getQuantity());
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                    productList.add(product);
                    addRowToTable(product);
                    JOptionPane.showMessageDialog(this, "Product added successfully!");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void updateProduct(Product product) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "UPDATE Product SET name = ?, category = ?, price = ?, quantity = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getQuantity());
            statement.setInt(5, product.getId());
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                int selectedRow = table.getSelectedRow();
                model.setValueAt(product.getName(), selectedRow, 1);
                model.setValueAt(product.getCategory(), selectedRow, 2);
                model.setValueAt(product.getPrice(), selectedRow, 3);
                model.setValueAt(product.getQuantity(), selectedRow, 4);
                JOptionPane.showMessageDialog(this, "Product updated successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void deleteProduct(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION){
            try (Connection connection = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM Product WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, id);
                int rowsDeleted = statement.executeUpdate();

                if (rowsDeleted > 0) {
                    int selectedRow = table.getSelectedRow();
                    productList.remove(selectedRow);
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    @Override
    public List<Product> loadData() {
        productList.clear();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Product";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                productList.add(product);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return productList;
    }

    @Override
    public void searchProductByName(String searchName) {
        model.setRowCount(0);
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Product WHERE name LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, searchName + "%");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                addRowToTable(product);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
