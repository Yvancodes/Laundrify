package Laundry;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class UserManagementDialog extends JDialog {

    private final Color BG_MAIN = new Color(245, 248, 252);
    private final Color CARD_WHITE = new Color(255, 255, 255);
    private final Color PRIMARY_BLUE = new Color(0, 102, 204);
    private final Color TEXT_DARK = new Color(20, 35, 55);
    private final Color DANGER_RED = new Color(220, 53, 69);
    private final Color SUCCESS_GREEN = new Color(40, 167, 69); // Green for Add/History
    private final Color BORDER_LIGHT = new Color(210, 225, 240);

    // Admin Table Data
    private DefaultTableModel adminModel;
    private JTable adminTable;

    // Cashier Table Data
    private DefaultTableModel cashierModel;
    private JTable cashierTable;

    // Customer Table Data
    private DefaultTableModel customerModel;
    private JTable customerTable;

    public UserManagementDialog(JFrame parent) {
        super(parent, "User & Customer Management", true);
        setSize(850, 650); // Slightly larger to accommodate new features
        setLocationRelativeTo(parent);

        JPanel contentPane = new JPanel(new BorderLayout(0, 20));
        contentPane.setBackground(BG_MAIN);
        contentPane.setBorder(new EmptyBorder(20, 25, 20, 25));
        setContentPane(contentPane);

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("User Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_BLUE);
        JLabel lblSub = new JLabel("Securely manage Admins, Cashiers, and Customers", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(TEXT_DARK);
        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // --- TABS ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(CARD_WHITE);

        // 1. Admin Tab
        tabbedPane.addTab("System Admins", createAdminPanel());

        // 2. Cashier Tab
        tabbedPane.addTab("Cashiers (Staff)", createCashierPanel());

        // 3. Customer Tab
        tabbedPane.addTab("Customers", createCustomerPanel());

        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // Load initial data
        loadAdmins("");
        loadCashiers("");
        loadCustomers("");
    }

    // ==========================================
    // ADMIN MANAGEMENT
    // ==========================================
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 10, 10, 10));

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        JTextField txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        styleButton(btnSearch, TEXT_DARK, Color.WHITE);
        btnSearch.addActionListener(e -> loadAdmins(txtSearch.getText().trim()));
        searchPanel.add(new JLabel("Search Admin: "));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        adminModel = new DefaultTableModel(new String[]{"ID", "Username", "Role"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        adminTable = new JTable(adminModel);
        styleTable(adminTable);

        JScrollPane scrollPane = new JScrollPane(adminTable);
        scrollPane.setBorder(new LineBorder(BORDER_LIGHT, 2, true));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        JButton btnAdd = new JButton("ADD NEW ADMIN");
        styleButton(btnAdd, SUCCESS_GREEN, Color.WHITE);
        btnAdd.addActionListener(e -> showAddUserDialog("Admin"));

        JButton btnResetPass = new JButton("RESET PASSWORD");
        styleButton(btnResetPass, PRIMARY_BLUE, Color.WHITE);
        btnResetPass.addActionListener(e -> resetUserPassword(adminTable, adminModel));

        JButton btnDelete = new JButton("DELETE ADMIN");
        styleButton(btnDelete, DANGER_RED, Color.WHITE);
        btnDelete.addActionListener(e -> deleteUser("Admin", adminTable, adminModel, "users"));

        actionPanel.add(btnAdd);
        actionPanel.add(btnResetPass);
        actionPanel.add(btnDelete);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadAdmins(String keyword) {
        adminModel.setRowCount(0);
        String query = "SELECT id, username, role FROM users WHERE LOWER(role) = 'admin'";
        if (!keyword.isEmpty()) query += " AND username LIKE ?";
        query += " ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (!keyword.isEmpty()) pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                adminModel.addRow(new Object[]{rs.getInt("id"), rs.getString("username"), rs.getString("role")});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ==========================================
    // CASHIER MANAGEMENT
    // ==========================================
    private JPanel createCashierPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 10, 10, 10));

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        JTextField txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        styleButton(btnSearch, TEXT_DARK, Color.WHITE);
        btnSearch.addActionListener(e -> loadCashiers(txtSearch.getText().trim()));
        searchPanel.add(new JLabel("Search Cashier: "));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        cashierModel = new DefaultTableModel(new String[]{"ID", "Username", "Role"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cashierTable = new JTable(cashierModel);
        styleTable(cashierTable);

        JScrollPane scrollPane = new JScrollPane(cashierTable);
        scrollPane.setBorder(new LineBorder(BORDER_LIGHT, 2, true));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        JButton btnAdd = new JButton("ADD NEW CASHIER");
        styleButton(btnAdd, SUCCESS_GREEN, Color.WHITE);
        btnAdd.addActionListener(e -> showAddUserDialog("Cashier"));

        JButton btnResetPass = new JButton("RESET PASSWORD");
        styleButton(btnResetPass, PRIMARY_BLUE, Color.WHITE);
        btnResetPass.addActionListener(e -> resetUserPassword(cashierTable, cashierModel));

        JButton btnDelete = new JButton("DELETE CASHIER");
        styleButton(btnDelete, DANGER_RED, Color.WHITE);
        btnDelete.addActionListener(e -> deleteUser("Cashier", cashierTable, cashierModel, "users"));

        actionPanel.add(btnAdd);
        actionPanel.add(btnResetPass);
        actionPanel.add(btnDelete);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadCashiers(String keyword) {
        cashierModel.setRowCount(0);
        String query = "SELECT id, username, role FROM users WHERE LOWER(role) = 'cashier'";
        if (!keyword.isEmpty()) query += " AND username LIKE ?";
        query += " ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (!keyword.isEmpty()) pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cashierModel.addRow(new Object[]{rs.getInt("id"), rs.getString("username"), rs.getString("role")});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ==========================================
    // CUSTOMER MANAGEMENT
    // ==========================================
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 10, 10, 10));

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        JTextField txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        styleButton(btnSearch, TEXT_DARK, Color.WHITE);
        btnSearch.addActionListener(e -> loadCustomers(txtSearch.getText().trim()));
        searchPanel.add(new JLabel("Search Name/Phone: "));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        customerModel = new DefaultTableModel(new String[]{"ID", "Name", "Phone Number"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        customerTable = new JTable(customerModel);
        styleTable(customerTable);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(new LineBorder(BORDER_LIGHT, 2, true));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        JButton btnHistory = new JButton("VIEW HISTORY");
        styleButton(btnHistory, SUCCESS_GREEN, Color.WHITE);
        btnHistory.addActionListener(e -> viewCustomerHistory());

        JButton btnEdit = new JButton("EDIT INFO");
        styleButton(btnEdit, PRIMARY_BLUE, Color.WHITE);
        btnEdit.addActionListener(e -> editCustomer());

        JButton btnDelete = new JButton("DELETE CUSTOMER");
        styleButton(btnDelete, DANGER_RED, Color.WHITE);
        btnDelete.addActionListener(e -> deleteUser("Customer", customerTable, customerModel, "customers"));

        actionPanel.add(btnHistory);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadCustomers(String keyword) {
        customerModel.setRowCount(0);
        String query = "SELECT id, name, phone FROM customers";
        if (!keyword.isEmpty()) query += " WHERE name LIKE ? OR phone LIKE ?";
        query += " ORDER BY name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (!keyword.isEmpty()) {
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customerModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("phone")});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ==========================================
    // ACCOUNT CREATION & EDITING LOGIC
    // ==========================================
    private void showAddUserDialog(String role) {
        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField();

        Object[] message = {
            "New " + role + " Username:", txtUsername,
            "Password:", txtPassword,
            "Confirm Password:", txtConfirm
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create New " + role, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (option == JOptionPane.OK_OPTION) {
            String username = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());
            String confirm = new String(txtConfirm.getPassword());

            if (username.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {
                pstmt.setString(1, username);
                pstmt.setString(2, pass);
                pstmt.setString(3, role); // Automatically assigns "Admin" or "Cashier" based on the button clicked
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, role + " account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh the appropriate table
                if (role.equalsIgnoreCase("Admin")) loadAdmins("");
                else loadCashiers("");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Username might already exist! " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetUserPassword(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        String username = model.getValueAt(row, 1).toString();

        String newPassword = JOptionPane.showInputDialog(this, "Enter a new password for " + username + ":", "Reset Password", JOptionPane.QUESTION_MESSAGE);
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET password = ? WHERE id = ?")) {
                pstmt.setString(1, newPassword.trim());
                pstmt.setString(2, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Password updated successfully for " + username + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editCustomer() {
        int row = customerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = customerModel.getValueAt(row, 0).toString();
        String currentName = customerModel.getValueAt(row, 1).toString();
        String currentPhone = customerModel.getValueAt(row, 2).toString();

        JTextField txtName = new JTextField(currentName);
        JTextField txtPhone = new JTextField(currentPhone);
        
        Object[] message = {
            "Customer Name:", txtName,
            "Phone Number:", txtPhone
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Customer Info", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE customers SET name = ?, phone = ? WHERE id = ?")) {
                pstmt.setString(1, txtName.getText().trim());
                pstmt.setString(2, txtPhone.getText().trim());
                pstmt.setString(3, id);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Customer info updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomers(""); // Refresh table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewCustomerHistory() {
        int row = customerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customerName = customerModel.getValueAt(row, 1).toString();

        JDialog historyDialog = new JDialog(this, "Laundry History - " + customerName, true);
        historyDialog.setSize(750, 450);
        historyDialog.setLocationRelativeTo(this);
        historyDialog.setLayout(new BorderLayout(10, 10));
        historyDialog.getContentPane().setBackground(BG_MAIN);
        historyDialog.getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15));

        DefaultTableModel historyModel = new DefaultTableModel(new String[]{"Invoice", "Items", "Total (₱)", "Status", "Date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable historyTable = new JTable(historyModel);
        styleTable(historyTable);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT invoice_number, items, total_amount, status, sale_date FROM sales WHERE customer_name = ? ORDER BY sale_date DESC")) {
            pstmt.setString(1, customerName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                historyModel.addRow(new Object[]{
                    rs.getString("invoice_number"),
                    rs.getString("items"),
                    String.format("%.2f", rs.getDouble("total_amount")),
                    rs.getString("status"),
                    rs.getString("sale_date")
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(new LineBorder(BORDER_LIGHT, 2, true));
        historyDialog.add(scrollPane, BorderLayout.CENTER);

        JButton btnClose = new JButton("CLOSE");
        styleButton(btnClose, TEXT_DARK, Color.WHITE);
        btnClose.addActionListener(e -> historyDialog.dispose());
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnClose);
        historyDialog.add(bottomPanel, BorderLayout.SOUTH);

        historyDialog.setVisible(true);
    }

    // ==========================================
    // SHARED UTILITIES
    // ==========================================
    private void deleteUser(String type, JTable table, DefaultTableModel model, String tableName) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a " + type.toLowerCase() + " to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        String nameOrUser = model.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the " + type.toLowerCase() + " '" + nameOrUser + "'?\nThis action cannot be undone.", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?")) {
                pstmt.setString(1, id);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, type + " deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh the appropriate table based on what we deleted
                if (tableName.equals("users")) {
                    if (type.equalsIgnoreCase("Admin")) loadAdmins("");
                    else loadCashiers("");
                } else {
                    loadCustomers("");
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionBackground(PRIMARY_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(TEXT_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bg.darker(), 1, true), 
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(bg); }
        });
    }
}