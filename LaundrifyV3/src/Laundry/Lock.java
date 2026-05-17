package Laundry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Lock extends JFrame {

    private JFrame parentFrame;
    private String lockedUsername;
    private String accountRole;
    private Color themeColor;

    private int failedAttempts = 0;
    private final int MAX_ATTEMPTS = 3;
    private Timer inactivityTimer;
    private Timer clockTimer;

    public Lock(JFrame parent, String lockedUsername, String role) {
        this.parentFrame = parent;
        this.lockedUsername = lockedUsername != null ? lockedUsername : "Unknown";
        this.accountRole = role != null ? role : "cashier";

        if (parentFrame != null) {
            parentFrame.setVisible(false);
        }

        if (this.accountRole.equalsIgnoreCase("admin")) {
            setTitle("Admin Locked");
            themeColor = new Color(238, 232, 220);
        } else {
            setTitle("Cashier Locked");
            themeColor = new Color(225, 240, 248);
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // --- TRUE FULLSCREEN KIOSK MODE ---
        setUndecorated(true);
        setExtendedState(Frame.MAXIMIZED_BOTH);

        // --- FULL WINDOW IMAGE BACKGROUND ---
        JPanel mainWrapper = new JPanel(new BorderLayout()) {
            private Image bgImage;
            {
                try {
                    java.net.URL imgURL = getClass().getResource("/images/logo.png");
                    if (imgURL != null) {
						bgImage = new ImageIcon(imgURL).getImage();
					}
                } catch (Exception ex) { }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(themeColor);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        setContentPane(mainWrapper);

        // ==========================================
        // TOP HEADER WITH LIVE CLOCK
        // ==========================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 51, 102, 220));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblHeaderTitle = new JLabel("LAUNDRIFY SECURE TERMINAL");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        headerPanel.add(lblHeaderTitle, BorderLayout.WEST);

        JLabel lblClock = new JLabel();
        lblClock.setFont(new Font("Consolas", Font.BOLD, 18));
        lblClock.setForeground(Color.WHITE);
        headerPanel.add(lblClock, BorderLayout.EAST);

        clockTimer = new Timer(1000, e -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy  |  hh:mm:ss a");
            lblClock.setText(sdf.format(new java.util.Date()));
        });
        clockTimer.start();
        mainWrapper.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // LOGIN FORM (Anchored at the Bottom)
        // ==========================================
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(0, 51, 102));
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblMessage = new JLabel("Locked: " + this.lockedUsername.toUpperCase(), SwingConstants.CENTER);
        lblMessage.setForeground(Color.WHITE);
        lblMessage.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(lblMessage, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(1, 1));
        formPanel.setOpaque(false);

        // BULLETPROOF PASSWORD FIELD
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);

        TitledBorder passBorder = BorderFactory.createTitledBorder(new LineBorder(new Color(170, 200, 220), 2, true), " Enter Password ");
        passBorder.setTitleColor(Color.WHITE);
        passBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(passBorder, new EmptyBorder(5, 5, 5, 5)));

        formPanel.add(passwordField);
        panel.add(formPanel, BorderLayout.CENTER);

        JButton btnUnlock = new JButton("UNLOCK");
        btnUnlock.setForeground(new Color(0, 51, 102));
        btnUnlock.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnUnlock.setBackground(Color.WHITE);
        btnUnlock.setFocusPainted(false);
        btnUnlock.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnUnlock.addActionListener(e -> {
            String pass = new String(passwordField.getPassword());

            if (pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your password.", "Empty Field", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean isAuthorized = false;

            // --- STRICT 1-TO-1 MATCHING ---
            // It MUST be the exact username who locked it, AND their exact password. No exceptions.
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

                pstmt.setString(1, this.lockedUsername);
                pstmt.setString(2, pass);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isAuthorized = true; // Perfect match found!
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isAuthorized) {
                if (inactivityTimer != null) {
					inactivityTimer.stop();
				}
                if (clockTimer != null) {
					clockTimer.stop();
				}
                this.dispose();
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
            } else {
                failedAttempts++;
                int attemptsLeft = MAX_ATTEMPTS - failedAttempts;

                if (failedAttempts >= MAX_ATTEMPTS) {
                    JOptionPane.showMessageDialog(this, "Maximum attempts reached. Shutting down system for security.", "Security Breach Detected", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(this, "Access Denied! Incorrect Password.\nAttempts remaining: " + attemptsLeft, "Security Alert", JOptionPane.WARNING_MESSAGE);
                    passwordField.setText("");
                }
            }
        });

        getRootPane().setDefaultButton(btnUnlock);
        panel.add(btnUnlock, BorderLayout.SOUTH);
        mainWrapper.add(panel, BorderLayout.SOUTH);

        inactivityTimer = new Timer(300000, e -> {
            JOptionPane.showMessageDialog(this, "System left unattended for too long. Shutting down for security.", "Timeout", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        });
        inactivityTimer.setRepeats(false);
        inactivityTimer.start();

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) { inactivityTimer.restart(); }
        });
    }
}