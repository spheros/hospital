package com.exigen.school.hospital.client.network;

import java.util.logging.Level;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 04.11.13
 */
public interface NetworkConfig {
    public static final Level LOG_LEVEL = Level.ALL;

    // todo in the future release
    public static final String PROPERTIES_FILE_NAME = "hospital.properties";


    public static final String DEFAULT_SERVER_IP_ADDRESS = "127.0.0.1";
    public static final int DEFAULT_SERVER_PORT = 1777;

    //Network error messages
    public static final String RESET_BY_PEER = "Connection reset by peer: socket write error";
    public static final String CONNECTION_REFUSED = "Connection refused: connect";
    public static final String SOCKET_CLOSED = "Socket closed";


    //Log and user messages
    public static final String RECONNECTION_TRIAL = "trying to reconnect...";
    public static final String CREATING_DRIVER_INSTANCE = "Network driver instance created";
    public static final String CLOSING_DRIVER_INSTANCE = "Closing network driver instance";
    public static final String CREATING_NEW_SOCKET = "new socket created ";
    public static final String USING_PREVIOUS_SOCKET = "using created socket ";

    public static final String CONNECTION_FAILED_MESSAGE = "Server connection failed\n";
    public static final String CONNECTION_CLOSED_MESSAGE = "Server closed the connection\n";
    public static final String COMMIT_DIALOG_MESSAGE = "Do you want to commit changes?\n";

    public static final String SERVER_RESPONSED_ERROR = "Server response error\n";

    public static final String GET_ALL_FIELDS_PARAM = "All";
    public static final String TABLE_NAME_PARAM = "tableName";


}
