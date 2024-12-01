package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class SalesManagementUI extends JPanel {
    private JTextField txtCustomerID, txtCustomerName, txtProductID, txtPrice, txtQuantity;
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<Object[]> salesList = new ArrayList<>();

    public SalesManagementUI() {
        setLayout(new BorderLayout());

        // Table to display sales management information
        String[] columnNames = {"Customer ID", "Customer Name", "Product ID", "Price", "Quantity"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Input form
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));

        panel.add(new JLabel("Customer ID:"));
        txtCustomerID = new JTextField();
        panel.add(txtCustomerID);

        panel.add(new JLabel("Customer Name:"));
        txtCustomerName = new JTextField();
        panel.add(txtCustomerName);

        panel.add(new JLabel("Product ID:"));
        txtProductID = new JTextField();
        panel.add(txtProductID);

        panel.add(new JLabel("Price:"));
        txtPrice = new JTextField();
        panel.add(txtPrice);

        panel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        panel.add(txtQuantity);

        // Add button
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String customerID = txtCustomerID.getText();
                String customerName = txtCustomerName.getText();
                String productID = txtProductID.getText();
                double price;
                int quantity;

                try {
                    price = Double.parseDouble(txtPrice.getText());
                    quantity = Integer.parseInt(txtQuantity.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SalesManagementUI.this, "Invalid price or quantity!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Object[] rowData = {customerID, customerName, productID, price, quantity};
                salesList.add(rowData);
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
                    salesList.remove(selectedRow);
                    model.removeRow(selectedRow);
                }
            }
        });
        panel.add(btnDelete);

        // Read file button
        JButton btnReadFile = new JButton("Read file");
        btnReadFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadSalesFromFile("Sales.txt");
            }
        });
        panel.add(btnReadFile);

        // Write file button
        JButton btnWriteFile = new JButton("Write file");
        btnWriteFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("Sales.txt"))) {
                    for (Object[] row : salesList) {
                        bw.write(row[0] + "," + row[1] + "," + row[2] + "," + row[3] + "," + row[4]);
                        bw.newLine();
                    }
                    JOptionPane.showMessageDialog(SalesManagementUI.this, "File written successfully!", "Message", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(btnWriteFile);

        add(panel, BorderLayout.EAST);
    }

    private void clearFields() {
        txtCustomerID.setText("");
        txtCustomerName.setText("");
        txtProductID.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
    }

    private void loadSalesFromFile(String filename) {
        salesList.clear();
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    Object[] rowData = {data[0], data[1], data[2], Double.parseDouble(data[3]), Integer.parseInt(data[4])};
                    salesList.add(rowData);
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
