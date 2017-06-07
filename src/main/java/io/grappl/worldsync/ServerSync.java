package io.grappl.worldsync;

import io.grappl.client.api.*;
import io.grappl.client.api.Protocol;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.GrapplBuilder;
import io.grappl.client.impl.error.RelayServerNotFoundException;
import io.grappl.worldsync.gui.MainGUI;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The main class.
 * This is the class that should be called when the .jar
 * is run. It will instantly construct a MainGUI, which
 * contains the log-in prompt.
 */
public class ServerSync {

    public static final String APP_NAME = "WorldSync";

    public static void main(String[] args) {
        /*
            Create log
            Start MainGUI
         */

        // TODO: Create log
        new MainGUI();
    }

    // Class will use Grappl's authentication system, so need to rewrite all of that unless something comes up

    // Get list of ServerDatas from the core server after logging in
    public static void setUpServerLocally(ServerData serverData) { // ServerData -> Server
        /*
            Create folder

            If new server
                Download the minecraft server jar
                Prompt the user to accept mojang's terms of service

            If not new server
                Download the files to the local location
                Find the minecraft server jar somehow

            Start server
            Get port
            Open Grappl

         */

        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");

        // Create folder
        String dataFolder = Utility.getAppdataFolder() + "servers\\";
        String serverFolder = dataFolder + serverData.getServerName() + "\\";
        File serverFolderFile = new File(serverFolder);
        serverFolderFile.mkdirs(); // Create all folders necessary to store the server
        boolean newServer = serverData.getBootTimes() == 0;

        System.out.println("Directory of server: " + serverFolder);

        if(newServer) {
            boolean check = true;

            File eulaText = new File(serverFolder + "/eula.txt");
            try {
                FileInputStream fileInputStream = new FileInputStream(eulaText);
                String input = new DataInputStream(fileInputStream).readLine();
                String[] fork = input.split("\\=");
                if(fork[1].equalsIgnoreCase("TRUE")) {
                    check = false;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int reply = -1;

            if(check)
                reply = JOptionPane.showConfirmDialog(null, "To run the server, you must agree to Mojang's EULA. Do you?");

            if(!check || reply == JOptionPane.YES_OPTION) {
                File mcServerFile = new File(serverFolder + "/mc_server.jar");

                if(!mcServerFile.exists()) {
                    final String mcServerURL = "https://s3.amazonaws.com/Minecraft.Download/versions/1.11.2/minecraft_server.1.11.2.jar";
                    Utility.download(mcServerURL, serverFolder+"mc_server.jar");
                    System.out.println("DOWNLOADER: Minecraft Server jar not found on disk- downloaded from AWS, version 1.11.2");
                } else {
                    System.out.println("DOWNLOADER: Server .jar found to be already present on disk. Not redownloading.");
                }

//                File eulaText = new File(serverFolder + "/eula.txt");
                try {
                    PrintStream eulaPrintStream = new PrintStream(new FileOutputStream(eulaText));
                    eulaPrintStream.println("eula=TRUE");
                    eulaPrintStream.flush();
                    eulaPrintStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "You cannot run the server without accepting.");
            }
        }

        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd " + serverFolder +" && java -jar mc_server.jar");
        builder.redirectErrorStream(true);
        try {
            Process p = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final int serverPort = 25565;

        GrapplBuilder grapplBuilder = new GrapplBuilder(new ApplicationState(), Protocol.TCP);
        grapplBuilder.atLocalPort(serverPort);
        Grappl theGrappl = grapplBuilder.build();
        try {
            theGrappl.connect("n.grappl.io");
            System.out.println("GRAPPL: Connected to relay server, all ports opened, public at > " + theGrappl.getExternalServer().toString() + " < ");
            JOptionPane.showMessageDialog(null, "Grappl connection open on: " + theGrappl.getExternalServer());
        } catch (RelayServerNotFoundException e) {
            e.printStackTrace();
        }
        // Server object will send ServerUpdate messages to core to update the servers status on occasion
    }

    /**
     * Takes the contents of a server's directory, and puts all
     * of it into a .zip file insides of the /zips directory.
     * This .zip file can then be used as a backup, and is generally
     * sent to the core server where it can be sent to another computer
     * or be unpacked and executed there.
     * @param serverName the name of the server to be packed
     * @return a File object reprsenting the .zip file everything was packed into
     */
    public static File packServerIntoZip(final String serverName) {
        String serverFolder = Utility.getAppdataFolder() + "servers\\" + serverName + "\\";
        File theZip = new File(Utility.getAppdataFolder() + "zips\\" + serverName + ".zip");

        try {
            theZip.createNewFile();
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(theZip));
            recursivelyAddToZip(serverFolder, "", zipOutputStream);
            zipOutputStream.flush();
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return theZip;
    }

    /**
     * Private method used exclusively by the 'packServerIntoZip' method.
     * @param serverPath the pack of the server being packed
     * @param filePath the path of the file being packed
     * @param zipOutputStream the ZipOutputStream associated with the zip file
     */
    private static void recursivelyAddToZip(
            final String serverPath, final String filePath, final ZipOutputStream zipOutputStream) {
        File theFolder = new File(serverPath + filePath);

        if (theFolder.listFiles().length == 0) return;

        for (File f : theFolder.listFiles()) {
            ZipEntry zipEntry = new ZipEntry(filePath + "" + f.getName());
            try {
                zipOutputStream.putNextEntry(zipEntry);
                if (!f.isDirectory()) {
                    FileInputStream fileInputStream = new FileInputStream(f);
                    org.apache.commons.io.IOUtils.copy(fileInputStream, zipOutputStream);
                }
                zipOutputStream.flush();
                zipOutputStream.closeEntry();
                System.out.println("Put entry: " + (filePath + "" + f.getName()));

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (f.isDirectory()) {
                recursivelyAddToZip(serverPath, filePath + f.getName() + "/", zipOutputStream);
            }
        }
    }

    /**
     * Uploads a .zip file to the core server.
     * @param serverZip the .zip file to be uploaded
     * @param serverName the name of the server that the .zip file contains the files of
     */
    public static void ship(final File serverZip, final String serverName) {
        System.out.println("Uploading .zip");
        String coreIP = "localhost";
        int port = 33233;

        try {
            Socket socket = new Socket(coreIP, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeInt(1);
            PrintStream printStream = new PrintStream(dataOutputStream);
            printStream.println(serverName);
            dataOutputStream.flush();
            System.out.println("Name sent");
            FileInputStream fileInputStream = new FileInputStream(serverZip);
            org.apache.commons.io.IOUtils.copy(fileInputStream, socket.getOutputStream());
            socket.getOutputStream().flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("done");
    }

    /**
     * Downloads a server's .zip from the core server.
     * @param serverZip a .zip file containing the server's files
     * @param serverName the name of the server
     */
    public static void retrieve(File serverZip, String serverName) {
        System.out.println("Retrieving");
        String ip = "localhost";
        int port = 33233;

        try {
            Socket socket = new Socket(ip, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeInt(2);
            PrintStream printStream = new PrintStream(dataOutputStream);
            printStream.println(serverName);
            System.out.println("Name sent");
            FileOutputStream fileOutputStream = new FileOutputStream(serverZip);
            org.apache.commons.io.IOUtils.copy(socket.getInputStream(), fileOutputStream);
            fileOutputStream.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("done");
    }
}
