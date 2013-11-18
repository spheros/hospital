package com.exigen.school.hospital.client.network;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 04.11.13
 */
public enum ClientMessageTypes {

    ESTABLISH_CONNECTION,
    GET,
    GET_REG,
    UPDATE_CELL,
    UPDATE_ROW,
    ADD,
    DELETE,


    //this is for debugging purpose only
    SQL_QUERY,
}
