package io.grappl.worldsync;

import java.util.UUID;

public class ServerSync {

    // Class will use Grappl's authentication system, so need to rewrite all of that unless something comes up

    // Get list of ServerDatas from the core server after logging in
    public static void setUpServerLocally(String servername, UUID id) { // ServerData -> Server
        /*
            Create folder

            If new server
                Download the minecraft server jar
                Prompt the user to accept mojang's terms of serviec
                Start server

            If not new server
                Download the files to the local location
                Find the minecraft server jar somehow
                And start it

            Get port
            Open Grappl

         */
    }
    // Server object will send ServerUpdate messages to core to update the servers status on occasion
}
