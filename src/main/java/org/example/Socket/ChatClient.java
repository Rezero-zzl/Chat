package org.example.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 聊天客户端
 * @author zzl
 * @date 2024/1/10 16:51
 */
public class ChatClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    // 与服务器建立连接
    public void start(String serverAddress,int port){
        try {
            socket = new Socket(serverAddress,port);
            System.out.println(socket);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(),true);
            System.out.println(String.format("连接服务器成功，开始聊天吧"));

            // 异步接收消息，防止阻塞发送流程
            new Thread(() -> {
                try {
                    while (true){
                        String message = reader.readLine();
                        System.out.println(String.format("接收消息 : ",message));
                    }
                } catch (IOException e){
                    System.out.println(String.format("客户端获取消息失败 : %s",e.getMessage()));
                }
            }).start();
            // 发送消息
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String message  = consoleReader.readLine();
                writer.println(message);
            }
        } catch (IOException e){
            System.out.println(String.format("连接服务器初始化失败 : %s", e.getMessage()));
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start("localhost",9000);
    }
}
