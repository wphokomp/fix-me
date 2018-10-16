package com.wphokomp.fixme.router.Model;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import com.wphokomp.fixme.router.Handler.RouterHandler;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {
    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    private AsynchronousSocketChannel asynchronousSocketChannel;
    private int clientId;
    private ByteBuffer byteBuffer;
    private SocketAddress socketAddress;
    private String message[];
    private RouterHandler routerHandler;
    private boolean isRead;
}
