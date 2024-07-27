package com.example.examplemod.logger;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketLogger {
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static String currentData = null;
    private static boolean isSocketConnecting = false;

    private static void connect(ICommandSender sender) {
        try {
            serverSocket = new ServerSocket(50000);
            sender.addChatMessage(new ChatComponentText("等待连接中..."));
            socket = serverSocket.accept();
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isSocketConnecting = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void waitForRequestAndSend() {
        if (!isSocketConnecting) {
            return;
        }

        try {
            String request = in.readLine(); // 阻塞直到接收到请求
            if ("GET_DATA".equals(request) && currentData != null) {
                // 发送当前数据
                // 因为是println(), 所以接收时候要多2Bytes来接收换行符!
                out.println(currentData);
                currentData = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(ICommandSender sender) {
        SocketLogger.connect(sender);
        sender.addChatMessage(new ChatComponentText("Mod 已启动！"));
        while (true) {
            SocketLogger.waitForRequestAndSend();
            if (!SocketLogger.isSocketConnecting) {
                break;
            }
        }
    }

    public static void updateData(String data) {
        currentData = data;
    }

    public static void close(ICommandSender sender) {
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
            sender.addChatMessage(new ChatComponentText("Mod 已停止！"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
