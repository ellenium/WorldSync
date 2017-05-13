package io.grappl.worldsync;

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

}
