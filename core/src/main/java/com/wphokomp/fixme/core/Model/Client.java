package com.wphokomp.fixme.core.Model;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

@Getter
@Setter
public class Client {
    private AsynchronousSocketChannel asynchronousSocketChannel;
    private int clientId;
    private ByteBuffer byteBuffer;
    private Thread mainThread;
    private boolean isRead;
}
