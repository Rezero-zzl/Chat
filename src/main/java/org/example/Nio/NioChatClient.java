package org.example.Nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 聊天客户端
 *
 * @author zzl
 * @date 2024/1/15 22:48
 */
public class NioChatClient {
    private final String host = "localhost";
    private final int port = 9001;
    private Selector selector;
    private SocketChannel socketChannel;

    public NioChatClient() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            System.out.println(String.format("聊天客户端初始化失败 : %s", e.getMessage()));
        }
    }

    public void sendMessage(String message) {
        try {
            socketChannel.write(ByteBuffer.wrap(message.getBytes()));
        } catch (IOException e) {
            System.out.println(String.format("发送消息失败 : %s", e.getMessage()));
        }
    }

    public void receiveMessage() {
        new Thread(()->{
            while (true){
                try {
                    int count = selector.select();
                    if (count > 0) {
                        Iterator<SelectionKey> selectionKeyIterator = selector.keys().iterator();
                        while (selectionKeyIterator.hasNext()) {
                            SelectionKey selectionKey = selectionKeyIterator.next();
                            if (selectionKey.isReadable()) {
                                SocketChannel readChannel = (SocketChannel) selectionKey.channel();
                                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                readChannel.read(byteBuffer);
                                String message = new String(byteBuffer.array());
                                System.out.println(String.format("接收 : %s",message));
                            }
                            selectionKeyIterator.remove();
                        }
                    }else {
                        System.out.println("没有就绪通道");
                    }
                } catch (IOException e) {
                    System.out.println(String.format("客户端接收消息失败 : %s",e.getMessage()));
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        NioChatClient nioChatClient = new NioChatClient();
        nioChatClient.receiveMessage();
        Scanner sc = new Scanner(System.in);
        String message = sc.nextLine();
        nioChatClient.sendMessage(message);
    }
}
