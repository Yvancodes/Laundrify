package Laundry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class CustomerDialog extends JDialog {

    private final Color WATER_BLUE_BG = new Color(235, 242, 252);
    private final Color CRISP_WHITE = Color.WHITE;
    private final Color NAVY_TEXT = new Color(15, 35, 65);
    private final Color SUDSY_BORDER = new Color(200, 220, 245);

    private Laundrify parentPOS;

    public CustomerDialog(Laundrify parent) {
        super(parent, "Customer Management", true);
        this.parentPOS = parent;

        setSize(650, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(WATER_BLUE_BG);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 15, 0, 15));

        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnSearch = new JButton("Search Name");
        styleButton(btnSearch, new Color(0, 102, 204), Color.WHITE);

        searchPanel.add(new JLabel("🔍Find Customer: "));
        searchPanel.add(searchField);
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        // TABLE NOW INCLUDES ID
        DefaultTableModel customerModel = new DefaultTableModel(new String[]{"ID", "Name", "Phone"}, 0) {
            @Override
			public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable customerTable = new JTable(customerModel);
        customerTable.setRowHeight(28);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerTable.setSelectionBackground(new Color(0, 102, 204));
        customerTable.setSelectionForeground(Color.WHITE);
        customerTable.setGridColor(new Color(200, 220, 245));
        customerTable.setShowHorizontalLines(true);
        customerTable.setShowVerticalLines(false);
        customerTable.setFillsViewportHeight(true);
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        customerTable.getTableHeader().setBackground(new Color(0, 70, 150));
        customerTable.getTableHeader().setForeground(Color.WHITE);

        Runnable loadCustomers = () -> {
            customerModel.setRowCount(0);
            String keyword = searchField.getText().trim();
            String query = keyword.isEmpty() ? "SELECT id, name, phone FROM customers ORDER BY name ASC"
                                             : "SELECT id, name, phone FROM customers WHERE name LIKE ? ORDER BY name ASC";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                if (!keyword.isEmpty()) {
					pstmt.setString(1, "%" + keyword + "%");
				}
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
					customerModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("phone")});
				}
            } catch (Exception ex) { ex.printStackTrace(); }
        };

        loadCustomers.run();
        btnSearch.addActionListener(e -> loadCustomers.run());

        JPanel tableWrapper = new JPanel(new BorderLayout(0, 10));
        tableWrapper.setOpaque(false);
        JScrollPane tableScroll = new JScrollPane(customerTable);
        tableScroll.setBorder(new LineBorder(new Color(200, 220, 245), 1, true));
        tableWrapper.add(tableScroll, BorderLayout.CENTER);

        JButton btnSelectExisting = new JButton("SELECT EXISTING");
        styleButton(btnSelectExisting, new Color(0, 102, 204), Color.WHITE);
        tableWrapper.add(btnSelectExisting, BorderLayout.SOUTH);
        centerPanel.add(tableWrapper);

        JPanel registerPanel = new JPanel(new GridLayout(6, 1, 0, 5));
        registerPanel.setBackground(CRISP_WHITE);
        registerPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SUDSY_BORDER, 2, true), new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblReg = new JLabel("Register New Customer");
        lblReg.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblReg.setForeground(new Color(0, 102, 204));

        JTextField newNameField = new JTextField();
        JTextField newPhoneField = new JTextField();

        newPhoneField.addKeyListener(new KeyAdapter() {
            @Override
			public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) { e.consume(); return; }
                if (newPhoneField.getText().length() >= 11 && c != KeyEvent.VK_BACK_SPACE) { e.consume(); }
            }
        });

        registerPanel.add(lblReg);
        registerPanel.add(new JLabel("Full Name:"));
        registerPanel.add(newNameField);
        registerPanel.add(new JLabel("Phone Number:"));
        registerPanel.add(newPhoneField);

        JButton btnRegisterNew = new JButton("SAVE & SELECT");
        styleButton(btnRegisterNew, new Color(0, 102, 204), Color.WHITE);
        registerPanel.add(btnRegisterNew);
        centerPanel.add(registerPanel);
        add(centerPanel, BorderLayout.CENTER);

        btnSelectExisting.addActionListener(e -> {
            int row = customerTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a customer first.", "Wait", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // PASS ID TO MAIN SCREEN
            parentPOS.applyCustomerToOrder(
                Integer.parseInt(customerModel.getValueAt(row, 0).toString()),
                customerModel.getValueAt(row, 1).toString(),
                customerModel.getValueAt(row, 2).toString()
            );
            dispose();
        });

        btnRegisterNew.addActionListener(e -> {
            String n = newNameField.getText().trim();
            String p = newPhoneField.getText().trim();
            if (n.isEmpty() || p.isEmpty()) {
				return;
			}
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO customers (name, phone) VALUES (?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, n);
                pstmt.setString(2, p);
                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    parentPOS.applyCustomerToOrder(rs.getInt(1), n, p);
                }
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Name already exists! Use the search bar.", "Duplicate", JOptionPane.ERROR_MESSAGE);
            }
        });
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
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { button.setBackground(bg); }
        });
    }
}