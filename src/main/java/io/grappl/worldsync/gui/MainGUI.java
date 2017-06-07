package io.grappl.worldsync.gui;

import javafx.geometry.Pos;
import sun.applet.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI {

    /**
     * A GUI similar to the one that Grappl spawns when it first opens, but with some
     * relevant differences.
     *
     * There is only a login prompt.
     *  - A username field
     *  - A password field
     *  - A login button
     *  - A signup button, that opens a link to the website
     *
     *  And then that's literally it until the user logs in, after which point another GUI will
     *  be spawned, containing the primary options.
     *
     *  A list of relevant details will be downloaded in a json format file, containing
     *  account data such as the servers associated with the account.
     *
     */

    public static void main(String[] args) {
        new MainGUI();
    }

    public MainGUI() {
        JFrame mainFrame = new JFrame("WorldSync login");
        mainFrame.setSize(400, 300);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(null);

        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JPasswordField();

        int startX = 30;
        int startY = 30;

        usernameLabel.setBounds(startX, startY, 400, 20);
        usernameField.setBounds(startX, startY + 30, 300, 30);
        passwordLabel.setBounds(startX, startY + 70, 300, 30);
        passwordField.setBounds(startX, startY + 100, 300, 30);

        mainFrame.add(usernameLabel);
        mainFrame.add(passwordLabel);
        mainFrame.add(usernameField);
        mainFrame.add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign up");

        loginButton.setBounds(startX, startY + 150, 140, 40);
        signUpButton.setBounds(startX + 160, startY + 150, 140, 40);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setVisible(false);
                new PostLoginGUI(usernameField.getText());
            }
        });

        mainFrame.add(loginButton);
        mainFrame.add(signUpButton);

        mainFrame.setVisible(true);
    }
}
