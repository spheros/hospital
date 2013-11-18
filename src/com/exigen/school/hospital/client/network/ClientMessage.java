package com.exigen.school.hospital.client.network;

import java.io.Serializable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 04.11.13
 */
public class ClientMessage implements Serializable {

    private ClientMessageTypes type;
    private Map<String, String> params;

    public ClientMessageTypes getType() {
        return type;
    }

    public void setType(ClientMessageTypes type) {
        this.type = type;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String toString() {
        return "Message type: " + getType() + ", message params: " + getParams();
    }
}
