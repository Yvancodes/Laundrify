package Laundry;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminPanel extends JFrame {

    private JPanel contentPane;
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JTextField totalSalesField;

    private JComboBox<String> dateFilterBox;
    private JComboBox<String> cashierFilterBox;

    // ── Palette (Primary Blue) ────────────────────────────────────────────────
    private final Color BG_MAIN      = new Color(235, 242, 252);  // light blue-grey canvas
    private final Color SURFACE      = new Color(255, 255, 255);  // white card
    private final Color PRIMARY_BLUE = new Color(0,   102, 204);  // main brand blue
    private final Color BLUE_DARK    = new Color(0,    70, 150);  // darker blue (hover/header)
    private final Color BLUE_LIGHT   = new Color(220, 235, 255);  // light blue tint
    private final Color BLUE_MID     = new Color(176, 207, 240);  // medium blue border
    private final Color BORDER_COLOR = new Color(200, 220, 245);  // subtle blue border
    private final Color TEXT_DARK    = new Color(15,   35,  65);  // near-black blue text
    private final Color TEXT_MUTED   = new Color(100, 130, 170);  // muted blue-grey
    private final Color ROW_ODD      = new Color(255, 255, 255);
    private final Color ROW_EVEN     = new Color(240, 247, 255);
    private final Color ROW_SELECT   = new Color(0,   102, 204);
    private final Color HEADER_BG    = new Color(0,    70, 150);

    public AdminPanel() {
        setTitle("Laundrify — Admin Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        contentPane = new JPanel(new BorderLayout(0, 0));
        contentPane.setBackground(BG_MAIN);
        setContentPane(contentPane);

        contentPane.add(buildTopBar(), BorderLayout.NORTH);
        contentPane.add(buildBody(),   BorderLayout.CENTER);

        loadSalesData();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  TOP BAR
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PRIMARY_BLUE);
        bar.setBorder(new EmptyBorder(14, 24, 14, 24));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBlock.setOpaque(false);

        JLabel lblBrand = new JLabel("LAUNDRIFY");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblRole = new JLabel("Admin Dashboard  ·  Sales & Operations");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(190, 215, 255));

        titleBlock.add(lblBrand);
        titleBlock.add(lblRole);

        JPanel iconRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        iconRow.setOpaque(false);

        JButton btnLock   = makeTopBarIconBtn("🔒", "Lock Screen");
        JButton btnLogout = makeTopBarIconBtn("🚪", "Logout");
        btnLock.addActionListener(e   -> new Lock(this, "Admin", "admin").setVisible(true));
        btnLogout.addActionListener(e -> logout());

        iconRow.add(btnLock);
        iconRow.add(btnLogout);

        bar.add(titleBlock, BorderLayout.WEST);
        bar.add(iconRow,    BorderLayout.EAST);
        return bar;
    }

    private JButton makeTopBarIconBtn(String icon, String tooltip) {
        JButton btn = new JButton(icon);
        btn.setToolTipText(tooltip);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(44, 44));
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1, true));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(BLUE_DARK); btn.setOpaque(true); }
            public void mouseExited(MouseEvent e)  { btn.setOpaque(false); }
        });
        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  BODY
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(18, 22, 18, 22));
        body.add(buildTableSection(), BorderLayout.CENTER);
        body.add(buildActionPanel(),  BorderLayout.EAST);
        return body;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  TABLE SECTION
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildTableSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        // Header row with filters
        JPanel headerRow = new JPanel(new BorderLayout(12, 0));
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(0, 0, 4, 0));

        JLabel lblSection = new JLabel("SUMMARY REPORT");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSection.setForeground(PRIMARY_BLUE);
        lblSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, PRIMARY_BLUE),
            new EmptyBorder(0, 8, 0, 0)
        ));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        filters.add(makeFilterLabel("Cashier:"));
        cashierFilterBox = makeComboBox();
        loadCashierDropdown();
        cashierFilterBox.addActionListener(e -> loadSalesData());
        filters.add(cashierFilterBox);

        filters.add(makeFilterLabel("Period:"));
        dateFilterBox = makeComboBox("All Time", "Today", "Last 7 Days", "Last 30 Days");
        dateFilterBox.setSelectedIndex(1);
        dateFilterBox.addActionListener(e -> loadSalesData());
        filters.add(dateFilterBox);

        headerRow.add(lblSection, BorderLayout.WEST);
        headerRow.add(filters,    BorderLayout.EAST);
        panel.add(headerRow, BorderLayout.NORTH);

        // Table model
        tableModel = new DefaultTableModel(
            new String[]{"Invoice", "Customer", "Items", "Total", "Paid", "Change", "Status", "Cashier", "Date"}, 0
        ) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        salesTable = new JTable(tableModel);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        salesTable.setRowHeight(34);
        salesTable.setShowVerticalLines(false);
        salesTable.setShowHorizontalLines(true);
        salesTable.setGridColor(BORDER_COLOR);
        salesTable.setBackground(ROW_ODD);
        salesTable.setForeground(TEXT_DARK);
        salesTable.setSelectionBackground(ROW_SELECT);
        salesTable.setSelectionForeground(Color.WHITE);
        salesTable.setFillsViewportHeight(true);
        salesTable.setIntercellSpacing(new Dimension(0, 0));

        // Alternating row + status colour renderer
        salesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (sel) {
                    setBackground(ROW_SELECT);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
                    setForeground(TEXT_DARK);
                    if (col == 6 && v != null) {
                        String s = v.toString().toLowerCase();
                        if (s.contains("paid") || s.contains("complet")) setForeground(new Color(0, 140, 60));
                        else if (s.contains("pending"))                  setForeground(new Color(180, 100, 0));
                    }
                }
                return this;
            }
        });

        // Header styling
        JTableHeader th = salesTable.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(HEADER_BG);
        th.setForeground(Color.WHITE);
        th.setPreferredSize(new Dimension(th.getWidth(), 38));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_BLUE));
        th.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(salesTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(ROW_ODD);
        panel.add(scroll, BorderLayout.CENTER);

        panel.add(buildTotalBar(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildTotalBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(12, 18, 12, 18)
        ));

        JLabel lbl = new JLabel("TOTAL REVENUE");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);

        totalSalesField = new JTextField("₱0.00");
        totalSalesField.setEditable(false);
        totalSalesField.setFont(new Font("Segoe UI", Font.BOLD, 26));
        totalSalesField.setForeground(PRIMARY_BLUE);
        totalSalesField.setBackground(SURFACE);
        totalSalesField.setBorder(null);
        totalSalesField.setHorizontalAlignment(JTextField.RIGHT);

        bar.add(lbl,             BorderLayout.WEST);
        bar.add(totalSalesField, BorderLayout.CENTER);
        return bar;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  ACTION PANEL
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildActionPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(210, 0));

        JLabel lbl = new JLabel("ACTIONS");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(PRIMARY_BLUE);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, PRIMARY_BLUE),
            new EmptyBorder(0, 8, 0, 0)
        ));
        wrapper.add(lbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(8, 1, 0, 10));
        grid.setOpaque(false);

        JButton btnSearch  = makeActionButton("Search");
        JButton btnRefresh = makeActionButton("Refresh");
        JButton btnPricing = makeActionButton("Pricing Setup");
        JButton btnAddons  = makeActionButton("Add-ons Setup");
        JButton btnUsers   = makeActionButton("User Management");
        JButton btnBackup  = makeActionButton("Backup Data");
        JButton btnPrint   = makeActionButton("Print Report");
        JButton btnHide    = makeActionButton("Hide Table");

        for (JButton b : new JButton[]{btnSearch, btnRefresh, btnPricing, btnAddons, btnUsers, btnBackup, btnPrint})
            styleActionButton(b, false);
        styleActionButton(btnHide, true);

        btnSearch.addActionListener(e  -> searchSales());
        btnRefresh.addActionListener(e -> loadSalesData());
        btnPricing.addActionListener(e -> new PricingSetup(this).setVisible(true));
        btnAddons.addActionListener(e  -> new AddonPricingSetup(this).setVisible(true));
        btnUsers.addActionListener(e   -> new UserManagementDialog(this).setVisible(true));
        btnBackup.addActionListener(e  -> backupAllSalesToPDF());
        btnPrint.addActionListener(e   -> printSalesReportByDate());
        btnHide.addActionListener(e    -> { tableModel.setRowCount(0); totalSalesField.setText("₱0.00"); });

        for (JButton b : new JButton[]{btnSearch, btnRefresh, btnPricing, btnAddons, btnUsers, btnBackup, btnPrint, btnHide})
            grid.add(b);

        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UI HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private JLabel makeFilterLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_DARK);
        return lbl;
    }

    private JComboBox<String> makeComboBox(String... items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        box.setBackground(BLUE_LIGHT);
        box.setForeground(TEXT_DARK);
        box.setPreferredSize(new Dimension(140, 30));
        box.setFocusable(false);
        return box;
    }

    private JButton makeActionButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleActionButton(JButton btn, boolean danger) {
        Color bg       = danger ? new Color(255, 238, 238) : SURFACE;
        Color fg       = danger ? new Color(180, 30,  30)  : TEXT_DARK;
        Color border   = danger ? new Color(255, 180, 180) : BORDER_COLOR;
        Color hoverBg  = danger ? new Color(255, 210, 210) : BLUE_LIGHT;
        Color hoverFg  = danger ? new Color(180, 30,  30)  : PRIMARY_BLUE;

        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); btn.setForeground(hoverFg); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg);      btn.setForeground(fg); }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  BACKEND — all original logic, unchanged
    // ─────────────────────────────────────────────────────────────────────────
    private void loadCashierDropdown() {
        java.awt.event.ActionListener[] listeners = cashierFilterBox.getActionListeners();
        for (java.awt.event.ActionListener l : listeners) cashierFilterBox.removeActionListener(l);
        cashierFilterBox.removeAllItems();
        cashierFilterBox.addItem("All Cashiers");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM users WHERE role = 'Cashier' ORDER BY username ASC")) {
            while (rs.next()) cashierFilterBox.addItem(rs.getString("username"));
        } catch (Exception e) { e.printStackTrace(); }
        for (java.awt.event.ActionListener l : listeners) cashierFilterBox.addActionListener(l);
    }

    private void loadSalesData() {
        if (tableModel == null || dateFilterBox == null || cashierFilterBox == null) return;
        tableModel.setRowCount(0);
        double totalRevenue = 0.00;

        String dateFilter    = dateFilterBox.getSelectedItem().toString();
        String cashierFilter = cashierFilterBox.getSelectedItem() != null
            ? cashierFilterBox.getSelectedItem().toString() : "All Cashiers";

        StringBuilder q = new StringBuilder(
            "SELECT s.invoice_number, COALESCE(c.name, 'Walk-in') AS customer_name, " +
            "GROUP_CONCAT(CONCAT(d.item_name, ' (₱', d.price, ')') SEPARATOR ', ') AS items_list, " +
            "s.total_amount, s.amount_paid, s.change_amount, s.status, COALESCE(u.username, 'Unknown') AS cashier_name, s.sale_date " +
            "FROM sales s " +
            "LEFT JOIN customers c ON s.customer_id = c.id " +
            "LEFT JOIN users u ON s.cashier_id = u.id " +
            "LEFT JOIN sales_details d ON d.sale_id = s.id WHERE 1=1 "
        );

        if (dateFilter.equals("Today"))             q.append("AND DATE(s.sale_date) = CURDATE() ");
        else if (dateFilter.equals("Last 7 Days"))  q.append("AND s.sale_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) ");
        else if (dateFilter.equals("Last 30 Days")) q.append("AND s.sale_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) ");

        if (!cashierFilter.equals("All Cashiers")) q.append("AND u.username = ? ");
        q.append("GROUP BY s.id ORDER BY s.sale_date DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(q.toString())) {
            if (!cashierFilter.equals("All Cashiers")) pstmt.setString(1, cashierFilter);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("invoice_number"),
                    rs.getString("customer_name"),
                    rs.getString("items_list") != null ? rs.getString("items_list") : "None",
                    String.format("₱%.2f", rs.getDouble("total_amount")),
                    String.format("₱%.2f", rs.getDouble("amount_paid")),
                    String.format("₱%.2f", rs.getDouble("change_amount")),
                    rs.getString("status"),
                    rs.getString("cashier_name"),
                    rs.getString("sale_date")
                });
                totalRevenue += rs.getDouble("total_amount");
            }
            totalSalesField.setText(String.format("₱%.2f", totalRevenue));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void searchSales() {
        String keyword = JOptionPane.showInputDialog(this, "Enter Invoice Number or Customer Name:", "Search Sales", JOptionPane.QUESTION_MESSAGE);
        if (keyword == null || keyword.trim().isEmpty()) { loadSalesData(); return; }

        tableModel.setRowCount(0);
        double totalRevenue = 0.00;
        String query =
            "SELECT s.invoice_number, COALESCE(c.name, 'Walk-in') AS customer_name, " +
            "GROUP_CONCAT(CONCAT(d.item_name, ' (₱', d.price, ')') SEPARATOR ', ') AS items_list, " +
            "s.total_amount, s.amount_paid, s.change_amount, s.status, COALESCE(u.username, 'Unknown') AS cashier_name, s.sale_date " +
            "FROM sales s LEFT JOIN customers c ON s.customer_id = c.id LEFT JOIN users u ON s.cashier_id = u.id " +
            "LEFT JOIN sales_details d ON d.sale_id = s.id " +
            "WHERE s.invoice_number LIKE ? OR c.name LIKE ? GROUP BY s.id ORDER BY s.sale_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + keyword.trim() + "%");
            pstmt.setString(2, "%" + keyword.trim() + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("invoice_number"), rs.getString("customer_name"),
                    rs.getString("items_list") != null ? rs.getString("items_list") : "None",
                    String.format("₱%.2f", rs.getDouble("total_amount")),
                    String.format("₱%.2f", rs.getDouble("amount_paid")),
                    String.format("₱%.2f", rs.getDouble("change_amount")),
                    rs.getString("status"),
                    rs.getString("cashier_name"),
                    rs.getString("sale_date")
                });
                totalRevenue += rs.getDouble("total_amount");
            }
            totalSalesField.setText(String.format("₱%.2f", totalRevenue));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void printSalesReportByDate() {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        JTextField startField = new JTextField(today);
        JTextField endField   = new JTextField(today);
        JComboBox<String> printCashierBox = new JComboBox<>();
        for (int i = 0; i < cashierFilterBox.getItemCount(); i++) printCashierBox.addItem(cashierFilterBox.getItemAt(i));

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Select Cashier:"));           panel.add(printCashierBox);
        panel.add(new JLabel("Start Date (YYYY-MM-DD):")); panel.add(startField);
        panel.add(new JLabel("End Date (YYYY-MM-DD):"));   panel.add(endField);

        if (JOptionPane.showConfirmDialog(this, panel, "Print Sales Report", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            String start           = startField.getText().trim();
            String end             = endField.getText().trim();
            String selectedCashier = printCashierBox.getSelectedItem().toString();
            String baseQuery =
                "SELECT s.invoice_number, COALESCE(c.name, 'Walk-in') AS customer_name, " +
                "s.total_amount, s.amount_paid, s.change_amount, s.status, COALESCE(u.username, 'Unknown') AS cashier_name, s.sale_date " +
                "FROM sales s LEFT JOIN customers c ON s.customer_id = c.id LEFT JOIN users u ON s.cashier_id = u.id ";

            if (selectedCashier.equals("All Cashiers")) {
                generatePDFReport(baseQuery + "WHERE DATE(s.sale_date) BETWEEN ? AND ? ORDER BY s.sale_date ASC",
                    new String[]{start, end},
                    "sales/Sales_Report_" + start + "_to_" + end + ".pdf",
                    "SALES REPORT: " + start + " TO " + end);
            } else {
                generatePDFReport(baseQuery + "WHERE u.username = ? AND DATE(s.sale_date) BETWEEN ? AND ? ORDER BY s.sale_date ASC",
                    new String[]{selectedCashier, start, end},
                    "sales/" + selectedCashier.replaceAll("\\s+", "") + "_Report_" + start + "_to_" + end + ".pdf",
                    "SALES REPORT (" + selectedCashier.toUpperCase() + "): " + start + " TO " + end);
            }
        }
    }

    private void backupAllSalesToPDF() {
        if (JOptionPane.showConfirmDialog(this, "Print a PDF of EVERY sale ever made?", "Backup Data", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String q =
                "SELECT s.invoice_number, COALESCE(c.name, 'Walk-in') AS customer_name, " +
                "s.total_amount, s.amount_paid, s.change_amount, s.status, COALESCE(u.username, 'Unknown') AS cashier_name, s.sale_date " +
                "FROM sales s LEFT JOIN customers c ON s.customer_id = c.id LEFT JOIN users u ON s.cashier_id = u.id ORDER BY s.sale_date ASC";
            generatePDFReport(q, new String[]{},
                "backup/Complete_Sales_Backup_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf",
                "COMPLETE DATABASE BACKUP");
        }
    }

    private void generatePDFReport(String query, String[] params, String fileName, String title) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) pstmt.setString(i + 1, params[i]);
            FileGeneration.generateReportFromRS(pstmt.executeQuery(), fileName, title);
            try {
                java.io.File pdfFile = new java.io.File(fileName);
                if (pdfFile.exists() && java.awt.Desktop.isDesktopSupported())
                    java.awt.Desktop.getDesktop().open(pdfFile);
            } catch (Exception ex) { /* silently ignore */ }
            JOptionPane.showMessageDialog(this, "Report generated!\nSaved as:\n" + fileName, "PDF Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.dispose();
            new Login().getFrame().setVisible(true);
        }
    }
}