package com.example.examplemod.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private SocketManager socketManager;
    public static String currentData = null;

    ClientHandler(Socket socket, SocketManager socketManager) throws IOException {
        this.socket = socket;
        this.socketManager = socketManager;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // 设置读取超时时间为30秒
        this.socket.setSoTimeout(30000);
    }


    public static void updateData(String data) {
        currentData = data;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String request = in.readLine();
                if ("GET_DATA".equals(request) && currentData != null) {
                    out.println(currentData);
                }
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Connection timed out. Closing connection.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            // 通知SocketManager移除当前ClientHandler
            if (socketManager != null) {
                socketManager.removeClientHandler(this);
            }
        }
    }
}
