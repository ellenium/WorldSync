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
    }
    // Server object will send ServerUpdate messages to core to update the servers status on occasion

    public static void pack(String serverName) {
        String dataFolder = Utility.getAppdataFolder() + "servers\\";
        String serverFolder = dataFolder + serverName + "\\";

        File theZip = new File(Utility.getAppdataFolder() + "zips\\" + serverName + ".zip");

        try {
            theZip.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(theZip));
            recurse(serverFolder, "", zipOutputStream);
            zipOutputStream.flush();
            zipOutputStream.closeEntry();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void recurse(String worldName, String name, ZipOutputStream zipOutputStream) {
//        System.out.println("Checking: " + name);
        File theFolder = new File(worldName + name);

        if (theFolder.listFiles().length == 0) return;

        for (File f : theFolder.listFiles()) {
            ZipEntry zipEntry = new ZipEntry(name + "" + f.getName());
            try {
                zipOutputStream.putNextEntry(zipEntry);
                if (!f.isDirectory()) {
                    FileInputStream fileInputStream = new FileInputStream(f);
                    org.apache.commons.io.IOUtils.copy(fileInputStream, zipOutputStream);
                }
                zipOutputStream.flush();
                zipOutputStream.closeEntry();
                System.out.println("Put entry: " + (name + "" + f.getName()));

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (f.isDirectory()) {
                recurse(worldName, name + f.getName() + "/", zipOutputStream);
            }
        }
    }

    public static void ship(File zip, String name) {
        System.out.println("uploading");
        String ip = "localhost";
        int port = 33233;

        try {
            Socket socket = new Socket(ip, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeInt(1);
            PrintStream printStream = new PrintStream(dataOutputStream);
            printStream.println(name);
            dataOutputStream.flush();
            System.out.println("Name sent");
            FileInputStream fileInputStream = new FileInputStream(zip);
            org.apache.commons.io.IOUtils.copy(fileInputStream, socket.getOutputStream());
            socket.getOutputStream().flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("done");
    }

    public static void retrieve(File zip, String name) {
        System.out.println("Retrieving");
        String ip = "localhost";
        int port = 33233;

        try {
            Socket socket = new Socket(ip, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeInt(2);
            PrintStream printStream = new PrintStream(dataOutputStream);
            printStream.println(name);
            System.out.println("Name sent");
            FileOutputStream fileOutputStream = new FileOutputStream(zip);
            org.apache.commons.io.IOUtils.copy(socket.getInputStream(), fileOutputStream);
            fileOutputStream.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("done");
    }
}
