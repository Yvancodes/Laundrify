package Laundry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class AddonPricingSetup extends JDialog {

    private final Color BG_BEIGE = new Color(235, 242, 252);
    private final Color CARD_WHITE = new Color(255, 255, 255);
    private final Color TEXT_DARK = new Color(15, 35, 65);
    private final Color BORDER_BEIGE = new Color(200, 220, 245);
    private final Color PRIMARY_BLUE = new Color(0, 102, 204);
    private final Color DANGER_RED = new Color(220, 53, 69); // Red for delete button

    private Map<String, JTextField> priceFields;
    private JPanel formPanel;

    public AddonPricingSetup(JFrame parent) {
        super(parent, "Add-Ons Management", true);
        setSize(550, 650); // Made slightly wider to fit the delete button
        setLocationRelativeTo(parent);

        JPanel contentPane = new JPanel(new BorderLayout(0, 20));
        contentPane.setBackground(BG_BEIGE);
        contentPane.setBorder(new EmptyBorder(20, 25, 20, 25));
        setContentPane(contentPane);

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Add-Ons Setup", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        JLabel lblSub = new JLabel("Update prices, add, or delete items", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 130, 170));
        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // --- CENTER (DYNAMIC LIST) ---
        // Changed to 3 columns to fit the delete button
        formPanel = new JPanel(new GridLayout(0, 3, 10, 15));
        formPanel.setBackground(CARD_WHITE);
        formPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(new LineBorder(BORDER_BEIGE, 2, true));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        priceFields = new HashMap<>();
        loadAddons();

        // --- BOTTOM (ADD NEW & SAVE) ---
        JPanel bottomWrapper = new JPanel(new BorderLayout(0, 15));
        bottomWrapper.setOpaque(false);

        // Add New Item Panel
        JPanel addNewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addNewPanel.setBackground(CARD_WHITE);
        addNewPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_BEIGE, 2, true), new EmptyBorder(5, 5, 5, 5)));

        JTextField txtNewItemName = new JTextField(12);
        txtNewItemName.setBorder(BorderFactory.createTitledBorder("New Item Name"));
        JTextField txtNewItemPrice = new JTextField(6);
        txtNewItemPrice.setBorder(BorderFactory.createTitledBorder("Price"));
        // Allow only digits and a single decimal point in price field
        ((javax.swing.text.AbstractDocument) txtNewItemPrice.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void insertString(javax.swing.text.DocumentFilter.FilterBypass fb, int off, String text, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String result = current.substring(0, off) + text + current.substring(off);
                if (text != null && result.matches("\\d*(\\.\\d{0,2})?")) {
					super.insertString(fb, off, text, a);
				}
            }
            @Override
            public void replace(javax.swing.text.DocumentFilter.FilterBypass fb, int off, int len, String text, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String result = current.substring(0, off) + (text == null ? "" : text) + current.substring(off + len);
                if (text == null || result.matches("\\d*(\\.\\d{0,2})?")) {
					super.replace(fb, off, len, text, a);
				}
            }
        });

        JButton btnAdd = new JButton("ADD");
        styleButton(btnAdd, PRIMARY_BLUE, Color.WHITE, false);
        btnAdd.addActionListener(e -> {
            String name = txtNewItemName.getText().trim();
            String priceStr = txtNewItemPrice.getText().trim();
            if (name.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both name and price.");
                return;
            }
            // Name must not contain digits
            if (name.matches(".*\\d.*")) {
                JOptionPane.showMessageDialog(this, "Add-on name must not contain numbers.", "Invalid Name", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Name must not be numbers-only or symbols-only
            if (!name.matches(".*[a-zA-Z].*")) {
                JOptionPane.showMessageDialog(this, "Add-on name must contain at least one letter.", "Invalid Name", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    JOptionPane.showMessageDialog(this, "Price must be greater than zero.", "Invalid Price", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                insertNewAddon(name, price);
                txtNewItemName.setText("");
                txtNewItemPrice.setText("");
                loadAddons();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price amount.");
            }
        });

        addNewPanel.add(txtNewItemName);
        addNewPanel.add(txtNewItemPrice);
        addNewPanel.add(btnAdd);
        bottomWrapper.add(addNewPanel, BorderLayout.NORTH);

        // Save & Cancel Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);

        JButton btnCancel = new JButton("CLOSE");
        styleButton(btnCancel, TEXT_DARK, CARD_WHITE, false);
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnCancel);

        JButton btnSave = new JButton("SAVE PRICES");
        styleButton(btnSave, TEXT_DARK, CARD_WHITE, false);
        btnSave.addActionListener(e -> savePrices());
        buttonPanel.add(btnSave);

        bottomWrapper.add(buttonPanel, BorderLayout.SOUTH);
        contentPane.add(bottomWrapper, BorderLayout.SOUTH);
    }

    private void loadAddons() {
        formPanel.removeAll();
        priceFields.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT addon_name, price FROM add_ons ORDER BY addon_name ASC")) {
            while (rs.next()) {
                String name = rs.getString("addon_name");

                // 1. Name Label
                JLabel nameLabel = new JLabel(name);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                nameLabel.setForeground(TEXT_DARK);

                // 2. Price Input
                JTextField priceInput = new JTextField(String.format("%.2f", rs.getDouble("price")));
                priceInput.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                priceInput.setHorizontalAlignment(SwingConstants.RIGHT);
                priceInput.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_BEIGE, 1, true), new EmptyBorder(4, 8, 4, 8)));

                // 3. Delete Button
                JButton btnDelete = new JButton("X");
                btnDelete.setFont(new Font("Segoe UI Black", Font.BOLD, 12));
                btnDelete.setBackground(DANGER_RED);
                btnDelete.setForeground(Color.WHITE);
                btnDelete.setFocusPainted(false);
                btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnDelete.setBorder(new LineBorder(DANGER_RED.darker(), 1, true));

                // Delete logic action
                btnDelete.addActionListener(e -> deleteAddon(name));

                formPanel.add(nameLabel);
                formPanel.add(priceInput);
                formPanel.add(btnDelete);

                priceFields.put(name, priceInput);
            }
        } catch (Exception e) { e.printStackTrace(); }

        formPanel.revalidate();
        formPanel.repaint();
    }

    // --- NEW DELETE METHOD ---
    private void deleteAddon(String addonName) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete '" + addonName + "'?\nThis cannot be undone.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM add_ons WHERE addon_name = ?")) {

                pstmt.setString(1, addonName);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Item deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadAddons(); // Refresh the list so it disappears immediately

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting item from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void insertNewAddon(String name, double price) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO add_ons (addon_name, price) VALUES (?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item successfully added!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding item. Ensure the item doesn't already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void savePrices() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE add_ons SET price = ? WHERE addon_name = ?")) {
            for (Map.Entry<String, JTextField> entry : priceFields.entrySet()) {
                String raw = entry.getValue().getText().trim();
                double newPrice = Double.parseDouble(raw);
                if (newPrice <= 0) {
                    JOptionPane.showMessageDialog(this,
                        "Price for '" + entry.getKey() + "' must be greater than zero.",
                        "Invalid Price", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                pstmt.setDouble(1, newPrice);
                pstmt.setString(2, entry.getKey());
                pstmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Prices successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers only.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error while saving.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton button, Color bg, Color fg, boolean isTransparent) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setOpaque(true);
        if (isTransparent) {
			button.setContentAreaFilled(false);
		}
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new LineBorder(isTransparent ? fg : bg, 2, true));
        button.addMouseListener(new MouseAdapter() {
            @Override
			public void mouseEntered(MouseEvent e) {
                if (!isTransparent) {
					button.setBackground(new Color(0, 70, 150));
				} else { button.setBackground(fg); button.setForeground(bg); }
            }
            @Override
			public void mouseExited(MouseEvent e) { button.setBackground(bg); button.setForeground(fg); }
        });
    }
}