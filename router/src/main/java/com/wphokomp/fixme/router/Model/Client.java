package com.wphokomp.fixme.router.Model;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import com.wphokomp.fixme.router.Handler.RouterHandler;

public class Client {
    public AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    public AsynchronousSocketChannel asynchronousSocketChannel;
    public int clientId;
    public ByteBuffer byteBuffer;
    public SocketAddress socketAddress;
    public String message[];
    public RouterHandler routerHandler;
    public boolean isRead;
}
