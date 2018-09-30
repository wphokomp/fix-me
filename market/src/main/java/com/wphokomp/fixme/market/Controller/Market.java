package com.wphokomp.fixme.market.Controller;

import com.wphokomp.fixme.core.Control.CoreControl;
import com.wphokomp.fixme.core.Core;
import com.wphokomp.fixme.market.Handler.MarketHandler;
import com.wphokomp.fixme.core.Models.Client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Random;
import java.util.concurrent.Future;

public class Market {
    private static int _quantity;
    private static int _value;
    private static int _request;
    private static int brokerId;
    private static final String fixValue = "8=FIX.4.2";
    private static Client client;

    public Market() {
        _quantity = 10;
        _request = 10;
        try {
            Random random = new Random();
            _request = random.nextInt(3) + 1;
        } catch (Exception ex) {
            _request = 2;
        }
    }

    public void connect() throws Exception {
        AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 5001);
        Future<Void> result = asynchronousSocketChannel.connect(socketAddress);
        result.get();
        System.out.println("Market connected to the server...");

        client = new Client();
        client.setAsynchronousSocketChannel(asynchronousSocketChannel);
        client.setByteBuffer(ByteBuffer.allocate(2048));
        client.setRead(true);
        client.setMainThread(Thread.currentThread());

        MarketHandler marketHandler = new MarketHandler();
        asynchronousSocketChannel.read(client.getByteBuffer(), client, marketHandler);
        try {
            Thread.currentThread().join();
        } catch (Exception ex) {
        }
    }

    private static String processMessage(String messageType, String requestType, String value, String quantity) {
        int val = Integer.parseInt(value);
        int number = Integer.parseInt(quantity);
        if (messageType.equals("D") && requestType.equals("2") && val < _value && (_request == 2 || _request == 3))
            return (getMessage(3, Integer.parseInt(quantity))); // When the broker sells an instrument
        else if (messageType.equals("D") && requestType.equals("1")
                && val >= _value && _quantity - number >= 0 && (_request == 2 || _request == 3))
            return (getMessage(2, Integer.parseInt(quantity))); // When an instrument is being bought by a broker
        else
            return (getMessage(1, Integer.parseInt(quantity))); // Reject a request from a broker
    }

    public static String processRequest(String result) {
        String data[] = result.split("" + (char) 1);
        String messageType = "";
        String requestType = "";
        String quantity = "";
        String value = "";
        String res = null;

        for (String _data :
                data) {
            res = _data.split("=")[1];
            ;
            if (_data.contains("35="))
                messageType = res;
            else if (_data.contains("54="))
                requestType = res;
            else if (_data.contains("44="))
                value = res;
            else if (_data.contains("38="))
                quantity = res;
            else if (_data.contains("id="))
                brokerId = Integer.parseInt(res);
        }
        return (processMessage(messageType, requestType, value, quantity));
    }

    private static String getMessage(int code, int quantity) {
        String message = null;
        String soh = "" + (char) 1;

        message = String.format("id=%d%s%s%s35=8%s39=8%s50=%d%s49=%d%s56=%d%s", client.getClientId(), soh, fixValue, soh, soh, soh, client.getClientId(), soh, client.getClientId(), soh, brokerId, soh);
        switch (code) {
            case 2:
                _quantity -= quantity;
                break;
            case 3:
                _quantity += quantity;
        }
        return (message + CoreControl.getChecksum(message));
    }

}
