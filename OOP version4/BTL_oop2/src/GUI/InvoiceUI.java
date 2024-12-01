package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvoiceUI extends JPanel {
    private JComboBox<String> cbCustomerID;
    private JTable table;
    private DefaultTableModel model;
    private List<String[]> allData;
    private DefaultComboBoxModel<String> comboBoxModel;
    private JLabel lblTotalAmount;

    public InvoiceUI() {
        setLayout(new BorderLayout());

        String[] columnNames = {"Customer ID", "Customer Name", "Product ID", "Price", "Quantity"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

       
        loadAllDataFromFile();

        
        JPanel panel = new JPanel();

        panel.add(new JLabel("Customer ID:"));
        comboBoxModel = new DefaultComboBoxModel<>();
        cbCustomerID = new JComboBox<>(comboBoxModel);
        panel.add(cbCustomerID);

      
        JButton btnLoadData = new JButton("Load data");
        btnLoadData.addActionListener(e -> loadDataByCustomerID());
        panel.add(btnLoadData);

       
        lblTotalAmount = new JLabel("Total Amount: 0.0");
        panel.add(lblTotalAmount);

       
        JButton btnLoadCustomerIDs = new JButton("Load Customer IDs");
        btnLoadCustomerIDs.addActionListener(e -> loadCustomerIDs());
        panel.add(btnLoadCustomerIDs);

        add(panel, BorderLayout.NORTH);
    }

    private void loadAllDataFromFile() {
        allData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("Sales.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    allData.add(data);
                }
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerIDs() {
        Set<String> customerIDs = new HashSet<>();
        for (String[] data : allData) {
            customerIDs.add(data[0]);
        }
        comboBoxModel.removeAllElements();
        for (String id : customerIDs) {
            comboBoxModel.addElement(id);
        }
    }

    private void loadDataByCustomerID() {
        String selectedCustomerID = (String) cbCustomerID.getSelectedItem();
        model.setRowCount(0); // Clear existing data in the table
        double totalAmount = 0.0;
        for (String[] data : allData) {
            if (data[0].equals(selectedCustomerID)) {
                model.addRow(data);
                double price = Double.parseDouble(data[3]);
                int quantity = Integer.parseInt(data[4]);
                totalAmount += price * quantity;
            }
        }
        lblTotalAmount.setText("Total Amount: " + totalAmount);
    }
}
