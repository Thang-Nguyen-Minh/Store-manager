package GUI;

import javax.swing.*;

public class MainUI extends javax.swing.JFrame {
    private JFrame frame;
    private JTabbedPane tabbedPane;

    public MainUI() {
        frame = new JFrame("Sales Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        // Add each UI section to its corresponding tab
        tabbedPane.addTab("Customers", new CustomerUI());
        tabbedPane.addTab("Products", new ProductUI());
        tabbedPane.addTab("Sales Management", new SalesManagementUI());
        tabbedPane.addTab("Invoices", new InvoiceUI());

        frame.add(tabbedPane);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

     public static void main(String[] args) {
         SwingUtilities.invokeLater(MainUI::new);
     }
}
