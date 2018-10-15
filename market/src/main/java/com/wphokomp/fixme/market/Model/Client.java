package com.wphokomp.fixme.market.Model;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class Client {
    public AsynchronousSocketChannel asynchronousSocketChannel;
    public int clientId;
    public ByteBuffer byteBuffer;
    public Thread mainThread;
    public boolean isRead;
}
