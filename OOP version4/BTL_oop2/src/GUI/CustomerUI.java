package GUI;

import Model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

public class CustomerUI extends JPanel {
    private JTextField txtCustomerID, txtCustomerName, txtAddress, txtPhoneNumber;
    private JButton btnAdd, btnDelete, btnUpdate, btnReadFile, btnWriteFile;
    private JTable table;
    private DefaultTableModel model;
    private HashMap<String, Customer> customerMap;

    public CustomerUI() {
        setLayout(new BorderLayout());

        customerMap = new HashMap<>();


        String[] columnNames = {"Customer ID", "Customer Name", "Address", "Phone Number"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

       
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));

        panel.add(new JLabel("Customer ID:"));
        txtCustomerID = new JTextField();
        panel.add(txtCustomerID);

        panel.add(new JLabel("Customer Name:"));
        txtCustomerName = new JTextField();
        panel.add(txtCustomerName);

        panel.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        panel.add(txtAddress);

        panel.add(new JLabel("Phone Number:"));
        txtPhoneNumber = new JTextField();
        panel.add(txtPhoneNumber);

       
        btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });
        panel.add(btnAdd);

        btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });
        panel.add(btnDelete);

        btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateCustomer();
            }
        });
        panel.add(btnUpdate);

        // Add file operations buttons
        btnReadFile = new JButton("Read File");
        btnReadFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                readCustomersFromFile("customers.txt");
            }
        });
        panel.add(btnReadFile);

        btnWriteFile = new JButton("Write File");
        btnWriteFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writeCustomersToFile("customers.txt");
            }
        });
        panel.add(btnWriteFile);

        add(panel, BorderLayout.EAST);
    }

  
    private void addCustomer() {
        String customerID = txtCustomerID.getText();
        String customerName = txtCustomerName.getText();
        String address = txtAddress.getText();
        String phoneNumber = txtPhoneNumber.getText();

        if (customerID.isEmpty() || customerName.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = new Customer(customerID, customerName, address, phoneNumber);
        customerMap.put(customerID, customer);

        model.addRow(new Object[]{customerID, customerName, address, phoneNumber});
        clearFields();
    }


    private void deleteCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String customerID = (String) model.getValueAt(selectedRow, 0);
            customerMap.remove(customerID);
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Select a customer to delete", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   
    private void updateCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String customerID = (String) model.getValueAt(selectedRow, 0);
            Customer customer = customerMap.get(customerID);
            if (customer != null) {
                customer.setCustomerName(txtCustomerName.getText());
                customer.setAddress(txtAddress.getText());
                customer.setPhoneNumber(txtPhoneNumber.getText());

                // Update the table view
                model.setValueAt(customer.getCustomerName(), selectedRow, 1);
                model.setValueAt(customer.getAddress(), selectedRow, 2);
                model.setValueAt(customer.getPhoneNumber(), selectedRow, 3);
                clearFields();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a customer to update", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

 
    private void clearFields() {
        txtCustomerID.setText("");
        txtCustomerName.setText("");
        txtAddress.setText("");
        txtPhoneNumber.setText("");
    }

   
    private void readCustomersFromFile(String filename) {
        customerMap.clear();
        model.setRowCount(0); // Clear the table before loading new data
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    String customerID = data[0];
                    String customerName = data[1];
                    String address = data[2];
                    String phoneNumber = data[3];

                    Customer customer = new Customer(customerID, customerName, address, phoneNumber);
                    customerMap.put(customerID, customer);

                    // Add customer to the table
                    model.addRow(new Object[]{customerID, customerName, address, phoneNumber});
                }
            }
            JOptionPane.showMessageDialog(this, "Customers loaded from file.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading from file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void writeCustomersToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Customer customer : customerMap.values()) {
                bw.write(customer.getCustomerID() + "," +
                        customer.getCustomerName() + "," +
                        customer.getAddress() + "," +
                        customer.getPhoneNumber());
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "Customers saved to file.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
