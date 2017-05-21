package io.grappl.worldsync.gui;
import io.grappl.worldsync.ServerData;
import io.grappl.worldsync.ServerSync;
import io.grappl.worldsync.Utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PostLoginGUI {

    /**
     *
     *  A GUI will be spawned that contains a list of the servers that are associated with this
     *  account.
     *
     *  There will be a number of server managment options:
     *      - Create new server (CreateServerGUI)
     *      - Update server (UpdateServerGUI)
     *      - Delete server (Are you sure? prompt, followed by deletion, and delete server packet)
     *      - Open server folder (Opens the OS file explorer to the server data folder)
     *
     *  There will also be a button the opens an account management prompt. (UserUpdateGUI)
     *  A logout button (logs out the user and goes back to MainGUI).
     */

    private JList<String> serverList;

    public PostLoginGUI() {
        JFrame theGUI = new JFrame();

        theGUI.setLayout(null);
        theGUI.setSize(405, 270);
        theGUI.setLocationRelativeTo(null);

        serverList = new JList<String>(new DefaultListModel<String>());
        ((DefaultListModel) serverList.getModel()).addElement("testServer");
        JScrollPane scrollPane = new JScrollPane(serverList);
        scrollPane.setBounds(20, 20, 350, 100);
        theGUI.add(scrollPane);

        populateButtons(theGUI);
//        theGUI.pack();

        theGUI.setVisible(true);
    }

    public String getSelectedServer() {
        return ((DefaultListModel) serverList.getModel()).getElementAt(serverList.getSelectedIndex()).toString();
    }

    public String getSelectedServerFolder() {
        return Utility.getAppdataFolder() + "servers\\" + getSelectedServer() + "\\";
    }

    public void populateButtons(JFrame window) {
        JButton createServerButton = new JButton("Create Server");
        JButton updateServerButton = new JButton("Run Server");
        JButton deleteServerButton = new JButton("Delete Server");
        JButton openServerFolderButton = new JButton("Server folder");
        JButton userUpdateGUIButton = new JButton("Account");
        JButton logoutButton = new JButton("Logout");

        final int startX = 5;
        final int startY = 150;
        final int buttonWidth = 120;
        final int buttonHeight = 30;
        final int buttonGap = 10;
        final int buttonDivide = buttonWidth + buttonGap;
        final int buttonDivideY = buttonHeight + buttonGap;

        createServerButton.setBounds(startX, startY, buttonWidth, buttonHeight);
        updateServerButton.setBounds(startX + buttonDivide, startY, buttonWidth, buttonHeight);
        deleteServerButton.setBounds(startX + (buttonDivide * 2), startY, buttonWidth, buttonHeight);

        openServerFolderButton.setBounds(startX, startY + buttonDivideY, buttonWidth, buttonHeight);
        userUpdateGUIButton.setBounds(startX + buttonDivide, startY + buttonDivideY, buttonWidth, buttonHeight);
        logoutButton.setBounds(startX + (buttonDivide * 2), startY + buttonDivideY, buttonWidth, buttonHeight);

        window.add(createServerButton);
        window.add(updateServerButton);
        window.add(deleteServerButton);
        window.add(openServerFolderButton);
        window.add(userUpdateGUIButton);
        window.add(logoutButton);

        createServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("What is the name of this server?");
                ServerSync.setUpServerLocally(new ServerData(name, UUID.randomUUID()));
                ((DefaultListModel) serverList.getModel()).addElement(name);
            }
        });

        updateServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String location = getSelectedServerFolder();
                ServerSync.setUpServerLocally(new ServerData(getActualName(), UUID.randomUUID()));
            }
        });

        deleteServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(null, "Actually delete server?");
            }
        });

        openServerFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String location = getSelectedServerFolder();
                File theFile = new File(location);
                try {
                    Desktop.getDesktop().open(theFile);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        userUpdateGUIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void main(String[] args) {

        PostLoginGUI postLoginGUI = new PostLoginGUI();
        File file = new File(Utility.getAppdataFolder() + "/servers/");

        System.out.println("Looking for servers on disk...");
        for(File server : file.listFiles()) {
            try {
                if(file.listFiles().length < 20)
                    System.out.println("Found server: " + server);
                String[] name = server.getAbsolutePath().split("\\\\");
                String actualName = name[name.length - 1];
                ((DefaultListModel) postLoginGUI.serverList.getModel()).addElement(actualName);
            } catch (Exception e) {}
        }
        if(file.listFiles().length >= 20) System.out.println("Too many found to list.");
        System.out.println("Done.");


    }

    public String getActualName() {
        String[] name = getSelectedServerFolder().split("\\\\");
        String actualName = name[name.length - 1];
        return actualName;
    }
}
