package com.example.examplemod.logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketLogger {
    public static boolean isSocketConnecting = false;
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static PrintWriter out;

    public static void connect() {
        try {
            serverSocket = new ServerSocket(50000);
            socket = serverSocket.accept();
            out = new PrintWriter(socket.getOutputStream(), true);
            isSocketConnecting = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send(String message) {
        out.println(message);
    }

    public static void close() {
        try {
            isSocketConnecting = false;
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
