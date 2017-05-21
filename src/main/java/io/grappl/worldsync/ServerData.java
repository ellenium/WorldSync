package io.grappl.worldsync;

import java.util.UUID;

public class ServerData {

    private String serverName = "";
    private UUID uuid = null;
    private int bootTimes = 0;
    private String jarName = "mc_server.jar";

    public ServerData(String serverName, UUID uuid) {
        this.serverName = serverName;
        this.uuid = uuid;
    }

    public String getServerName() {
        return serverName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getBootTimes() {
        return bootTimes;
    }
}
