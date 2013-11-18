package com.exigen.school.hospital.server.storage;

import com.exigen.school.hospital.client.network.ClientMessage;
import com.exigen.school.hospital.server.network.ServerMessage;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 04.11.13
 */
public interface StorageDriver {
    ServerMessage handleMessage(ClientMessage cMessage);
}
