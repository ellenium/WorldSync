package io.grappl.worldsync;

import java.io.File;

/**
 * Methods through which to communicate with the core server.
 *
 * Methods:
 *
 * Local -> Core
 *  - Authentication packet {username, password} (Grappl account)
 *  - User update packet
 *  - Create server packet
 *  - Server update packet
 *  - Delete server packet
 *  - Upload file packet {name of file, file binary data}
 *  - Server initialization start packet (Server initialization finished packet is first server update packet)
 *  - Transfer operation to remote packet
 *
 *  Core -> Local
 *  - Authentication successful packet
 *  - User data packet
 *  - Server data packet
 *  - Server creation successful packet
 *  - Download file packet
 *  - File download complete packet
 *  - Server initialization blocked packet
 *  - Premium services enabled packet
 */
public class Protocol {

    /*
        Sends a Grappl authentication packet to the Grappl core
        server to negotiation authentication.
     */
    public static void sendAuthenticationPacket() {}

    /*
        Sends a UserData packet in json form to the core server
        to update the user's account information.
     */
    public static void sendUserUpdatePacket() {}

    /*
        Tells the core server all necessary information about
        a server that has been started.
     */
    public static void sendCreateServerPacket() {}

    /*
        Contains the UUID of a server, which will then be deleted.
     */
    public static void sendDeleteServerPacket() {}

    /* Sends a ServerData object in json form to the core server.
       Also sends a list of files that will be sent to the server in the
       near future, which may or may not be a part of the ServerData object.
     */
    public static void sendServerUpdatePacket() {}

    /*
       Does the basics of sending a file to a remote server-
       Send the file name/ the location it needs to be stored at remotely
       The size of the file in bytes
       And then the binary data
     */
    public static void sendUploadFilePacket(File theFile) {}

    /*
        Just a packet with the UUID of the server that is being started,
        to tell the server that someone is trying to start the server
        and that it can't let anyone else start it
     */
    public static void sendServerInitializationStartPacket() {}

    /*
        Delegates the operation of the server to the core server.
     */
    public static void transferOperationToRemotePacket() {}
}
