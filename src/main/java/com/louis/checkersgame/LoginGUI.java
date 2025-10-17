package com.louis.checkersgame;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Handles user login and registration before starting the Checkers game.
 * Integrates with DatabaseManager and UserManager.
 */
public class LoginGUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginGUI() {
        setTitle("Checkers - Login");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Login to Play Checkers", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel);

        usernameField = new JTextField();
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        panel.add(usernameField);

        passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        panel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        panel.add(buttonPanel);

        add(panel);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = UserManager.loginUser(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome, " + user.getUsername() + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            dispose();

            // ✅ Launch the main Checkers game window with user info
            SwingUtilities.invokeLater(() -> new CheckersGUI(user));

        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = UserManager.registerUser(username, password);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Username might already exist.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        DatabaseManager.initialize();
        SwingUtilities.invokeLater(() -> {
            LoginGUI loginGUI = new LoginGUI();
            loginGUI.setVisible(true);
        });
    }
}
