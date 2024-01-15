package org.example.Nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 聊天服务器
 * @author zzl
 * @date 2024/1/15 21:53
 */
public class NioChatServer {
    private final int port = 9001;
    private Selector selector;
    private ServerSocketChannel listenChannel;

    public NioChatServer(){
        try {
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            // 绑定端口
            listenChannel.socket().bind(new InetSocketAddress(port));
            //todo 设置非阻塞模式 作用：
            listenChannel.configureBlocking(false);
            // 将该 listenChanel 注册到 selector 对接收连接事件进行监听
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e){
            System.out.printf("服务初始化失败 : %s%n",e.getMessage());
        }
    }

    public void listen(){
        try {
            while (true){
                int count = selector.select();
                // 有就绪事件
                if (count > 0){
                    // 遍历所有注册事件,
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()){
                        SelectionKey selectionKey = iterator.next();
                        // 就绪事件为接收连接
                        if (selectionKey.isAcceptable()){
                            SocketChannel socketChannel = listenChannel.accept();
                            socketChannel.configureBlocking(false);
                            // 对客户端发送消息事件 进行监听
                            socketChannel.register(selector,SelectionKey.OP_READ);
                            System.out.printf("%s 上线%n",socketChannel.getRemoteAddress());
                        }
                        // 就绪事件为接收消息事件
                        if (selectionKey.isReadable()){
                            forwardMessage(selectionKey);
                        }
                        // todo 当前的 key 删除，防止重复处理  为啥
                        iterator.remove();
                    }
                }else {
                    System.out.println("等待....");
                }
            }
        } catch (IOException e) {
            System.out.println(String.format("服务器错误 : %s",e.getMessage()));
        } finally {
            System.out.println("收尾处理....");
        }
    }

    public void forwardMessage(SelectionKey selectionKey){
        // 从通道读取数据
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int count = 0;
        try {
            count = socketChannel.read(byteBuffer);
        } catch (IOException e) {
            System.out.println(String.format("服务器获取消息失败 : %s",e.getMessage()));
        }
        // todo 什么含义
        if (count > 0){
            String msg = new String(byteBuffer.array());
            System.out.println(String.format("接收到消息 : %s，进行转发",msg));
            for (SelectionKey key : selector.keys()) {
                Channel channel = key.channel();
                if (channel instanceof SocketChannel && channel != socketChannel){
                    SocketChannel forwardChannel = (SocketChannel) channel;
                    try {
                        forwardChannel.write(byteBuffer);
                    } catch (IOException e) {
                        System.out.println(String.format("转发数据失败 : %s",e.getMessage()));
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        NioChatServer nioChatServer = new NioChatServer();
        nioChatServer.listen();
    }
}
