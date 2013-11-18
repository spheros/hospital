package com.exigen.school.hospital.server.network;

import com.exigen.school.hospital.client.network.ClientMessage;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 04.11.13
 */
public class ErrorBundle implements Serializable {
    private ClientMessage clientMessage;
    private String errorMsg;
    private int errorCode;

    public ClientMessage getClientMessage() {
        return clientMessage;
    }

    public void setClientMessage(ClientMessage clientMessage) {
        this.clientMessage = clientMessage;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
