package com.exigen.school.hospital.server.network;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 04.11.13
 */
public class ServerMessage implements Serializable {

    private ServerMessageTypes type;
    private DataBundle dBundle;
    private ErrorBundle eBundle;

    public ServerMessage(ServerMessageTypes type) {
        this(type, null, null);
    }

    public ServerMessage(ServerMessageTypes type, DataBundle dBundle) {
        this(type, dBundle, null);
    }

    public ServerMessage(ServerMessageTypes type, ErrorBundle eBundle) {
        this(type, null, eBundle);
    }

    public ServerMessage(ServerMessageTypes type, DataBundle dBundle,
                         ErrorBundle eBundle) {
        this.setType(type);
        this.setdBundle(dBundle);
        this.seteBundle(eBundle);
    }

    public ServerMessageTypes getType() {
        return type;
    }

    public void setType(ServerMessageTypes type) {
        this.type = type;
    }

    public DataBundle getDataBundle() {
        return dBundle;
    }

    public void setdBundle(DataBundle dBundle) {
        this.dBundle = dBundle;
    }

    public ErrorBundle getErrorBundle() {
        return eBundle;
    }

    public void seteBundle(ErrorBundle eBundle) {
        this.eBundle = eBundle;
    }

    public String toString() {
        return "Message type: " + getType() + ", DataBundle: " +
                getDataBundle() + ", ErrorBundle: " + getErrorBundle();
    }
}
