package org.example.Socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天服务器
 * @author zzl
 * @date 2024/1/10 16:51
 */
public class ChatServer {
    private List<ClientHandler> clientList = new ArrayList<>();
    // 启动
    public void start(Integer port){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println(String.format("聊天服务启动，监听端口 : %s",port));
            // 监听客户端连接
            while (true){
                // 服务器主线程监听客户端连接
                Socket socket = serverSocket.accept();
                System.out.println(socket);
                System.out.println(String.format("客户端连接成功"));
                // 创建聊天客户端处理工具
                ClientHandler clientHandler = new ClientHandler(this, socket);
                // 维护客户端列表
                clientList.add(clientHandler);
                // 新起线程异步接收，监听转发客户端消息 -- 防止其他接收，转发等操作阻塞服务器，导致客户端连接失败
                new Thread(clientHandler).start();
            }
        }catch (Exception e){
            System.out.println(String.format("服务器监听端口失败 : %s",e.getMessage()));
        }finally {
            clientList.clear();
        }
    }

    /**
     * 转发消息
     * @param message
     * @param sender
     */
    public void broadcastMessage(String message, ClientHandler sender){
        // 排除自己,进行转发
        for(ClientHandler client : clientList){
            if (client != sender){
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler client){
        clientList.remove(client);
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start(9000);
    }

}
