package com.exigen.school.hospital.client.gui.exceptions;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 09.11.13
 */
public class ServerResponseException extends Exception {
    String msg;
    int errorCode;

    public ServerResponseException(String msg) {
        this(msg, -1);
    }

    public ServerResponseException(String msg, int errorCode) {
        this.msg = msg;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
