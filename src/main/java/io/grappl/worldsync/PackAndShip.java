package io.grappl.worldsync;

import java.io.*;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PackAndShip {

    public static void main(String[] args) {

        pack("Underground Lyfe");
        ship(new File(Utility.getAppdataFolder() + "/zips/Underground Lyfe.zip"), "Underground Lyfe");
    }

    public static void pack(String name) {
        String dataFolder = Utility.getAppdataFolder() + "servers\\";
        String serverFolder = dataFolder + name + "\\";

        File theZip = new File(Utility.getAppdataFolder() + "zips\\" + name + ".zip");

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
