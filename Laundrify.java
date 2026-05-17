package Laundry;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.Stack;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 
import java.util.UUID;

public class Laundrify extends JFrame {

    private JPanel contentPane;
    private JTextArea receiptArea;
    private JTextField totalField;

    private String loggedInCashier = "Unknown Cashier";
    private double currentTotal = 0.00;
    private Stack<Double> priceHistory = new Stack<>();
    private Stack<String> itemHistory = new Stack<>();
    
    // NORMALIZATION: Now tracks ID!
    private int customerID = -1; 
    private String customerName = "";
    private String customerPhone = "";

    private final Color MAIN_SYSTEM_BG = new Color(235, 242, 252);      // light blue-grey canvas
    private final Color CARD_HOLDER_BG = Color.WHITE;                   // white card surface
    private final Color BORDER_GRAY = new Color(200, 220, 245);         // soft blue border
    private final Color SLATE_DARK_TEXT = new Color(15, 35, 65);        // near-black blue text
    private final Color PRIMARY_VIBRANT_BLUE = new Color(0, 102, 204);  // primary brand blue
    private final Color CHECKOUT_GREEN = new Color(16, 185, 129);       // keep green for PAY NOW
    private final Color DANGER_BG = new Color(255, 238, 238);           // soft red bg
    private final Color DANGER_TEXT = new Color(180, 30, 30);           // red text

    public Laundrify(String cashierName) {
        this.loggedInCashier = cashierName;
        java.io.File resDir = new java.io.File("resource");
        if (resDir.exists()) {
                for (java.io.File f : resDir.listFiles()) {
                System.out.println("  " + f.getName());
            }
        } else {
            }
        setTitle("Laundrify - Cashier POS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setUndecorated(true); 
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setBounds(100, 100, 680, 550); 
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout(20, 20));
        contentPane.setBackground(MAIN_SYSTEM_BG);
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        // ── Top bar (matches AdminPanel style) ──────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_VIBRANT_BLUE);
        headerPanel.setBorder(new EmptyBorder(14, 24, 14, 24));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBlock.setOpaque(false);

        JLabel titleLabel = new JLabel("LAUNDRIFY");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Cashier POS  ·  " + loggedInCashier);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(190, 215, 255));

        titleBlock.add(titleLabel);
        titleBlock.add(subLabel);
        headerPanel.add(titleBlock, BorderLayout.WEST);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        JPanel mainBody = new JPanel(new GridLayout(1, 2, 25, 0));
        mainBody.setOpaque(false);
        contentPane.add(mainBody, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setOpaque(false);
        mainBody.add(leftPanel);

        receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Consolas", Font.PLAIN, 16)); 
        receiptArea.setForeground(SLATE_DARK_TEXT);
        receiptArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setBorder(new LineBorder(BORDER_GRAY, 2, true));
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel receiptHeaderPanel = new JPanel(new BorderLayout());
        receiptHeaderPanel.setBackground(new Color(0, 70, 150));
        receiptHeaderPanel.setBorder(new EmptyBorder(7, 10, 7, 10)); 

        JLabel lblCashier = new JLabel("Cashier: " + loggedInCashier);
        lblCashier.setFont(new Font("Consolas", Font.BOLD, 14)); 
        lblCashier.setForeground(Color.WHITE);
        receiptHeaderPanel.add(lblCashier, BorderLayout.WEST);

        JLabel lblClock = new JLabel();
        lblClock.setHorizontalAlignment(SwingConstants.RIGHT);
        lblClock.setFont(new Font("Consolas", Font.BOLD, 14));
        lblClock.setForeground(Color.WHITE);
        receiptHeaderPanel.add(lblClock, BorderLayout.EAST);

        scrollPane.setColumnHeaderView(receiptHeaderPanel);

        javax.swing.Timer timeTimer = new javax.swing.Timer(1000, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy  |  hh:mm:ss a");
                lblClock.setText(sdf.format(new java.util.Date()));
            }
        });
        timeTimer.start(); 

        JPanel leftBottomPanel = new JPanel(new BorderLayout(0, 10));
        leftBottomPanel.setOpaque(false);
        leftPanel.add(leftBottomPanel, BorderLayout.SOUTH);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(PRIMARY_VIBRANT_BLUE); 
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0, 70, 150), 2, true), 
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        
        JLabel totalLabel = new JLabel(" TOTAL DUE: ");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setForeground(Color.WHITE);
        
        totalField = new JTextField("0.00");
        totalField.setEditable(false);
        totalField.setBorder(null);
        totalField.setBackground(PRIMARY_VIBRANT_BLUE);
        totalField.setForeground(CHECKOUT_GREEN); 
        totalField.setFont(new Font("Segoe UI", Font.BOLD, 32));
        totalField.setHorizontalAlignment(JTextField.RIGHT);
        
        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(totalField, BorderLayout.CENTER);
        leftBottomPanel.add(totalPanel, BorderLayout.NORTH);

        JPanel clearRemovePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        clearRemovePanel.setOpaque(false);
        
        JButton btnClear = new JButton("CLEAR");
        styleButton(btnClear, DANGER_BG, DANGER_TEXT);
        btnClear.addActionListener(e -> clearReceipt());
        clearRemovePanel.add(btnClear);
        
        JButton btnRemove = new JButton("REMOVE");
        styleButton(btnRemove, DANGER_BG, DANGER_TEXT);
        btnRemove.addActionListener(e -> removeLastItem());
        clearRemovePanel.add(btnRemove);
        
        leftBottomPanel.add(clearRemovePanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setOpaque(false);
        mainBody.add(rightPanel);

        JPanel mainServicesHolder = new JPanel(new BorderLayout(0, 15));
        mainServicesHolder.setBackground(CARD_HOLDER_BG);
        mainServicesHolder.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_GRAY, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel mainServicesTitle = new JLabel("Standard Laundry Services");
        mainServicesTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainServicesTitle.setForeground(PRIMARY_VIBRANT_BLUE);
        mainServicesTitle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, PRIMARY_VIBRANT_BLUE),
            BorderFactory.createEmptyBorder(0, 8, 0, 0)
        ));
        mainServicesHolder.add(mainServicesTitle, BorderLayout.NORTH);

        JPanel servicesGrid = new JPanel(new GridLayout(3, 2, 12, 12));
        servicesGrid.setOpaque(false);
        
        addServiceButton(servicesGrid, "WASH", "Wash", "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAACCElEQVR4nO1a25HDIAw0N9dgKkhtV0FK5H5ChsFAkNiVnMB+ZcLD0iKhh30cGxsbKyOUf8QYo4cglgghvPT+VW1w+3v9jo87fL4lRBaQK3JaV1FMOt8KuQX8uElxEQwT0DvN2rh0vhe2BXgL4I1hAt5dWuW4dL4XlrcAVSL06XlAHgaXzwSXd4HlCejWArmpfDparr28BSxPgKocnsFQmDV0PRMCpKE1n88mg0oAIqdIe7CIoBHQbax0lGmtizFGBgkUAmpKjAqfzyv3YZAAjwKl0OEJzV61tehUHUpATXnEvkwSYASUNzfaVMs9USRACPCqIBHPnSaAZfYtoN0BGgUkys9Giku4gEaI+IR0DC1HAswCRk7vXYqbxkfiPcoKzKrBPKVtKZePWV2sagIkAkrzeQ0JWsIgFuDVOUI8l+4C2mrOyhWW7whtArwF8MYmgP0A7WXGboUl7GpQu1ByMlIr0Jy+1lLM7oCchJFiyCq5ghVD0gJG2zXOnyeX8owpC9Cc0mgxxJYjAdoQkbStZ4S+VFOU3bYugW7B7WoQIchxnN/ooC2h3BNFOjQMstyB2XmG5wE1ErRE1Nai3Y3ycrTWsBw139n8QAra6/Fe11ZjEayLlvqBBKKtxY4wJp/ISF9qWoZV84+kvHKGFroEeNX5ljiFwaudEBqlftU84FtJ+Fa9NjYm8A8bS2aslXF58AAAAABJRU5ErkJggg==", new Color(0, 80, 160));
        addServiceButton(servicesGrid, "DRY", "Dry", "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAACCklEQVR4nO1aW5ICIQyUrb2gJ/BsewKPmP1xLAp5BboDCP1lmYEJTUIew+12cHCwM1z4h4jICEUs4Zx7r/u3aYL73/u3PB8AlcZBZQH+wj/GLUSEbwE/IxWZAdUE5Ha/Rj4rjgWMVmA0qgkoHXIrHYI+treApkRo9TzAD4PbZ4Lbu8D2BGRrAd9UVkfKtbe3gO0JaCqHe1AVZg1dz4QAbWj1n2eTQSUAkVNcc7CIoBGQbaxkFpMaJyLCIIFCQGwRtcr7z4XzMEiAR4FQafdCy1yxsehUHUpAbPGIeZkkwAgIT260qYZzokiAEDCqgkS8t5uAktnLC7nxGjnaHaBnQMrsS+6glSPdq4uAEvtsufa5GGAWYLX7teNqQasGrXa/F80EIKq6XrlWnxggFqA9mVG7j3ADakNkBTegEFDK2HrlSNAsYBUSqC6wAgn0pujsJJh0hWcmwawavBZRyuw08qHVoDYGs0Nia05g+mHEMjOsBaUjpJG1yqfoCGl2hLn7PZZBbYqm/uuVIyNB9oZILbOsbjDyXal1UapBK0xVDYbxGZ2wlJqjraA2RVEkMF0Mngdo2+I5xMai3Y3ycdQ550LFa803RxbjrKF9Ho+RcKHFIlgHLfWCxKV0z1nAjjAmV2S0FZxlWDW/JDUqZ0ghS4DVx4mR+AiDs+0QGuH6onnAt5Lwres6OOjAP5zen4uY8ciuAAAAAElFTkSuQmCC", new Color(0, 80, 160));
        addServiceButton(servicesGrid, "FOLD", "Fold", "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAA5UlEQVR4nO3aQQ7CMAxE0QnighyR43CqsMqGTeyWMjH5b19pMnFVIyEBAAAAAABgL+3Mw733/q0gZ7XWDp3l0EMrHfxTtojbVUGqSBew8u1L+XxMgDuAGwW4A7jdsw+0x/N1RRCX7ScgvQit/hmUcstQagIqHF7K5dz+FQgXUOX2h2heJsAdwI0C3AHcwpvgv22Aw/YTEN6Yqn0GpdhGGJqAioeXYrm3fwWmBVS9/WGWnwlwB3CjAHcANwpwB3CjAHcANwpwB3CjAHcAt2kBR/97s4pZ/u0nIHW7lX4aV59cAAB+4A2PCD+mgy472AAAAABJRU5ErkJggg==", new Color(0, 102, 204));
        addServiceButton(servicesGrid, "WASH & DRY", "Wash & Dry", "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAABdElEQVR4nO2YwQ7DIAxDYdr//zK7DKnKMpoAwUX43RCoDm4gQEqEEEIIIYSQE8n/OkopJUw058fovlYH0fo+Svdn0IpApAZKV82Ak6AB6ADQ0AB0AGhoADoANDQAHQAaGoAOAM3xBrzRAURfgO44PgNoAFIcnf4pMQOwBrTeBlcxVAW0FF4xqZm6XQa01m7tswZUx1n2g5m6FZcBMgBNrI7pDWi1rnkPuAaRv2jjZN/oTh+t694Evak9i5xz1p7SZdurazKgN5096xulu805QP7du7aVWwNGN7PeLFilu0UGRP39lB5wHbZi2QCvbashWxggJ2PJCOu3t1gCV2Yvh1sDdihlI7pbZUDEZmgy4OmlbETXnQHWYGa/9mh/V2t7dc0GyFT7JyT7Ru8E0bquMihTsuX2zMtQpG7XOaC1NmffAqN1hw5CkZNdpbtVGYyABqADQEMD0AGgOd4AtZzMPsaqwkopQ+iqGRBd31tv+whdQg7mA37WaFTlcd+QAAAAAElFTkSuQmCC", new Color(0, 102, 204));
        addServiceButton(servicesGrid, "SELF-SERVE", "Self-Serve", "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAABRElEQVR4nO2ayRaDIAxFQ0///5ftpm6cEEjy0HPvtkimF4qIGQAAAACAhGVZFqX9j9L4GrwyCdIEzIAsAduqq1SAAhRGz6qtUAEKyDZYq3K2ClITcDe4zCTQAlmGWquapQIUkGGkt5oZKkAB0QZGqxitAhSgdkBNeAJKKUX5fI0UBfQGER28WWILtAaTEbzZQAJ6VufyZ3SMlz9mZsOGsip1xYhPTQo4yrL6VHfUp2+vkdlZfa623J1J7qBoBQ//Tltg1tfXXntn43dZGQ0kQwmePrrvA6KV4D3/LgEz/K1Fso0vZCcYpYKIeQ8T4KECb2c95juKK/RdwCsJkesKJ0JnP7xtMWzeCHnCqbDNu7O8TIB3G6i+DV7Fwdfh2oCIxTDzgkTNf47FVYYfdUkqak8QfU/wjt/yFnjicRsAAAAAwAv4Aadn79hXYPfVAAAAAElFTkSuQmCC", new Color(0, 80, 160));
        
        // WEIGHT button with image
        JButton btnWeight = makeImageButton("WEIGHT", "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAA9klEQVR4nO2XSxKEMAgFZWruf+XM1rLGEJSPFt1rA+TlJeC2AQAAAAAAAAAAAABAF0T7YIwxMgqJRERO9/nJLOSJIEB1AdUgQHUB1bQXQG2DUfxrr7N2FUV7B5QIcDZcVQxdOCA7oXbK2S5o74D2P0NfjyCrWMTMyrd8Be46wbo+K58qQMVw4olW//Lm9opaRPF4Q67mW1lnOt3jZmYJIh5PS75V0cz2fkNXsDjm1v2eiSEi4imWFu/qWxX6wHkL4BVrT9gk6H1Voq5e+1E4RICo04qIiwO8A0a3Se/4OMAzWNaQ5JkHB3gFyh6R3zCSAwAAADyaHx07h/nMfAPEAAAAAElFTkSuQmCC", new Color(0, 80, 160));
        btnWeight.addActionListener(e -> addWeightService());
        servicesGrid.add(btnWeight);
        
        mainServicesHolder.add(servicesGrid, BorderLayout.CENTER);
        rightPanel.add(mainServicesHolder, BorderLayout.CENTER); 

        JPanel operationsWrapper = new JPanel(new BorderLayout(0, 20)); 
        operationsWrapper.setOpaque(false);

        JPanel quickActionsCard = new JPanel(new BorderLayout(0, 15));
        quickActionsCard.setBackground(CARD_HOLDER_BG);
        quickActionsCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_GRAY, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel quickActionsTitle = new JLabel("Operations & Management");
        quickActionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        quickActionsTitle.setForeground(PRIMARY_VIBRANT_BLUE);
        quickActionsTitle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, PRIMARY_VIBRANT_BLUE),
            BorderFactory.createEmptyBorder(0, 8, 0, 0)
        ));
        quickActionsCard.add(quickActionsTitle, BorderLayout.NORTH);

        JPanel bottomOpsPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        bottomOpsPanel.setOpaque(false);
        
        JPanel serviceAndAddonsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        serviceAndAddonsPanel.setOpaque(false);

        JButton btnFullService = new JButton("FULL SERVICE");
        styleButton(btnFullService, PRIMARY_VIBRANT_BLUE, Color.WHITE);
        btnFullService.addActionListener(e -> addService("Full Service"));
        serviceAndAddonsPanel.add(btnFullService);

        JButton btnAddOns = new JButton("ADD ONS");
        styleButton(btnAddOns, PRIMARY_VIBRANT_BLUE, Color.WHITE);
        btnAddOns.addActionListener(e -> showAddOnsDialog());
        serviceAndAddonsPanel.add(btnAddOns);

        bottomOpsPanel.add(serviceAndAddonsPanel); 

        JPanel extraBtnsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        extraBtnsPanel.setOpaque(false);
        
        JButton btnCustInfo = new JButton(" CUSTOMER INFO");
        styleButton(btnCustInfo, new Color(0, 70, 150), Color.WHITE);
        btnCustInfo.addActionListener(e -> new CustomerDialog(this).setVisible(true));
        extraBtnsPanel.add(btnCustInfo);

        JButton btnQueue = new JButton(" QUEUE MANAGER");
        styleButton(btnQueue, new Color(0, 70, 150), Color.WHITE);
        btnQueue.addActionListener(e -> new QueueManager().setVisible(true));
        extraBtnsPanel.add(btnQueue);

        bottomOpsPanel.add(extraBtnsPanel);
        quickActionsCard.add(bottomOpsPanel, BorderLayout.CENTER);

        JPanel iconPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        iconPanel.setOpaque(false);
        
        JButton btnUser = new JButton("🚪");
        styleButton(btnUser, new Color(220, 235, 255), new Color(0, 70, 150));
        btnUser.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        btnUser.addActionListener(e -> logout()); 
        iconPanel.add(btnUser);
        
        JButton btnLock = new JButton("🔒");
        styleButton(btnLock, new Color(220, 235, 255), new Color(0, 70, 150));
        btnLock.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        btnLock.addActionListener(e -> new Lock(this, loggedInCashier, "cashier").setVisible(true)); 
        iconPanel.add(btnLock);

        JButton btnPay = new JButton("PAY NOW");
        styleButton(btnPay, CHECKOUT_GREEN, Color.WHITE); 
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        btnPay.addActionListener(e -> processPaymentFlow()); 

        JPanel cardBottomArea = new JPanel(new BorderLayout(10, 0));
        cardBottomArea.setOpaque(false);
        cardBottomArea.add(iconPanel, BorderLayout.WEST);
        cardBottomArea.add(btnPay, BorderLayout.CENTER);
        
        quickActionsCard.add(cardBottomArea, BorderLayout.SOUTH);

        operationsWrapper.add(quickActionsCard, BorderLayout.SOUTH);
        rightPanel.add(operationsWrapper, BorderLayout.SOUTH);
    }
    
    private void showAddOnsDialog() {
        JDialog dialog = new JDialog(this, "Select Laundry Add-Ons", true);
        dialog.setUndecorated(true); 
        dialog.setBounds(this.getBounds()); 
        dialog.setBackground(new Color(15, 23, 42, 170)); 

        JPanel overlayWrapper = new JPanel(new GridBagLayout());
        overlayWrapper.setOpaque(false); 

        JPanel modalCard = new JPanel(new BorderLayout(15, 15));
        modalCard.setBackground(MAIN_SYSTEM_BG); 
        modalCard.setPreferredSize(new Dimension(500, 600)); 
        modalCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_GRAY, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblHeader = new JLabel("Choose Add-On Services", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(PRIMARY_VIBRANT_BLUE);
        lblHeader.setBorder(new EmptyBorder(10, 15, 10, 15));
        modalCard.add(lblHeader, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(CARD_HOLDER_BG); 
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        java.util.List<Object[]> addonsData = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT addon_name, price FROM add_ons ORDER BY addon_name ASC")) {
            
            while (rs.next()) {
                addonsData.add(new Object[]{rs.getString("addon_name"), rs.getDouble("price")});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        java.util.List<AddonUIContext> addonItems = new java.util.ArrayList<>();

        for (Object[] data : addonsData) {
            String name = (String) data[0];
            double price = (Double) data[1];

            JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
            itemPanel.setBackground(CARD_HOLDER_BG); 
            itemPanel.setMaximumSize(new Dimension(420, 50));
            itemPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GRAY, 1, true),
                new EmptyBorder(8, 15, 8, 15)
            ));

            JCheckBox chkBox = new JCheckBox(String.format("%s (₱%.2f)", name, price));
            chkBox.setFont(new Font("Segoe UI", Font.BOLD, 15));
            chkBox.setBackground(CARD_HOLDER_BG);
            chkBox.setForeground(SLATE_DARK_TEXT);
            chkBox.setFocusPainted(false);
            chkBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

            SpinnerModel sm = new SpinnerNumberModel(1, 1, 10, 1);
            JSpinner spinner = new JSpinner(sm);
            spinner.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            spinner.setPreferredSize(new Dimension(70, 35));
            spinner.setEnabled(false); 

            chkBox.addActionListener(e -> spinner.setEnabled(chkBox.isSelected()));

            itemPanel.add(chkBox, BorderLayout.CENTER);
            itemPanel.add(spinner, BorderLayout.EAST);

            listPanel.add(itemPanel);
            listPanel.add(Box.createRigidArea(new Dimension(0, 10))); 

            addonItems.add(new AddonUIContext(name, price, chkBox, spinner));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(new LineBorder(BORDER_GRAY, 2, true));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        modalCard.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnCancel = new JButton("CANCEL");
        styleButton(btnCancel, DANGER_BG, DANGER_TEXT);
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnConfirm = new JButton("ADD TO ORDER");
        styleButton(btnConfirm, PRIMARY_VIBRANT_BLUE, Color.WHITE);
        btnConfirm.addActionListener(e -> {
            boolean hasSelection = false;
            for (AddonUIContext item : addonItems) {
                if (item.chkBox.isSelected()) {
                    int qty = (Integer) item.spinner.getValue();
                    double totalItemPrice = item.price * qty;
                    String entryName = String.format("%s (x%d)", item.name, qty);

                    itemHistory.push(entryName);
                    priceHistory.push(totalItemPrice);
                    
                    receiptArea.append(String.format("%s - ₱%.2f\n", entryName, totalItemPrice));
                    currentTotal += totalItemPrice;
                    hasSelection = true;
                }
            }
            if (hasSelection) {
                totalField.setText(String.format("%.2f", currentTotal));
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select at least one add-on.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        footerPanel.add(btnCancel);
        footerPanel.add(btnConfirm);
        modalCard.add(footerPanel, BorderLayout.SOUTH);

        overlayWrapper.add(modalCard);
        dialog.setContentPane(overlayWrapper);

        dialog.setOpacity(0.0f);
        Timer fadeInTimer = new Timer(15, e -> {
            float newOpacity = dialog.getOpacity() + 0.08f;
            if (newOpacity >= 1.0f) {
                dialog.setOpacity(1.0f);
                ((Timer)e.getSource()).stop();
            } else {
                dialog.setOpacity(newOpacity);
            }
        });
        fadeInTimer.start();
        dialog.setVisible(true);
    }

    private class AddonUIContext {
        String name;
        double price;
        JCheckBox chkBox;
        JSpinner spinner;

        AddonUIContext(String name, double price, JCheckBox chkBox, JSpinner spinner) {
            this.name = name;
            this.price = price;
            this.chkBox = chkBox;
            this.spinner = spinner;
        }
    }
    
    private Double showTouchpadDialog(String title, boolean isPayment) {
        JDialog dialog = new JDialog(this, title, true); 
        dialog.setSize(400, 550);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(new BorderLayout(15, 15));
        dialog.getContentPane().setBackground(MAIN_SYSTEM_BG);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(15, 15, 0, 15));
        
        JTextField displayField = new JTextField();
        displayField.setFont(new Font("Segoe UI", Font.BOLD, 36));
        displayField.setForeground(SLATE_DARK_TEXT);
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_GRAY, 2), new EmptyBorder(10, 10, 10, 10)));
        topPanel.add(displayField, BorderLayout.CENTER);
        dialog.getContentPane().add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        if (isPayment) {
            JPanel billsPanel = new JPanel(new GridLayout(2, 3, 8, 8));
            billsPanel.setOpaque(false);
            int[] bills = {20, 50, 100, 200, 500, 1000};
            
            for (int bill : bills) {
                JButton btnBill = new JButton("₱" + bill);
                btnBill.setFont(new Font("Segoe UI", Font.BOLD, 16));
                btnBill.setBackground(CHECKOUT_GREEN); 
                btnBill.setForeground(Color.WHITE);
                btnBill.setFocusPainted(false);
                btnBill.setBorder(new LineBorder(BORDER_GRAY, 1, true));
                btnBill.addActionListener(e -> displayField.setText(String.valueOf(bill)));
                billsPanel.add(btnBill);
            }
            centerPanel.add(billsPanel, BorderLayout.NORTH);
        }

        JPanel numpadPanel = new JPanel(new GridLayout(4, 3, 8, 8));
        numpadPanel.setOpaque(false);
        String[] keys = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "C", "0", "."};
        
        for (String key : keys) {
            JButton btnKey = new JButton(key);
            btnKey.setFont(new Font("Segoe UI", Font.BOLD, 24));
            
            if(key.equals("C")) {
                btnKey.setBackground(DANGER_BG);
                btnKey.setForeground(DANGER_TEXT);
            } else {
                btnKey.setBackground(new Color(220, 235, 255)); 
                btnKey.setForeground(PRIMARY_VIBRANT_BLUE);
            }
            
            btnKey.setFocusPainted(false);
            btnKey.setBorder(new LineBorder(BORDER_GRAY, 1, true));
            
            btnKey.addActionListener(e -> {
                if (key.equals("C")) {
                    displayField.setText(""); 
                } else {
                    displayField.setText(displayField.getText() + key); 
                }
            });
            numpadPanel.add(btnKey);
        }
        centerPanel.add(numpadPanel, BorderLayout.CENTER);
        dialog.getContentPane().add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 15, 15, 15));
        
        JButton btnConfirm = new JButton("CONFIRM ENTRY");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnConfirm.setBackground(PRIMARY_VIBRANT_BLUE);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setPreferredSize(new Dimension(0, 50));
        btnConfirm.setBorder(new LineBorder(PRIMARY_VIBRANT_BLUE.darker(), 1, true));
        
        final Double[] finalResult = {null}; 
        
        btnConfirm.addActionListener(e -> {
            try {
                if (!displayField.getText().isEmpty()) {
                    finalResult[0] = Double.parseDouble(displayField.getText());
                    dialog.dispose(); 
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid Number Format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        bottomPanel.add(btnConfirm, BorderLayout.SOUTH);
        dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true); 
        return finalResult[0]; 
    }


    /** Decodes a base64 PNG string into a scaled ImageIcon — no files or internet needed */
    private ImageIcon iconFromBase64(String b64, int size) {
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(b64);
            Image img = new ImageIcon(bytes).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) { return null; }
    }

    /**
     * Creates a service button with an icon image on top and label text below.
     * iconPath = path relative to working directory, e.g. "https://img.icons8.com/ios-filled/100/ffffff/washing-machine.png"
     */
    private JButton makeImageButton(String labelText, String iconPath, Color bgColor) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(0, 2));
        btn.setBackground(bgColor);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color border = bgColor.darker();
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(border, 2, true),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        // Icon label — loaded from embedded base64 data (no files or internet needed)
        JLabel iconLabel = new JLabel("", SwingConstants.CENTER);
        if (iconPath != null && !iconPath.isEmpty()) {
            ImageIcon ic = iconFromBase64(iconPath, 44);
            if (ic != null) iconLabel.setIcon(ic);
        }
        btn.add(iconLabel, BorderLayout.CENTER);

        // Text label (bottom)
        JLabel textLabel = new JLabel(labelText, SwingConstants.CENTER);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        textLabel.setForeground(Color.WHITE);
        btn.add(textLabel, BorderLayout.SOUTH);

        return btn;
    }

    private void addServiceButton(JPanel panel, String btnText, String itemText, String iconPath, Color bgColor) {
        JButton btn = makeImageButton(btnText, iconPath, bgColor);
        btn.addActionListener(e -> addService(itemText));
        panel.add(btn);
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color borderHighlight;
        if (bg.equals(PRIMARY_VIBRANT_BLUE)) borderHighlight = PRIMARY_VIBRANT_BLUE.darker();
        else if (bg.equals(CHECKOUT_GREEN))  borderHighlight = CHECKOUT_GREEN.darker();
        else if (bg.equals(CARD_HOLDER_BG) || bg.equals(MAIN_SYSTEM_BG)) borderHighlight = BORDER_GRAY;
        else borderHighlight = bg.darker();

        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderHighlight, 2, true),
            BorderFactory.createEmptyBorder(12, 10, 12, 10)
        ));
    }

    // NORMALIZATION METHOD UPDATE
    public void applyCustomerToOrder(int id, String name, String phone) {
        this.customerID = id;
        this.customerName = name;
        this.customerPhone = phone;
        
        receiptArea.append("==================================\n");
        receiptArea.append("  Customer: " + customerName + "\n");
        receiptArea.append("  Contact: " + customerPhone + "\n");
        receiptArea.append("==================================\n");
    }
    
    private void addWeightService() {
        Double weight = showTouchpadDialog("Enter Laundry Weight (kg)", false);
        
        if (weight != null) {
            if (weight <= 0) {
                JOptionPane.showMessageDialog(this, "Weight must be greater than zero.", "Invalid Weight", JOptionPane.WARNING_MESSAGE);
                return; 
            }
            double pricePerKg = 0.00;
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT price FROM prices WHERE service_name = 'Weight (per kg)'")) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) pricePerKg = rs.getDouble("price");
                else { JOptionPane.showMessageDialog(this, "Price for 'Weight' not set."); return; }
            } catch (Exception ex) { return; }

            double totalCost = weight * pricePerKg;
            String entryName = String.format("Weight (%.1f kg)", weight);
            
            itemHistory.push(entryName);   
            priceHistory.push(totalCost);  
            receiptArea.append(String.format("  %-16s - ₱%.2f\n", entryName, totalCost)); 
            currentTotal += totalCost;
            totalField.setText(String.format("%.2f", currentTotal));
        }
    }

    private void addService(String name) {
        double currentPrice = 0.00;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT price FROM prices WHERE service_name = ?")) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) currentPrice = rs.getDouble("price");
            else { JOptionPane.showMessageDialog(this, "Price not set for: " + name); return; }
        } catch (Exception e) { return; }

        itemHistory.push(name);          
        priceHistory.push(currentPrice); 
        receiptArea.append(String.format("  %-16s - ₱%.2f\n", name, currentPrice)); 
        currentTotal += currentPrice;
        totalField.setText(String.format("%.2f", currentTotal));
    }

    private void clearReceipt() {
        receiptArea.setText("");
        currentTotal = 0.00;
        priceHistory.clear();
        itemHistory.clear();
        customerID = -1; // Reset tracker
        customerName = "";
        customerPhone = "";
        totalField.setText("0.00");
    }

    private void removeLastItem() {
        if (!priceHistory.isEmpty()) {
            currentTotal -= priceHistory.pop();
            itemHistory.pop(); 
            if (currentTotal < 0) currentTotal = 0; 
            totalField.setText(String.format("%.2f", currentTotal));
            String text = receiptArea.getText();
            int lastNewLineIndex = text.lastIndexOf('\n', text.length() - 2);
            receiptArea.setText(lastNewLineIndex == -1 ? "" : text.substring(0, lastNewLineIndex + 1));
        }
    }

    private void processPaymentFlow() {
        if (currentTotal <= 0) { 
            JOptionPane.showMessageDialog(this, "No items selected.", "Wait!", JOptionPane.WARNING_MESSAGE); 
            return; 
        }

        boolean hasWeight = false;
        for (String item : itemHistory) {
            if (item.contains("Weight")) { hasWeight = true; break; }
        }

        if (!hasWeight) {
            int confirm = JOptionPane.showConfirmDialog(this, "WAIT! No Laundry Weight was entered for this order.\n\nAre you sure you want to proceed to payment without a weight?", "Missing Weight Check", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) { return; }
        }

        Double amountPaid = showTouchpadDialog(String.format("Total Due: ₱%.2f", currentTotal), true);
        
        if (amountPaid != null) {
            if (amountPaid < currentTotal) { 
                JOptionPane.showMessageDialog(this, "Insufficient payment.", "Error", JOptionPane.ERROR_MESSAGE); 
                return; 
            }
            double change = amountPaid - currentTotal;
            saveTransactionAndPrint(amountPaid, change);
        }
    }

    // THE 3NF NORMALIZED DATABASE SAVE FUNCTION
    private void saveTransactionAndPrint(double amountPaid, double change) {
        String invoiceNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 
            
            Integer cashierId = null;
            try (PreparedStatement pstCashier = conn.prepareStatement("SELECT id FROM users WHERE username = ?")) {
                pstCashier.setString(1, loggedInCashier);
                ResultSet rsC = pstCashier.executeQuery();
                if (rsC.next()) cashierId = rsC.getInt("id");
            }

            String insertSaleSQL = "INSERT INTO sales (invoice_number, customer_id, cashier_id, total_amount, amount_paid, change_amount) VALUES (?, ?, ?, ?, ?, ?)";
            int generatedSaleId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(insertSaleSQL, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, invoiceNumber);
                if (customerID > 0) pstmt.setInt(2, customerID); else pstmt.setNull(2, java.sql.Types.INTEGER);
                if (cashierId != null) pstmt.setInt(3, cashierId); else pstmt.setNull(3, java.sql.Types.INTEGER);
                pstmt.setDouble(4, currentTotal);
                pstmt.setDouble(5, amountPaid);
                pstmt.setDouble(6, change);
                pstmt.executeUpdate();
                
                ResultSet gKeys = pstmt.getGeneratedKeys();
                if (gKeys.next()) generatedSaleId = gKeys.getInt(1);
            }

            String insertDetailsSQL = "INSERT INTO sales_details (sale_id, item_name, price) VALUES (?, ?, ?)";
            try (PreparedStatement pstDetails = conn.prepareStatement(insertDetailsSQL)) {
                for (int i = 0; i < itemHistory.size(); i++) {
                    pstDetails.setInt(1, generatedSaleId);
                    pstDetails.setString(2, itemHistory.get(i));
                    pstDetails.setDouble(3, priceHistory.get(i));
                    pstDetails.addBatch();
                }
                pstDetails.executeBatch();
            }

            conn.commit(); 
            
            FileGeneration.generateReceipt(invoiceNumber, customerName, customerPhone, itemHistory, priceHistory, currentTotal, amountPaid, change, loggedInCashier);        
            try {
                java.io.File pdfFile = new java.io.File("receipt/" + invoiceNumber + "_Receipt.pdf");
                if (pdfFile.exists() && java.awt.Desktop.isDesktopSupported()) { java.awt.Desktop.getDesktop().open(pdfFile); }
            } catch (Exception ex) {}
            
            JOptionPane.showMessageDialog(this, "Transaction Complete!\n\nInvoice No: " + invoiceNumber + "\nReceipt saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearReceipt(); 
            
        } catch (Exception e) { 
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error saving transaction.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); 
            Login loginScreen = new Login();
            loginScreen.getFrame().setVisible(true);
        }
    }
}