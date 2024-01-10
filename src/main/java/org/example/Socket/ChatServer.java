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
    private Integer port;
    private List<Socket> clientList;
    // 初始化
    public void init(Integer port){
        this.port = port;
        clientList = new ArrayList<>();
    }
    // 启动
    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println(String.format("聊天服务启动，监听端口：%",port));
            // 监听客户端连接
            while (true){
                // 服务器主线程监听客户端连接
                Socket socket = serverSocket.accept();
                System.out.println(String.format("客户端连接成功"));
                // 维护客户端列表
                clientList.add(socket);
                // 新起线程接收，转发客户端消息 -- 防止其他接收，转发等操作阻塞服务器，导致客户端连接失败
            }
        }catch (Exception e){
            System.out.println(String.format("服务器监听端口失败 : %",e.getMessage()));
        }finally {
            clientList.clear();
        }
    }

    public void forwardMessage(){

    }

}
