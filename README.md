# WorldSync

The aim of this program is threefold:


    - To make an easy way for a person to create a personal server on one computer, and be able to start it on another.

    - To make it easy for people to create and host servers behind firewalls.

    - To create a backup program for minecraft servers.


This program is the client portion of the entire client-server application called WorldSync.
It will, if completed, allow the user to:


    - Create servers on one computer, the files of which are automatically uploaded to the cloud.

    - Once there, the user can either run their server on another machine, or pay to have it ran on the cloud.
      (No matter where it is executed, it has the same address due to Grappl)


Running the server on the cloud would effectively make this cloud a server host.
However, it would be possible to offer far cheaper rates, since users can offload the hosting to their private computers.

Currently, the clientside is more or less the only thing to really work in any capacity.
At current, it can download a minecraft server .jar from AWS, select that the user agrees to the EULA,
and start the server. The user can create and delete servers from the GUI. Servers will automatically
be able to be joined by external users since Grappl is already set up. However, none of the other
functionality currently works. It currently is useful as a tool to organize and manage servers.