package com.exigen.school.hospital.client.network;

import com.exigen.school.hospital.server.network.ServerMessage;
import com.exigen.school.hospital.server.network.ServerMessageTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 04.11.13
 */
public class NetworkDriver implements NetworkConfig {
    private static Logger logger;
    private String socketInfo;
    private static NetworkDriver instance = null;
    private Socket socket;

    ObjectInputStream in = null;
    ObjectOutputStream out = null;

    public static NetworkDriver getInstance() throws IOException,
            ClassNotFoundException {
        instance = (instance != null) ? instance : new NetworkDriver();
        return instance;
    }

    private NetworkDriver() {
        if (logger == null) {
            logger = Logger.getLogger(this.getClass().getName());
            logger.setLevel(LOG_LEVEL);
            logger.log(Level.INFO, CREATING_DRIVER_INSTANCE);
        }
    }

    private void initConnection() throws IOException,
            ClassNotFoundException {

        ClientMessage msg = new ClientMessage();
        msg.setType(ClientMessageTypes.ESTABLISH_CONNECTION);

        try {
            if (socket == null || socket.isClosed()) {

                socket = new Socket(DEFAULT_SERVER_IP_ADDRESS, DEFAULT_SERVER_PORT);

                //for debug purposes
                createSocketInfo();
                logger.log(Level.INFO, CREATING_NEW_SOCKET + socketInfo);

                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());

            } else {
                logger.log(Level.INFO, USING_PREVIOUS_SOCKET + socketInfo);
            }

            out.writeObject(msg);

            ServerMessage srvMsg;
            if ((srvMsg = (ServerMessage) in.readObject()) != null) {
                if (srvMsg.getType().equals(ServerMessageTypes.ESTABLISH_CONNECTION_OK)) {

                }
            }
        } catch (IOException ioe) {
            String exMgs = ioe.getMessage();
            if (exMgs.equals(RESET_BY_PEER)) {
                logger.log(Level.INFO, exMgs + "\n" + RECONNECTION_TRIAL);
                resetInstance();
            }
            logger.log(Level.SEVERE, ioe.getMessage());
            throw ioe;
        }

    }

    public ServerMessage sendMessage(ClientMessage clMsg) throws IOException,
            ClassNotFoundException {
        initConnection();

        out.writeObject(clMsg);
        return (ServerMessage) in.readObject();
    }

    private void resetInstance() throws IOException, ClassNotFoundException {
        socket.close();
        initConnection();
    }

    public void close() throws IOException {
        logger.log(Level.INFO, CLOSING_DRIVER_INSTANCE);
        instance = null;
        socket.close();
    }

    private void createSocketInfo() {
        socketInfo = "(" + socket.getLocalAddress() + ":" +
                socket.getLocalPort() + " -> " + DEFAULT_SERVER_IP_ADDRESS + ":" + DEFAULT_SERVER_PORT + ")";
    }

}
