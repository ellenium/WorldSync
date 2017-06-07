package io.grappl.worldsync.gui;
import io.grappl.worldsync.ServerData;
import io.grappl.worldsync.ServerSync;
import io.grappl.worldsync.Utility;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 *
 *  A GUI will be spawned that contains a list of the servers that are associated with this
 *  account.
 *
 *  There will be a number of server management options:
 *      - Create new server (CreateServerGUI)
 *      - Update server (UpdateServerGUI)
 *      - Delete server (Are you sure? prompt, followed by deletion, and delete server packet)
 *      - Open server folder (Opens the OS file explorer to the server data folder)
 *
 *  There will also be a button the opens an account management prompt. (UserUpdateGUI)
 *  A logout button (logs out the user and goes back to MainGUI).
 */

public class PostLoginGUI {

    /**
     * The GUI element that lets the user scroll through all the
     * servers that are associated with the logged in account.
     * (At present, it just shows all the servers that are in
     * in the local server directory on the disk.)
     */
    private JList<String> serverList;

    PostLoginGUI(final String username) { // TODO: Replace the "username" with some sort of auth object
        File serverFolder = new File(Utility.getAppdataFolder() + "/servers/");

        JFrame theGUI = new JFrame();

        theGUI.setTitle(ServerSync.APP_NAME + " - Logged in as " + username);
        theGUI.setLayout(null);
        theGUI.setSize(405, 290); // Formerly 270
        theGUI.setLocationRelativeTo(null);

        serverList = new JList<>(new DefaultListModel<>());
        JScrollPane scrollPane = new JScrollPane(serverList);
        scrollPane.setBounds(20, 20, 350, 100);
        theGUI.add(scrollPane);

        /*
            Assuming that every folder in the server directory is actually
            a server, then this should provide an accurate count of the number
            of servers present. Considering nothing else should be in here in
            normal function and this number doesn't affect anything critical
            to the program's operation, then this can be counted on as a
            sufficiently accurate count.
         */
        int numberOfServers = serverFolder.listFiles().length;

        System.out.println("Looking for servers on disk...");
        for(File server : serverFolder.listFiles()) {
            try {
                // If there are less than 20 servers, print out the names of all.
                if(numberOfServers < 20)
                    System.out.println("Found server: " + server);

                // Gets the name of the directory out of it's path
                String[] name = server.getAbsolutePath().split("\\\\");
                String actualName = name[name.length - 1];

                // The directory name is the server name, and is added as an option in the GUI
                ((DefaultListModel) serverList.getModel()).addElement(actualName);
            } catch (Exception e) {}
        }

        /*
            If there were more than 20 servers, we just need to tell the
            user that there were a lot. To do otherwise would result
            in console spam.
         */
        if(numberOfServers >= 20)
            System.out.println("Too many found to list.");

        System.out.println("Done searching for servers.");

        populateButtons(theGUI);

        theGUI.setVisible(true);
    }

    private String getSelectedServer() {
        return ((DefaultListModel) serverList.getModel()).getElementAt(serverList.getSelectedIndex()).toString();
    }

    private String getSelectedServerFolder() {
        return Utility.getAppdataFolder() + "servers\\" + getSelectedServer() + "\\";
    }

    private void populateButtons(JFrame window) {
        JButton createServerButton = new JButton("New Server");
        JButton updateServerButton = new JButton("Update Server");
        JButton deleteServerButton = new JButton("Delete Server");
        JButton openServerFolderButton = new JButton("Server folder");
        JButton userUpdateGUIButton = new JButton("Account");
        JButton logoutButton = new JButton("Logout");
        JButton startServer = new JButton("Start Server");
        JButton stopServer = new JButton("Stop Server");

        final int startX = 5;
        final int startY = 170; // Formerly 150
        final int buttonWidth = 120;
        final int buttonHeight = 30;
        final int buttonGap = 10;
        final int buttonDivide = buttonWidth + buttonGap;
        final int buttonDivideY = buttonHeight + buttonGap;

        startServer.setBounds(startX + buttonDivide/2, startY - (buttonDivideY), buttonWidth, buttonHeight);
        stopServer.setBounds(startX + buttonDivide + buttonDivide/2, startY - buttonDivideY, buttonWidth, buttonHeight);
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
        window.add(startServer);
        window.add(stopServer);

        createServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("What will be the name of this server?");
                ServerSync.setUpServerLocally(new ServerData(name, UUID.randomUUID()));
                ((DefaultListModel) serverList.getModel()).addElement(name);
            }
        });

        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerSync.setUpServerLocally(new ServerData(getActualNameOfSelectedServer(), UUID.randomUUID()));
            }
        });

        deleteServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int reply = JOptionPane.showConfirmDialog(null, "Actually delete server?");

                if(reply == JOptionPane.YES_OPTION) {
                    try {
                        FileUtils.deleteDirectory(new File(getSelectedServerFolder()));
                        ((DefaultListModel) serverList.getModel()).removeElement(getActualNameOfSelectedServer());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Server not deleted. Whew.");
                }
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
                // TODO: make this do something
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: make this do something
            }
        });
    }

    /**
     * Extract the actual name of a server from the path
     * of it's folder.
     * @return the actual name of the server
     */
    private String getActualNameOfSelectedServer() {
        String[] name = getSelectedServerFolder().split("\\\\");
        return name[name.length - 1];
    }
}
