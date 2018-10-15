package com.wphokomp.fixme.router.Handler;

import com.wphokomp.fixme.router.Controller.RouterController;
import com.wphokomp.fixme.router.Model.Client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Client> {
    private static int clientId = 100000;
    @Override
    public void completed(AsynchronousSocketChannel client, Client attach) {
        try {
            SocketAddress clientAddr = client.getRemoteAddress();
            System.out.format("Accepted a  connection from  %s%n", clientAddr);
            attach.asynchronousServerSocketChannel.accept(attach, this);
            RouterHandler rwHandler = new RouterHandler();
            Client newAttach = new Client();
            newAttach.asynchronousServerSocketChannel = attach.asynchronousServerSocketChannel;
            newAttach.asynchronousSocketChannel = client;
            newAttach.clientId = clientId++;
            newAttach.byteBuffer = ByteBuffer.allocate(2048);
            newAttach.isRead = false;
            newAttach.socketAddress = clientAddr;
            Charset cs = Charset.forName("UTF-8");
            byte data[] = Integer.toString(newAttach.clientId).getBytes(cs);
            newAttach.routerHandler = rwHandler;
            newAttach.byteBuffer.put(data);
            newAttach.byteBuffer.flip();
            RouterController.addClient(newAttach);
            client.write(newAttach.byteBuffer, newAttach, rwHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable e, Client attach) {
        System.out.println("Failed to accept a  connection.");
        e.printStackTrace();
    }

}
