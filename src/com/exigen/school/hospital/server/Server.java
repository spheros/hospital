package com.exigen.school.hospital.server;

import com.exigen.school.hospital.server.network.ClientHandler;
import com.exigen.school.hospital.server.storage.StorageDriver;
import com.exigen.school.hospital.server.storage.jdbc.JdbcStorageDriver;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 28.10.13
 */
public class Server implements Runnable, ServerConfig {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private final StorageDriver storageDriver;


    public Server(int port)
            throws IOException, SQLException, ClassNotFoundException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newCachedThreadPool();
        storageDriver = JdbcStorageDriver.getInstance();
        Logger.getLogger(this.getClass().getName()).setLevel(LOG_LEVEL);
    }

    public void run() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Server started listening" +
                " on port " + SERVER_PORT);
        try {
            for (;;) {
                Socket socket = serverSocket.accept();
                Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                        "Received connection from " + socket.getInetAddress() + ":" + socket.getPort());

                pool.execute(new ClientHandler(socket, storageDriver));

            }
        } catch (IOException ex) {
            pool.shutdown();
        }
    }

    public static void main(String[] args) throws
            SQLException, ClassNotFoundException {
        try {
            ( new Thread( new Server(SERVER_PORT)) ).start();
        } catch (IOException e) {
            if (e.getClass().equals(BindException.class)) {
                Logger.getLogger("Server").log(Level.INFO,
                        "Seems that you have another copy of Server running");
            } else
            e.printStackTrace();
        }
    }
}
