package com.exigen.school.hospital.server.network;

import com.exigen.school.hospital.client.network.ClientMessage;
import com.exigen.school.hospital.client.network.ClientMessageTypes;
import com.exigen.school.hospital.server.ServerConfig;
import com.exigen.school.hospital.server.storage.StorageDriver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 04.11.13
 */
public class ClientHandler implements Runnable, ServerConfig {
    private Logger logger;
    private static int idCounter;
    private final int handlerId;
    private final Socket socket;
    private String socketInfo;
    private final InetAddress remoteAddr;
    private final int remotePort;
    private final StorageDriver storageDriver;

    public ClientHandler(Socket socket, StorageDriver storageDriver) {
        logger = Logger.getLogger(this.getClass().getName());
        logger.setLevel(LOG_LEVEL);
        handlerId = ++idCounter;
        this.socket = socket;
        remoteAddr = socket.getInetAddress();
        remotePort = socket.getPort();
        this.storageDriver = storageDriver;
    }

    public void run() {
        try {

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            ClientMessage clMsg;

            while ((clMsg = (ClientMessage) in.readObject()) != null) {
                if (clMsg.getType().equals(ClientMessageTypes.ESTABLISH_CONNECTION)) {
                    out.writeObject(new ServerMessage(ServerMessageTypes.ESTABLISH_CONNECTION_OK));
                } else {
                    logger.log(Level.INFO, "received query " +
                            "from socket " + socketInfo + clMsg.toString());
                    out.writeObject(storageDriver.handleMessage(clMsg));
                }
            }

        } catch (IOException e) {
            if (e.getMessage().equals("Connection reset")) {
                logger.log(Level.INFO, "Lost connection on socket " + socketInfo);
            } else {
                logger.log(Level.SEVERE, e.getMessage());
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void printMsg(String msg) {
        System.out.println(this + ": " + msg);
    }

    public String toString() {
        return "ClientThread(id=" + handlerId + ", " + socketInfo + ")";
    }

    private void createSocketInfo() {
        socketInfo = socket.getLocalAddress() + ":" +
                socket.getLocalPort() + " -> " + remoteAddr + ":" + remotePort;
    }
}
