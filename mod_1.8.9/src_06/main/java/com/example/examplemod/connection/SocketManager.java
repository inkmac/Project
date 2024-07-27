package com.example.examplemod.connection;

import com.example.examplemod.logger.FileLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class SocketManager {
    private int maxConnections;
    private List<ClientHandler> clients;
    private ServerSocket serverSocket;
    private volatile boolean isRunning;

    public SocketManager(int maxConnections) {
        this.maxConnections = maxConnections;
        this.clients = new ArrayList<>();
    }

    public void startServer(int port) {
        if (isRunning) {
            FileLogger.log("Server is already running.");
            return;
        }

        isRunning = true;

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);

                while (isRunning) {
                    if (clients.size() < maxConnections) {
                        Socket socket = serverSocket.accept();
                        ClientHandler clientHandler = new ClientHandler(socket, this);
                        clients.add(clientHandler);
                        clientHandler.start(); // 启动处理客户端的线程
                    } else {
                        FileLogger.log("Maximum connections reached. Waiting for free slots...");
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void closeServer() {
        try {
            isRunning = false;
            for (ClientHandler client : clients) {
                client.interrupt();  // 结束线程
            }
            serverSocket.close();
            System.out.println("Server stopped.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void removeClientHandler(ClientHandler handler) {
        clients.remove(handler);
        FileLogger.log("Client disconnected. Remaining connections: " + clients.size());
    }
}



