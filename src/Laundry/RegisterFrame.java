package Laundry;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Graphics;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// ── NEW: for live keystroke validation ──
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class RegisterFrame {

    private JFrame frame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;

    // ── NEW: strength widgets ──
    private JProgressBar pwStrengthBar;
    private JLabel lblStrengthText;
    private JLabel lblUsernameError;
    private JLabel lblPasswordHint;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                RegisterFrame window = new RegisterFrame();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public RegisterFrame() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Laundrify - Register");
        frame.setBounds(100, 100, 1200, 700);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // ── SIDEBAR PANEL (unchanged layout, same bounds) ──
        JPanel sidebarRegisterPanel = new JPanel();
        sidebarRegisterPanel.setBackground(SystemColor.window);
        sidebarRegisterPanel.setPreferredSize(new Dimension(500, 0));
        frame.getContentPane().add(sidebarRegisterPanel, BorderLayout.EAST);
        sidebarRegisterPanel.setLayout(null);

        JLabel lblTitle = new JLabel("Register");
        lblTitle.setForeground(SystemColor.desktop);
        lblTitle.setBounds(100, 60, 200, 38);
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 28));
        sidebarRegisterPanel.add(lblTitle);

        // ── USERNAME ──
        JLabel lblUser = new JLabel("Username");
        lblUser.setBounds(100, 109, 80, 16);
        lblUser.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        sidebarRegisterPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(100, 130, 315, 40);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sidebarRegisterPanel.add(txtUsername);

        // ── NEW: username error label (hidden by default) ──
        lblUsernameError = new JLabel("Username must only contain letters and numbers (no spaces).");
        lblUsernameError.setBounds(100, 172, 315, 14);
        lblUsernameError.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblUsernameError.setForeground(new Color(200, 30, 30));
        lblUsernameError.setVisible(false);
        sidebarRegisterPanel.add(lblUsernameError);

        // ── Block spaces, symbols, and anything not [a-zA-Z0-9_] from the username field ──
        // "+" instead of "*" means pasted/typed text must be non-empty valid chars;
        // empty string (backspace/delete) is handled separately so deletions still work.
        ((AbstractDocument) txtUsername.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text != null && text.matches("[a-zA-Z0-9_]+")) {
                    super.insertString(fb, offset, text, attr);
                    validateUsername();
                } else {
                    showUsernameError("No spaces or special characters allowed.");
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                    throws BadLocationException {
                // allow empty text (deletions / backspace)
                if (text != null && (text.isEmpty() || text.matches("[a-zA-Z0-9_]+"))) {
                    super.replace(fb, offset, length, text, attr);
                    validateUsername();
                } else {
                    showUsernameError("No spaces or special characters allowed.");
                }
            }
        });

        // ── PASSWORD ──
        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(100, 196, 80, 16);
        lblPass.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        sidebarRegisterPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(100, 216, 315, 40);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sidebarRegisterPanel.add(txtPassword);

        // ── NEW: Password strength progress bar ──
        pwStrengthBar = new JProgressBar(0, 3);
        pwStrengthBar.setBounds(100, 260, 315, 8);
        pwStrengthBar.setValue(0);
        pwStrengthBar.setStringPainted(false);
        pwStrengthBar.setBorderPainted(false);
        pwStrengthBar.setBackground(new Color(220, 220, 220));
        pwStrengthBar.setForeground(new Color(220, 220, 220)); // starts grey
        sidebarRegisterPanel.add(pwStrengthBar);

        // ── NEW: Strength text label (Weak / Fair / Strong) ──
        lblStrengthText = new JLabel("");
        lblStrengthText.setBounds(100, 272, 315, 16);
        lblStrengthText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStrengthText.setForeground(Color.GRAY);
        sidebarRegisterPanel.add(lblStrengthText);

        // ── NEW: Password rules hint ──
        lblPasswordHint = new JLabel("Min 8 chars · at least 1 letter · at least 1 number");
        lblPasswordHint.setBounds(100, 291, 315, 14);
        lblPasswordHint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblPasswordHint.setForeground(new Color(150, 150, 150));
        sidebarRegisterPanel.add(lblPasswordHint);

        // ── NEW: Live strength check on every keystroke ──
        txtPassword.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStrength(); }
            public void removeUpdate(DocumentEvent e) { updateStrength(); }
            public void changedUpdate(DocumentEvent e) { updateStrength(); }
        });

        // ── CONFIRM PASSWORD ──
        JLabel lblConfirm = new JLabel("Confirm Password");
        lblConfirm.setBounds(100, 314, 150, 16);
        lblConfirm.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        sidebarRegisterPanel.add(lblConfirm);

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setBounds(100, 334, 315, 40);
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sidebarRegisterPanel.add(txtConfirmPassword);

        // ── REGISTER BUTTON (same y-offset kept proportional) ──
        JButton btnRegister = new JButton("REGISTER");
        btnRegister.setBounds(100, 400, 315, 50);
        btnRegister.addActionListener(e -> handleRegistration());
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setBackground(SystemColor.textHighlight);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegister.setFocusPainted(false);
        sidebarRegisterPanel.add(btnRegister);

        JLabel lblBackToLogin = new JLabel("Already have an account? Login here");
        lblBackToLogin.setBounds(151, 468, 220, 16);
        lblBackToLogin.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        lblBackToLogin.setForeground(SystemColor.textHighlight);
        lblBackToLogin.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        sidebarRegisterPanel.add(lblBackToLogin);

        lblBackToLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Login login = new Login();
                login.getFrame().setVisible(true);
                frame.dispose();
            }
        });

        // ── MAIN LOGO PANEL (unchanged) ──
        ImagePanel mainLogoPanel = new ImagePanel("/resources/laundrify-logo.png");
        mainLogoPanel.setOpaque(false);
        frame.getContentPane().add(mainLogoPanel, BorderLayout.CENTER);
        mainLogoPanel.setLayout(new BorderLayout(0, 0));

        JLabel lblTag = new JLabel("Smart Laundry Management System");
        lblTag.setHorizontalAlignment(SwingConstants.CENTER);
        lblTag.setForeground(Color.WHITE);
        lblTag.setFont(new Font("Segoe UI Light", Font.ITALIC, 22));
        mainLogoPanel.add(lblTag, BorderLayout.NORTH);
    }

    // ────────────────────────────────────────────────
    //  NEW: Live username validation
    // ────────────────────────────────────────────────
    private void validateUsername() {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            lblUsernameError.setVisible(false);
            return;
        }
        // Must start with a letter, 3-20 chars, letters/numbers/underscore only
        if (!username.matches("^[a-zA-Z][a-zA-Z0-9_]{2,19}$")) {
            showUsernameError("Must start with a letter, 3–20 characters.");
        } else {
            lblUsernameError.setVisible(false);
        }
    }

    private void showUsernameError(String message) {
        lblUsernameError.setText(message);
        lblUsernameError.setVisible(true);
    }

    // ────────────────────────────────────────────────
    //  NEW: Live password strength meter
    // ────────────────────────────────────────────────
    private void updateStrength() {
        String pw = new String(txtPassword.getPassword());

        if (pw.isEmpty()) {
            pwStrengthBar.setValue(0);
            pwStrengthBar.setForeground(new Color(220, 220, 220));
            lblStrengthText.setText("");
            return;
        }

        int score = 0;

        // Rule 1: at least 8 characters
        if (pw.length() >= 8) score++;

        // Rule 2: contains at least one letter
        if (pw.matches(".*[a-zA-Z].*")) score++;

        // Rule 3: contains at least one digit
        if (pw.matches(".*[0-9].*")) score++;

        // Bonus: special character → pushes "Fair" to "Strong"
        // (score stays capped at 3 so the bar doesn't overflow)
        if (pw.matches(".*[^a-zA-Z0-9].*") && score == 3) score = 3; // already max
        else if (pw.matches(".*[^a-zA-Z0-9].*") && score == 2) score = 3; // special char lifts Fair → Strong

        pwStrengthBar.setValue(score);

        switch (score) {
            case 1:
                pwStrengthBar.setForeground(new Color(220, 53, 53));   // red
                lblStrengthText.setForeground(new Color(220, 53, 53));
                lblStrengthText.setText("Weak — add letters, numbers, or more characters");
                break;
            case 2:
                pwStrengthBar.setForeground(new Color(230, 140, 30));  // amber
                lblStrengthText.setForeground(new Color(200, 110, 0));
                lblStrengthText.setText("Fair — add a number or special character");
                break;
            case 3:
                pwStrengthBar.setForeground(new Color(40, 167, 69));   // green
                lblStrengthText.setForeground(new Color(30, 130, 50));
                lblStrengthText.setText("Strong \u2713");
                break;
            default:
                pwStrengthBar.setForeground(new Color(220, 220, 220));
                lblStrengthText.setText("");
        }
    }

    // ────────────────────────────────────────────────
    //  BACKEND UNCHANGED — only added front-end guards
    // ────────────────────────────────────────────────
    private void handleRegistration() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirm  = new String(txtConfirmPassword.getPassword()).trim();
        String role     = "Cashier";

        // ── NEW: front-end validation before hitting the DB ──

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!username.matches("^[a-zA-Z][a-zA-Z0-9_]{2,19}$")) {
            JOptionPane.showMessageDialog(frame,
                "Username must start with a letter and be 3–20 characters.\nNo spaces or special symbols.",
                "Invalid Username", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        // Password must not contain spaces (trim already removed leading/trailing;
        // this catches spaces embedded in the middle)
        if (password.contains(" ")) {
            JOptionPane.showMessageDialog(frame,
                "Password must not contain spaces.",
                "Invalid Password", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        // Password: min 8 chars, at least 1 letter, at least 1 digit
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(frame,
                "Password must be at least 8 characters long.",
                "Weak Password", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[a-zA-Z].*")) {
            JOptionPane.showMessageDialog(frame,
                "Password must contain at least one letter.",
                "Weak Password", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[0-9].*")) {
            JOptionPane.showMessageDialog(frame,
                "Password must contain at least one number.",
                "Weak Password", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(frame, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            txtConfirmPassword.requestFocus();
            return;
        }

        // ── BACKEND: unchanged — same SQL as original ──
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(frame,
                    "Registration Successful! Returning to Login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                Login login = new Login();
                login.getFrame().setVisible(true);
                frame.dispose();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame,
                "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── ImagePanel unchanged ──
    class ImagePanel extends JPanel {
        private Image img;

        public ImagePanel(String imgPath) {
            try {
                java.net.URL imgURL = getClass().getResource(imgPath);
                if (imgURL != null) {
                    this.img = new ImageIcon(imgURL).getImage();
                } else {
                    System.out.println("ERROR: Could not find image at " + imgPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(0, 0, 0, 40));
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g.setColor(new Color(30, 60, 90));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public JFrame getFrame() {
        return this.frame;
    }
}