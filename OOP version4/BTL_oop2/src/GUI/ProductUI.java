package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class ProductUI extends JPanel {
    private JTextField txtProductID, txtProductName, txtPrice;
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<Object[]> productList = new ArrayList<>();

    public ProductUI() {
        setLayout(new BorderLayout());

        // Table to display product information
        String[] columnNames = {"Product ID", "Product Name", "Price"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Input form
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        panel.add(new JLabel("Product ID:"));
        txtProductID = new JTextField();
        panel.add(txtProductID);

        panel.add(new JLabel("Product Name:"));
        txtProductName = new JTextField();
        panel.add(txtProductName);

        panel.add(new JLabel("Price:"));
        txtPrice = new JTextField();
        panel.add(txtPrice);

        // Add button
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String productID = txtProductID.getText();
                String productName = txtProductName.getText();
                double price;

                try {
                    price = Double.parseDouble(txtPrice.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ProductUI.this, "Invalid price!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Object[] rowData = {productID, productName, price};
                productList.add(rowData);
                model.addRow(rowData);

                clearFields();
            }
        });
        panel.add(btnAdd);

        // Delete button
        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    productList.remove(selectedRow);
                    model.removeRow(selectedRow);
                }
            }
        });
        panel.add(btnDelete);

        // Read file button
        JButton btnReadFile = new JButton("Read file");
        btnReadFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadProductsFromFile("Product.txt");
            }
        });
        panel.add(btnReadFile);

        // Write file button
        JButton btnWriteFile = new JButton("Write file");
        btnWriteFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("Product.txt"))) {
                    for (Object[] row : productList) {
                        bw.write(row[0] + "," + row[1] + "," + row[2]);
                        bw.newLine();
                    }
                    JOptionPane.showMessageDialog(ProductUI.this, "File written successfully!", "Message", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(btnWriteFile);

        add(panel, BorderLayout.EAST);
    }

    private void clearFields() {
        txtProductID.setText("");
        txtProductName.setText("");
        txtPrice.setText("");
    }

    private void loadProductsFromFile(String filename) {
        productList.clear();
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    Object[] rowData = {data[0], data[1], Double.parseDouble(data[2])};
                    productList.add(rowData);
                    model.addRow(rowData);
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "File " + filename + " not found!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
