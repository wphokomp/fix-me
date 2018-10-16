package com.wphokomp.fixme.router.Handler;

import com.wphokomp.fixme.router.Controller.RouterController;
import com.wphokomp.fixme.router.Model.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.Random;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Client> {

    @Override
    public void completed(AsynchronousSocketChannel asynchronousSocketChannel, Client client) {
        try {
            SocketAddress clientAddr = asynchronousSocketChannel.getLocalAddress();
            System.out.println("Connection accepted.");
            client.getAsynchronousServerSocketChannel().accept(client, this);
            RouterHandler routerHandler = new RouterHandler();
            Client newClient = new Client();
            newClient.setAsynchronousServerSocketChannel(client.getAsynchronousServerSocketChannel());
            newClient.setAsynchronousSocketChannel(asynchronousSocketChannel);
            newClient.setClientId(Integer.parseInt(generateClientID()));
            newClient.setByteBuffer(ByteBuffer.allocate(2048));
            newClient.setRead(false);
            newClient.setSocketAddress(clientAddr);
            Charset charset = Charset.defaultCharset();
            byte data[] = Integer.toString(newClient.getClientId()).getBytes(charset);
            newClient.setRouterHandler(routerHandler);
            newClient.getByteBuffer().put(data);
            newClient.getByteBuffer().flip();
            RouterController.addClient(newClient);
            asynchronousSocketChannel.write(newClient.getByteBuffer(), newClient, routerHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable e, Client client) {
        System.out.println("Failed to accept a connection.");
        e.printStackTrace();
    }

    public static String generateClientID() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        return String.format("%06d", number);
    }
}
