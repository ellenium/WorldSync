package io.grappl.worldsync;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Utility {

    /**
     * Returns the location of the user's application data folder.
     * It is located in AppData/Roaming in windows.
     *
     * If the folder does not already exist, calling this method creates it.
     */
    public static String getAppdataFolder() {
        String location = System.getenv("APPDATA") + "\\Grappl\\WorldSync\\";
        File locationFile = new File(location);
        locationFile.mkdirs();
        return location;
    }

    public static boolean download(String url, String localLocation) {
        try {
            URL location = new URL(url);
            ReadableByteChannel readableByteChannel = Channels.newChannel(location.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(localLocation));
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
