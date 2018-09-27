package com.wphokomp.fixme.router.Handlers;

import com.wphokomp.fixme.router.Controller.Route;
import com.wphokomp.fixme.router.Models.Client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class Connection implements CompletionHandler<AsynchronousSocketChannel, Client> {

    private static int clientId = 100000; //Replace this with the method
    @Override
    public void completed(AsynchronousSocketChannel asynchronousSocketChannel, Client client) {
        try {
            SocketAddress socketAddress = asynchronousSocketChannel.getRemoteAddress();

            System.out.format("Connection established on %s%n", socketAddress);
            client.getAsynchronousServerSocketChannel().accept();

            Handler handler = new Handler();
            Client newClient = new Client();
            newClient.setAsynchronousServerSocketChannel(client.getAsynchronousServerSocketChannel());
            newClient.setAsynchronousSocketChannel(client.getAsynchronousSocketChannel());
            newClient.setClientId(clientId++);
            newClient.setByteBuffer(ByteBuffer.allocate(2048));
            newClient.setRead(false);
            newClient.setSocketAddress(socketAddress);

            Charset charset = Charset.forName("UTF-8");
            byte data[] = Integer.toString(newClient.getClientId()).getBytes(charset);
            newClient.setHandler(handler);
            newClient.getByteBuffer().put(data);
            newClient.getByteBuffer().flip();
            Route.addClient(newClient);
            asynchronousSocketChannel.write(newClient.getByteBuffer(), newClient, handler);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void failed(Throwable throwable, Client client) {
        System.out.println("Failed to accept connection...");
        throwable.printStackTrace();
    }
}
