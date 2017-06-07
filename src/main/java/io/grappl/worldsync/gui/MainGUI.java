package io.grappl.worldsync.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A GUI similar to the one that Grappl spawns when it first opens, but with some
 * relevant differences.
 *
 * There is only a login prompt.
 *  - A username field
 *  - A password field
 *  - A login button
 *  - A signup button, that opens a link to the website
 *  - A "Remember me" checkbox
 *
 *  And then that's literally it until the user logs in, after which point another GUI will
 *  be spawned, containing the primary options.
 *
 *  A list of relevant details will be downloaded in a json format file, containing
 *  account data such as the servers associated with the account.
 *
 */
public class MainGUI {

    public MainGUI() {
        JFrame mainFrame = new JFrame("WorldSync login");
        mainFrame.setSize(400, 300);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(null);

        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign up");

        int startX = 30;
        int startY = 30;

        usernameLabel.setBounds(startX, startY, 400, 20);
        usernameField.setBounds(startX, startY + 30, 300, 30);
        passwordLabel.setBounds(startX, startY + 70, 300, 30);
        passwordField.setBounds(startX, startY + 100, 300, 30);
        loginButton.setBounds(startX, startY + 150, 140, 40);
        signUpButton.setBounds(startX + 160, startY + 150, 140, 40);

        mainFrame.add(usernameLabel);
        mainFrame.add(passwordLabel);
        mainFrame.add(usernameField);
        mainFrame.add(passwordField);
        mainFrame.add(loginButton);
        mainFrame.add(signUpButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setVisible(false);
                /*
                    Rather than just believing the user,
                    there needs to some authentication sequence
                    here.
                 */
                new PostLoginGUI(usernameField.getText());
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Launch register page in browser
            }
        });

        mainFrame.setVisible(true);
    }
}
