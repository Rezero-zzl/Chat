package org.example.Socket;

import java.io.*;
import java.net.Socket;

/**
 * 聊天服务器
 * @author zzl
 * @date 2024/1/14 18:56
 */
public class ClientHandler implements Runnable{
    private ChatServer server;
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(ChatServer server,Socket clientSocket){
        this.server = server;
        this.clientSocket = clientSocket;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println(String.format("客户端获取输入、输出流失败 : %s",e.getMessage()));
        }
    }

    @Override
    public void run() {
        try {
            while(true){
                String message = reader.readLine();
                if (message == null){
                    break;
                }
                System.out.println(String.format("收到消息 : %s，进行转发",message));
                server.broadcastMessage(message,this);
            }
        }catch (IOException e){
            System.out.println(String.format("客户端接收消息失败 : %s",e.getMessage()));
        } finally {
            server.removeClient(this);
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println(String.format("客户端socket关闭失败 : %s",e.getMessage()));
            }
        }
    }

    /**
     * 往当前客户端发送消息
     * @param message
     */
    public void sendMessage(String message){
        writer.println(message);
    }
}
