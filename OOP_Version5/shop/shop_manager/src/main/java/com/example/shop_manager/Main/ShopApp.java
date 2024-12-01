package com.example.shop_manager.Main;

import javax.swing.*;

import com.example.shop_manager.Response.*;


public class ShopApp extends javax.swing.JFrame{
    private JFrame frame;
    private JTabbedPane tabbedPane;

    public ShopApp() {
        frame = new JFrame("SHOP MANANGEMENT");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Customer", new CustomerDataBase());
        tabbedPane.addTab("Product", new ProductDataBase());
        tabbedPane.addTab("Order", new OrderDataBase());
        tabbedPane.addTab("Report", new ReportDataBase());
        tabbedPane.addTab("Inventory", new InventoryDataBase());

        frame.add(tabbedPane);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ShopApp::new);
    }
}

