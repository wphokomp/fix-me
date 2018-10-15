package com.wphokomp.fixme.router.Handler;

import com.wphokomp.fixme.router.Model.Client;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class StartServer implements Runnable {
    private String host;
    private int port;

    public StartServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(this.host, this.port);
            asynchronousServerSocketChannel.bind(inetSocketAddress);
            if (this.port % 2 == 0) System.out.format("Server is listening for Brokers at: %s%n", inetSocketAddress);
            else System.out.format("Server is listening for Markets at: %s%n", inetSocketAddress);
            Client client = new Client();
            client.asynchronousServerSocketChannel = asynchronousServerSocketChannel;
            asynchronousServerSocketChannel.accept(client, new ConnectionHandler());
            Thread.currentThread().join();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
