package com.wphokomp.fixme.router.Handlers;

import com.wphokomp.fixme.router.Models.Client;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class Server implements Runnable {

    private String hostName;
    private int port;

    public Server(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open(); //www.java2s.com
            InetSocketAddress inetSocketAddress = new InetSocketAddress(this.hostName, this.port);
            asynchronousServerSocketChannel.bind(inetSocketAddress);
            if (this.port == 5000)
                System.out.format("Server listening on %s%n", inetSocketAddress);
            else
                System.out.format("Server listening on %s%n", inetSocketAddress);
            Client client = new Client();
            client.setAsynchronousServerSocketChannel(asynchronousServerSocketChannel);
            asynchronousServerSocketChannel.accept(client, new Connection());
            Thread.currentThread().join();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
