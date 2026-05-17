package Laundry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
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
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // ── SIDEBAR PANEL — GridBagLayout for proper centering ──
        JPanel sidebarRegisterPanel = new JPanel(new java.awt.GridBagLayout());
        sidebarRegisterPanel.setBackground(SystemColor.window);
        sidebarRegisterPanel.setPreferredSize(new Dimension(500, 0));
        frame.getContentPane().add(sidebarRegisterPanel, BorderLayout.EAST);

        java.awt.GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        int FIELD_WIDTH = 315;

        // ── TITLE ──
        JLabel lblTitle = new JLabel("Register");
        lblTitle.setForeground(SystemColor.desktop);
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 28));
        lblTitle.setPreferredSize(new Dimension(FIELD_WIDTH, 44));
        gbc.gridy = 0; gbc.insets = new Insets(40, 0, 18, 0);
        sidebarRegisterPanel.add(lblTitle, gbc);

        // ── USERNAME LABEL ──
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        lblUser.setPreferredSize(new Dimension(FIELD_WIDTH, 18));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 4, 0);
        sidebarRegisterPanel.add(lblUser, gbc);

        // ── USERNAME FIELD ──
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(FIELD_WIDTH, 40));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 2, 0);
        sidebarRegisterPanel.add(txtUsername, gbc);

        // ── USERNAME ERROR ──
        lblUsernameError = new JLabel("Username must only contain letters and numbers (no spaces).");
        lblUsernameError.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblUsernameError.setForeground(new Color(200, 30, 30));
        lblUsernameError.setPreferredSize(new Dimension(FIELD_WIDTH, 16));
        lblUsernameError.setVisible(false);
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 6, 0);
        sidebarRegisterPanel.add(lblUsernameError, gbc);

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

        // ── PASSWORD LABEL ──
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        lblPass.setPreferredSize(new Dimension(FIELD_WIDTH, 18));
        gbc.gridy = 4; gbc.insets = new Insets(6, 0, 4, 0);
        sidebarRegisterPanel.add(lblPass, gbc);

        // ── PASSWORD FIELD ──
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(FIELD_WIDTH, 40));
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 4, 0);
        sidebarRegisterPanel.add(txtPassword, gbc);

        // ── STRENGTH BAR ──
        pwStrengthBar = new JProgressBar(0, 3);
        pwStrengthBar.setPreferredSize(new Dimension(FIELD_WIDTH, 8));
        pwStrengthBar.setValue(0);
        pwStrengthBar.setStringPainted(false);
        pwStrengthBar.setBorderPainted(false);
        pwStrengthBar.setBackground(new Color(220, 220, 220));
        pwStrengthBar.setForeground(new Color(220, 220, 220));
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 2, 0);
        sidebarRegisterPanel.add(pwStrengthBar, gbc);

        // ── STRENGTH TEXT ──
        lblStrengthText = new JLabel("");
        lblStrengthText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStrengthText.setForeground(Color.GRAY);
        lblStrengthText.setPreferredSize(new Dimension(FIELD_WIDTH, 16));
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 2, 0);
        sidebarRegisterPanel.add(lblStrengthText, gbc);

        // ── PASSWORD HINT ──
        lblPasswordHint = new JLabel("Min 8 chars · at least 1 letter · at least 1 number");
        lblPasswordHint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblPasswordHint.setForeground(new Color(150, 150, 150));
        lblPasswordHint.setPreferredSize(new Dimension(FIELD_WIDTH, 16));
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 6, 0);
        sidebarRegisterPanel.add(lblPasswordHint, gbc);

        // ── NEW: Live strength check on every keystroke ──
        txtPassword.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void insertUpdate(DocumentEvent e) { updateStrength(); }
            @Override
			public void removeUpdate(DocumentEvent e) { updateStrength(); }
            @Override
			public void changedUpdate(DocumentEvent e) { updateStrength(); }
        });

        // ── CONFIRM PASSWORD LABEL ──
        JLabel lblConfirm = new JLabel("Confirm Password");
        lblConfirm.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        lblConfirm.setPreferredSize(new Dimension(FIELD_WIDTH, 18));
        gbc.gridy = 9; gbc.insets = new Insets(6, 0, 4, 0);
        sidebarRegisterPanel.add(lblConfirm, gbc);

        // ── CONFIRM PASSWORD FIELD ──
        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConfirmPassword.setPreferredSize(new Dimension(FIELD_WIDTH, 40));
        gbc.gridy = 10; gbc.insets = new Insets(0, 0, 18, 0);
        sidebarRegisterPanel.add(txtConfirmPassword, gbc);

        // ── REGISTER BUTTON ──
        JButton btnRegister = new JButton("REGISTER");
        btnRegister.setPreferredSize(new Dimension(FIELD_WIDTH, 50));
        btnRegister.addActionListener(e -> handleRegistration());
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setBackground(SystemColor.textHighlight);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegister.setFocusPainted(false);
        btnRegister.setOpaque(true);
        gbc.gridy = 11; gbc.insets = new Insets(0, 0, 14, 0);
        sidebarRegisterPanel.add(btnRegister, gbc);

        // ── BACK TO LOGIN ──
        JLabel lblBackToLogin = new JLabel("Already have an account? Login here", SwingConstants.CENTER);
        lblBackToLogin.setPreferredSize(new Dimension(FIELD_WIDTH, 18));
        lblBackToLogin.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        lblBackToLogin.setForeground(SystemColor.textHighlight);
        lblBackToLogin.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        gbc.gridy = 12; gbc.insets = new Insets(0, 0, 40, 0);
        sidebarRegisterPanel.add(lblBackToLogin, gbc);
        lblBackToLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
                Login login = new Login();
                login.getFrame().setVisible(true);
                frame.dispose();
            }
        });

        // ── MAIN LOGO PANEL (unchanged) ──
        ImagePanel mainLogoPanel = new ImagePanel("/images/logo.png");
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
        if (pw.length() >= 8) {
			score++;
		}

        // Rule 2: contains at least one letter
        if (pw.matches(".*[a-zA-Z].*")) {
			score++;
		}

        // Rule 3: contains at least one digit
        if (pw.matches(".*[0-9].*")) {
			score++;
		}

        // Bonus: special character → pushes "Fair" to "Strong"
        // (score stays capped at 3 so the bar doesn't overflow)
        if (pw.matches(".*[^a-zA-Z0-9].*") && score == 3) {
			score = 3; // already max
		} else if (pw.matches(".*[^a-zA-Z0-9].*") && score == 2) {
			score = 3; // special char lifts Fair → Strong
		}

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
                lblStrengthText.setText("Strong");
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

        // Password must not contain spaces
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

        // ── BACKEND: Hash the password before inserting ──
        String hashedPassword = HashUtil.hashPassword(password); // <--- HASHING ADDED HERE
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword); // <--- PASS THE HASHED PASSWORD TO DB
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