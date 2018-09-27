package com.wphokomp.fixme.core.Models;

import com.wphokomp.fixme.router.Handlers.Handler;
import lombok.Getter;
import lombok.Setter;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

@Getter
@Setter
public class Client {
    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    private AsynchronousSocketChannel asynchronousSocketChannel;
    private SocketAddress socketAddress;
    private ByteBuffer byteBuffer;
    private String message[];
    private Handler handler;
    private boolean isRead;
    private int clientId;
    private Thread mainThread;

}

