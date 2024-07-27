package com.example.examplemod.connection;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class DataServer {
    private static ServerSocket serverSocket = null;
    private static ICommandSender chatSender = null;
    private static String currentData = null;

    public static void startServer() throws IOException {

        // 创建一个 ServerSocket 对象，监听端口 50000
        serverSocket = new ServerSocket(50000);
        chatSender.addChatMessage(new ChatComponentText("Server started."));

        new Thread(() -> {
            while (true) {
                Socket clientSocket;
                BufferedReader in;
                PrintWriter out;
                try {
                    // 等待客户端连接
                    chatSender.addChatMessage(new ChatComponentText("Waiting for connection..."));
                    clientSocket = serverSocket.accept();
                    chatSender.addChatMessage(new ChatComponentText("Got a connection from " + clientSocket.getRemoteSocketAddress()));


                    // 设置 10 秒超时
                    clientSocket.setSoTimeout(10000);

                    // 发送消息
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("Connected");

                    // 接收客户端消息
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


                while (true) {
                    try {
                        // 读取数据
                        String data = in.readLine();

                        if (data == null) {
                            // 如果数据为空，表示客户端关闭了连接
                            chatSender.addChatMessage(new ChatComponentText("Client close connection"));
                            break;
                        }

                        if ("GET".equals(data)) {
                            // 因为是println() 所以接收时需要多出两个字节
                            out.println(currentData);
                        }


                    } catch (SocketTimeoutException e) {
                        chatSender.addChatMessage(new ChatComponentText("Data receive timeout"));
                        break;
                    } catch (IOException e) {
                        chatSender.addChatMessage(new ChatComponentText("An error occurred: " + e.getMessage()));
                        break;
                    }
                }

                // 确保连接被关闭
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                chatSender.addChatMessage(new ChatComponentText("Connection closed"));
            }
        }).start();
    }

    public static void closeServer() throws IOException {
        serverSocket.close();
    }

    public static void updateData(String data) {
        currentData = data;
    }

    public static void setChatSender(ICommandSender sender) {
        chatSender = sender;
    }
}
