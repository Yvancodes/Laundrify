package Laundry;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class UserManagementDialog extends JDialog {

    private final Color BG_MAIN       = new Color(245, 248, 252);
    private final Color CARD_WHITE     = new Color(255, 255, 255);
    private final Color PRIMARY_BLUE   = new Color(0, 102, 204);
    private final Color TEXT_DARK      = new Color(20, 35, 55);
    private final Color DANGER_RED     = new Color(220, 53, 69);
    private final Color SUCCESS_GREEN  = new Color(40, 167, 69);
    private final Color BORDER_LIGHT   = new Color(210, 225, 240);

    private DefaultTableModel adminModel;
    private JTable adminTable;
    private DefaultTableModel cashierModel;
    private JTable cashierTable;
    private DefaultTableModel customerModel;
    private JTable customerTable;

    public UserManagementDialog(JFrame parent) {
        super(parent, "User & Customer Management", true);
        setSize(850, 650);
        setLocationRelativeTo(parent);

        JPanel contentPane = new JPanel(new BorderLayout(0, 20));
        contentPane.setBackground(BG_MAIN);
        contentPane.setBorder(new EmptyBorder(20, 25, 20, 25));
        setContentPane(contentPane);

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

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(CARD_WHITE);
        tabbedPane.addTab("System Admins",    createAdminPanel());
        tabbedPane.addTab("Cashiers (Staff)", createCashierPanel());
        tabbedPane.addTab("Customers",        createCustomerPanel());
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        loadAdmins("");
        loadCashiers("");
        loadCustomers("");
    }

    // ══════════════════════════════════════════════
    //  ADMIN PANEL  (unchanged layout)
    // ══════════════════════════════════════════════
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 10, 10, 10));

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

        adminModel = new DefaultTableModel(new String[]{"ID", "Username", "Role"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        adminTable = new JTable(adminModel);
        styleTable(adminTable);
        JScrollPane sp = new JScrollPane(adminTable);
        sp.setBorder(new LineBorder(BORDER_LIGHT, 2, true));
        panel.add(sp, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);
        JButton btnAdd       = new JButton("ADD NEW ADMIN");
        JButton btnResetPass = new JButton("RESET PASSWORD");
        JButton btnDelete    = new JButton("DELETE ADMIN");
        styleButton(btnAdd,       SUCCESS_GREEN, Color.WHITE);
        styleButton(btnResetPass, PRIMARY_BLUE,  Color.WHITE);
        styleButton(btnDelete,    DANGER_RED,    Color.WHITE);
        btnAdd.addActionListener(e       -> showAddUserDialog("Admin"));
        btnResetPass.addActionListener(e -> resetUserPassword(adminTable, adminModel));
        btnDelete.addActionListener(e    -> deleteUser("Admin", adminTable, adminModel, "users"));
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
             PreparedStatement ps = conn.prepareStatement(query)) {
            if (!keyword.isEmpty()) ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                adminModel.addRow(new Object[]{rs.getInt("id"), rs.getString("username"), rs.getString("role")});
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ══════════════════════════════════════════════
    //  CASHIER PANEL  (unchanged layout)
    // ══════════════════════════════════════════════
    private JPanel createCashierPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 10, 10, 10));

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

        cashierModel = new DefaultTableModel(new String[]{"ID", "Username", "Role"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        cashierTable = new JTable(cashierModel);
        styleTable(cashierTable);
        JScrollPane sp = new JScrollPane(cashierTable);
        sp.setBorder(new LineBorder(BORDER_LIGHT, 2, true));
        panel.add(sp, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);
        JButton btnAdd       = new JButton("ADD NEW CASHIER");
        JButton btnResetPass = new JButton("RESET PASSWORD");
        JButton btnDelete    = new JButton("DELETE CASHIER");
        styleButton(btnAdd,       SUCCESS_GREEN, Color.WHITE);
        styleButton(btnResetPass, PRIMARY_BLUE,  Color.WHITE);
        styleButton(btnDelete,    DANGER_RED,    Color.WHITE);
        btnAdd.addActionListener(e       -> showAddUserDialog("Cashier"));
        btnResetPass.addActionListener(e -> resetUserPassword(cashierTable, cashierModel));
        btnDelete.addActionListener(e    -> deleteUser("Cashier", cashierTable, cashierModel, "users"));
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
             PreparedStatement ps = conn.prepareStatement(query)) {
            if (!keyword.isEmpty()) ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                cashierModel.addRow(new Object[]{rs.getInt("id"), rs.getString("username"), rs.getString("role")});
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ══════════════════════════════════════════════
    //  CUSTOMER PANEL  (unchanged layout)
    // ══════════════════════════════════════════════
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 10, 10, 10));

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

        customerModel = new DefaultTableModel(new String[]{"ID", "Name", "Phone Number"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        customerTable = new JTable(customerModel);
        styleTable(customerTable);
        JScrollPane sp = new JScrollPane(customerTable);
        sp.setBorder(new LineBorder(BORDER_LIGHT, 2, true));
        panel.add(sp, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);
        JButton btnHistory = new JButton("VIEW HISTORY");
        JButton btnEdit    = new JButton("EDIT INFO");
        JButton btnDelete  = new JButton("DELETE CUSTOMER");
        styleButton(btnHistory, SUCCESS_GREEN, Color.WHITE);
        styleButton(btnEdit,    PRIMARY_BLUE,  Color.WHITE);
        styleButton(btnDelete,  DANGER_RED,    Color.WHITE);
        btnHistory.addActionListener(e -> viewCustomerHistory());
        btnEdit.addActionListener(e    -> editCustomer());
        btnDelete.addActionListener(e  -> deleteUser("Customer", customerTable, customerModel, "customers"));
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
             PreparedStatement ps = conn.prepareStatement(query)) {
            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                customerModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("phone")});
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ══════════════════════════════════════════════
    //  ADD USER DIALOG  — with full validation
    // ══════════════════════════════════════════════
    private void showAddUserDialog(String role) {
        // ── Build a proper panel so we can add labels beneath fields ──
        JTextField      txtUsername = new JTextField(22);
        JPasswordField  txtPassword = new JPasswordField(22);
        JPasswordField  txtConfirm  = new JPasswordField(22);
        JProgressBar    strengthBar = new JProgressBar(0, 3);
        JLabel          lblStrength = new JLabel(" ");

        // Username: block digits & symbols at the source
        applyUsernameFilter(txtUsername);

        // Strength bar styling
        strengthBar.setStringPainted(false);
        strengthBar.setBorderPainted(false);
        strengthBar.setPreferredSize(new Dimension(220, 7));
        strengthBar.setBackground(new Color(220, 220, 220));
        strengthBar.setForeground(new Color(220, 220, 220));
        lblStrength.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStrength.setForeground(Color.GRAY);

        // Live strength update
        txtPassword.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            void refresh() { updateStrengthBar(new String(txtPassword.getPassword()), strengthBar, lblStrength); }
        });

        JLabel lblUsernameRule = new JLabel("Letters, numbers, underscores · starts with a letter · 3–20 chars");
        lblUsernameRule.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblUsernameRule.setForeground(new Color(130, 130, 130));

        JLabel lblPassRule = new JLabel("Min 8 chars · at least 1 letter · at least 1 number");
        lblPassRule.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblPassRule.setForeground(new Color(130, 130, 130));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(340, 230));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 2, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.gridx  = 0; c.gridy = 0; c.gridwidth = 2;
        panel.add(new JLabel("New " + role + " Username:"), c);
        c.gridy = 1; panel.add(txtUsername,   c);
        c.gridy = 2; panel.add(lblUsernameRule, c);
        c.gridy = 3; panel.add(new JLabel("Password:"), c);
        c.gridy = 4; panel.add(txtPassword,   c);
        c.gridy = 5; panel.add(strengthBar,   c);
        c.gridy = 6; panel.add(lblStrength,   c);
        c.gridy = 7; panel.add(lblPassRule,   c);
        c.gridy = 8; panel.add(new JLabel("Confirm Password:"), c);
        c.gridy = 9; panel.add(txtConfirm,    c);

        int option = JOptionPane.showConfirmDialog(
            this, panel, "Create New " + role,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) return;

        String username = txtUsername.getText().trim();
        String pass     = new String(txtPassword.getPassword()).trim();
        String confirm  = new String(txtConfirm.getPassword()).trim();

        // ── Validate username ──
        if (username.isEmpty()) {
            showError("Username cannot be empty."); return;
        }
        if (!username.matches("^[a-zA-Z][a-zA-Z0-9_]{2,19}$")) {
            showError("Username must start with a letter and be 3–20 characters.\nNo spaces or special symbols allowed."); return;
        }

        // ── Validate password ──
        if (pass.contains(" ")) {
            showError("Password must not contain spaces."); return;
        }
        String passError = getPasswordError(pass);
        if (passError != null) { showError(passError); return; }

        if (!pass.equals(confirm)) {
            showError("Passwords do not match!"); return;
        }

        // ── Write to DB (backend unchanged) ──
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, pass);
            ps.setString(3, role);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, role + " account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            if (role.equalsIgnoreCase("Admin")) loadAdmins(""); else loadCashiers("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Username might already exist!\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════
    //  RESET PASSWORD  — with validation
    // ══════════════════════════════════════════════
    private void resetUserPassword(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id       = model.getValueAt(row, 0).toString();
        String username = model.getValueAt(row, 1).toString();

        // Build a small panel with a strength bar instead of a plain input dialog
        JPasswordField txtNew     = new JPasswordField(22);
        JPasswordField txtConfirm = new JPasswordField(22);
        JProgressBar   bar        = new JProgressBar(0, 3);
        JLabel         lblStr     = new JLabel(" ");

        bar.setStringPainted(false);
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(220, 7));
        bar.setBackground(new Color(220, 220, 220));
        bar.setForeground(new Color(220, 220, 220));
        lblStr.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStr.setForeground(Color.GRAY);

        txtNew.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { refresh(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { refresh(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            void refresh() { updateStrengthBar(new String(txtNew.getPassword()), bar, lblStr); }
        });

        JLabel lblRule = new JLabel("Min 8 chars · at least 1 letter · at least 1 number");
        lblRule.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblRule.setForeground(new Color(130, 130, 130));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(300, 170));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 2, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.gridx  = 0; c.gridy = 0;
        panel.add(new JLabel("New password for  \"" + username + "\":"), c);
        c.gridy = 1; panel.add(txtNew,    c);
        c.gridy = 2; panel.add(bar,       c);
        c.gridy = 3; panel.add(lblStr,    c);
        c.gridy = 4; panel.add(lblRule,   c);
        c.gridy = 5; panel.add(new JLabel("Confirm new password:"), c);
        c.gridy = 6; panel.add(txtConfirm, c);

        int option = JOptionPane.showConfirmDialog(
            this, panel, "Reset Password",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) return;

        String newPass  = new String(txtNew.getPassword()).trim();
        String confirm  = new String(txtConfirm.getPassword()).trim();

        // ── Validate ──
        if (newPass.contains(" ")) {
            showError("Password must not contain spaces."); return;
        }
        String passError = getPasswordError(newPass);
        if (passError != null) { showError(passError); return; }

        if (!newPass.equals(confirm)) {
            showError("Passwords do not match!"); return;
        }

        // ── Write to DB (backend unchanged) ──
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE users SET password = ? WHERE id = ?")) {
            ps.setString(1, newPass);
            ps.setString(2, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Password updated successfully for " + username + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════
    //  EDIT CUSTOMER  — with validation
    // ══════════════════════════════════════════════
    private void editCustomer() {
        int row = customerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id           = customerModel.getValueAt(row, 0).toString();
        String currentName  = customerModel.getValueAt(row, 1).toString();
        String currentPhone = customerModel.getValueAt(row, 2).toString();

        JTextField txtName  = new JTextField(currentName, 22);
        JTextField txtPhone = new JTextField(currentPhone, 22);

        // ── Block digits from the name field ──
        applyNameFilter(txtName);

        // ── Block non-digits from the phone field ──
        applyPhoneFilter(txtPhone);

        JLabel lblNameRule  = new JLabel("Letters and spaces only — no numbers");
        JLabel lblPhoneRule = new JLabel("Numbers only · 11 digits · must start with 09");
        lblNameRule .setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblPhoneRule.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblNameRule .setForeground(new Color(130, 130, 130));
        lblPhoneRule.setForeground(new Color(130, 130, 130));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(300, 140));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 2, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.gridx  = 0; c.gridy = 0;
        panel.add(new JLabel("Customer Name:"), c);
        c.gridy = 1; panel.add(txtName,     c);
        c.gridy = 2; panel.add(lblNameRule, c);
        c.gridy = 3; panel.add(new JLabel("Phone Number:"), c);
        c.gridy = 4; panel.add(txtPhone,     c);
        c.gridy = 5; panel.add(lblPhoneRule, c);

        int option = JOptionPane.showConfirmDialog(
            this, panel, "Edit Customer Info",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) return;

        String name  = txtName .getText().trim();
        String phone = txtPhone.getText().trim();

        // ── Validate name ──
        if (name.isEmpty()) {
            showError("Customer name cannot be empty."); return;
        }
        if (!name.matches("^[a-zA-Z][a-zA-Z .'-]{1,49}$")) {
            showError("Name must contain letters only (spaces, hyphens, apostrophes allowed)."); return;
        }

        // ── Validate phone ──
        if (!phone.matches("^09\\d{9}$")) {
            showError("Phone number must be 11 digits and start with 09\n(e.g. 09123456789)."); return;
        }

        // ── Write to DB (backend unchanged) ──
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE customers SET name = ?, phone = ? WHERE id = ?")) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer info updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadCustomers("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════
    //  VIEW HISTORY  (unchanged)
    // ══════════════════════════════════════════════
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

        DefaultTableModel historyModel = new DefaultTableModel(
            new String[]{"Invoice", "Items", "Total (₱)", "Status", "Date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable historyTable = new JTable(historyModel);
        styleTable(historyTable);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT invoice_number, items, total_amount, status, sale_date FROM sales WHERE customer_name = ? ORDER BY sale_date DESC")) {
            ps.setString(1, customerName);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                historyModel.addRow(new Object[]{
                    rs.getString("invoice_number"),
                    rs.getString("items"),
                    String.format("%.2f", rs.getDouble("total_amount")),
                    rs.getString("status"),
                    rs.getString("sale_date")
                });
        } catch (Exception ex) { ex.printStackTrace(); }

        JScrollPane sp = new JScrollPane(historyTable);
        sp.setBorder(new LineBorder(BORDER_LIGHT, 2, true));
        historyDialog.add(sp, BorderLayout.CENTER);

        JButton btnClose = new JButton("CLOSE");
        styleButton(btnClose, TEXT_DARK, Color.WHITE);
        btnClose.addActionListener(e -> historyDialog.dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnClose);
        historyDialog.add(bottomPanel, BorderLayout.SOUTH);
        historyDialog.setVisible(true);
    }

    // ══════════════════════════════════════════════
    //  DELETE USER  (unchanged)
    // ══════════════════════════════════════════════
    private void deleteUser(String type, JTable table, DefaultTableModel model, String tableName) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a " + type.toLowerCase() + " to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id          = model.getValueAt(row, 0).toString();
        String nameOrUser  = model.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the " + type.toLowerCase() + " '" + nameOrUser + "'?\nThis action cannot be undone.",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, type + " deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            if (tableName.equals("users")) {
                if (type.equalsIgnoreCase("Admin")) loadAdmins(""); else loadCashiers("");
            } else {
                loadCustomers("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════
    //  VALIDATION HELPERS
    // ══════════════════════════════════════════════

    /**
     * Returns an error message if the password fails the rules, or null if it passes.
     * Rules: min 8 chars, at least one letter, at least one digit.
     */
    private String getPasswordError(String password) {
        if (password == null || password.isEmpty())
            return "Password cannot be empty.";
        if (password.length() < 8)
            return "Password must be at least 8 characters long.";
        if (!password.matches(".*[a-zA-Z].*"))
            return "Password must contain at least one letter.";
        if (!password.matches(".*[0-9].*"))
            return "Password must contain at least one number.";
        return null; // all good
    }

    /**
     * Updates the JProgressBar and label to show Weak / Fair / Strong.
     * Score: +1 length≥8, +1 has letter, +1 has digit, special char lifts score by 1 (capped at 3).
     */
    private void updateStrengthBar(String pw, JProgressBar bar, JLabel lbl) {
        if (pw == null || pw.isEmpty()) {
            bar.setValue(0);
            bar.setForeground(new Color(220, 220, 220));
            lbl.setText(" ");
            return;
        }
        int score = 0;
        if (pw.length() >= 8)              score++;
        if (pw.matches(".*[a-zA-Z].*"))    score++;
        if (pw.matches(".*[0-9].*"))       score++;
        // special character boosts Fair → Strong
        if (pw.matches(".*[^a-zA-Z0-9].*") && score == 2) score = 3;

        bar.setValue(score);
        switch (score) {
            case 1:
                bar.setForeground(new Color(220, 53, 53));
                lbl.setForeground(new Color(180, 30, 30));
                lbl.setText("Weak — add letters, numbers or more characters");
                break;
            case 2:
                bar.setForeground(new Color(230, 140, 30));
                lbl.setForeground(new Color(180, 100, 0));
                lbl.setText("Fair — add a number or special character");
                break;
            case 3:
                bar.setForeground(new Color(40, 167, 69));
                lbl.setForeground(new Color(30, 130, 50));
                lbl.setText("Strong \u2713");
                break;
            default:
                bar.setForeground(new Color(220, 220, 220));
                lbl.setText(" ");
        }
    }

    /**
     * DocumentFilter that allows only letters, digits, and underscores in username fields.
     * Spaces and all other special characters are blocked at the keystroke level.
     * The first-letter and 3-20 char rules are enforced on submit.
     */
    private void applyUsernameFilter(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int off, String text, AttributeSet a)
                    throws BadLocationException {
                // \\S matches non-whitespace; combined with word chars = no spaces allowed
                if (text != null && text.matches("[a-zA-Z0-9_]+"))
                    super.insertString(fb, off, text, a);
            }
            @Override
            public void replace(FilterBypass fb, int off, int len, String text, AttributeSet a)
                    throws BadLocationException {
                if (text != null && (text.isEmpty() || text.matches("[a-zA-Z0-9_]+")))
                    super.replace(fb, off, len, text, a);
            }
        });
    }

    /**
     * DocumentFilter that blocks digits from name fields entirely.
     */
    private void applyNameFilter(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int off, String text, AttributeSet a)
                    throws BadLocationException {
                if (text != null && !text.matches(".*\\d.*"))
                    super.insertString(fb, off, text, a);
            }
            @Override
            public void replace(FilterBypass fb, int off, int len, String text, AttributeSet a)
                    throws BadLocationException {
                if (text != null && !text.matches(".*\\d.*"))
                    super.replace(fb, off, len, text, a);
            }
        });
    }

    /**
     * DocumentFilter that allows only digits in phone fields.
     */
    private void applyPhoneFilter(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int off, String text, AttributeSet a)
                    throws BadLocationException {
                if (text != null && text.matches("\\d*"))
                    super.insertString(fb, off, text, a);
            }
            @Override
            public void replace(FilterBypass fb, int off, int len, String text, AttributeSet a)
                    throws BadLocationException {
                if (text != null && text.matches("\\d*"))
                    super.replace(fb, off, len, text, a);
            }
        });
    }

    /** Shortcut for error dialogs. */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    // ══════════════════════════════════════════════
    //  STYLE HELPERS  (unchanged)
    // ══════════════════════════════════════════════
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
            public void mouseExited(MouseEvent e)  { button.setBackground(bg); }
        });
    }
}