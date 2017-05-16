package io.grappl.worldsync;

import io.grappl.client.api.*;
import io.grappl.client.api.Protocol;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.GrapplBuilder;
import io.grappl.client.impl.error.RelayServerNotFoundException;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.UUID;

/**
 * The main class. This runs when the program starts.
 */
public class ServerSync {

    public static void main(String[] args) {
        /*
            Create log
            Start MainGUI
         */

        setUpServerLocally(new ServerData("testServer", UUID.randomUUID()));
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

        // Create folder
        String dataFolder = Utility.getAppdataFolder() + "servers/";
        String serverFolder = dataFolder + serverData.getServerName() + "/";
        File serverFolderFile = new File(serverFolder);
        serverFolderFile.mkdirs(); // Create all folders necessary to store the server
        boolean newServer = serverData.getBootTimes() == 0;

        if(newServer) {
            JOptionPane.showConfirmDialog(null, "To run the server, you must agree to Mojang's EULA. Do you?");

            final String mcServerURL = "https://s3.amazonaws.com/Minecraft.Download/versions/1.11.2/minecraft_server.1.11.2.jar";
            Utility.download(mcServerURL, serverFolder + "/mc_server.jar");
            System.out.println("Jar downloaded");

            File eulaText = new File(serverFolderFile + "/eula.txt");
            try {
                PrintStream eulaPrintStream = new PrintStream(new FileOutputStream(eulaText));
                eulaPrintStream.println("eula=TRUE");
                eulaPrintStream.flush();
                eulaPrintStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        else {

        }

        File mcServer = new File(serverFolder + "/mc_server.jar");
//         Load server
//        try {
//            Runtime.getRuntime().exec(serverFolder +"/mc_server.jar");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            File file = new File(serverFolder + "/mc_server.jar");
            ClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
            String mainClassLocation = "net.minecraft.server.MinecraftServer";
                Class theClass = classLoader.loadClass(mainClassLocation);

                try {
                    final Method staticMain = theClass.getMethod("main", String[].class);
                    System.out.println("on. point.");

                    System.out.println(staticMain.getName() + " " + staticMain.getGenericReturnType() + " " + staticMain.getParameterCount());
                    Thread serverThread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                String[] arguments = new String[]{"-nogui"};
                                staticMain.invoke(null, (Object) arguments);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    serverThread.start();

//                    Object the = theClass.newInstance();
//                    System.out.println("new instance");
//                    Method m = theClass.newInstance().getClass().getMethod("main", new Class[0]);
//                    System.out.println("method name: " + m.getName());
//                    m.invoke(the, new Object[0]);
                } catch (Exception var13) {
                    var13.printStackTrace();
                }
        }catch (Exception e) {}

        final int serverPort = 25565;

        GrapplBuilder grapplBuilder = new GrapplBuilder(new ApplicationState(), Protocol.TCP);
        grapplBuilder.atLocalPort(serverPort);
        Grappl theGrappl = grapplBuilder.build();
        try {
            theGrappl.connect("n.grappl.io");
            System.out.println("Connected to relay server, all ports opened, public at " + theGrappl.getExternalServer().toString());
        } catch (RelayServerNotFoundException e) {
            e.printStackTrace();
        }
    }
    // Server object will send ServerUpdate messages to core to update the servers status on occasion
}
