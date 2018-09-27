package com.wphokomp.fixme.broker.Controller;

import com.wphokomp.fixme.broker.Handler.BrokerHandler;
import com.wphokomp.fixme.core.Models.Client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

public class BrokerControler {
    private static int _quantity;
    private static int _pocket;
    private static Client client;
    public static final String fixValue = "8=FIX.4.2";
    public static int broketStatus;
    public static int marketId;

    public BrokerControler(int id, int status) {
        broketStatus = status;
        marketId = id;
        _pocket = 1234567;
        _quantity = 22;
    }

    public void connect() throws Exception {
        AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 5000);
        Future<Void> result = asynchronousSocketChannel.connect(socketAddress);
        result.get();
        System.out.println("Broker connected...");
        client = new Client();
        client.setAsynchronousSocketChannel(asynchronousSocketChannel);
        client.setByteBuffer(ByteBuffer.allocate(2048));
        client.setRead(true);

        client.setMainThread(Thread.currentThread());

        BrokerHandler brokerHandler = new BrokerHandler();
        asynchronousSocketChannel.read(client.getByteBuffer(), client, brokerHandler);
        try {
            Thread.currentThread().join();
        } catch (Exception ex) {
        }
    }

    public static String sellInstrument(int buyer) {
        String soh = "\u0001";
        String message = String.format("id=%d%s%s%s35=D%s54=2%s38=2%s44=55%s55=pizza%s50=%d%s49=%d%s56=%d%s"
                , client.getClientId(), soh, fixValue, soh, soh, soh, soh, soh, soh, client.getClientId()
                , soh, client.getClientId(), soh, buyer, soh);
        if (_quantity > 0)
            return (message);
        else
            return ("disconnect");
    }

    public static String buyInstrument(int seller) {
        String soh = "" + (char) 1;
        String message = String.format("id=%d%s%s%s35=D%s54=1%s38=2%s44=90%s55=beer%s50=%d%s49=%d%s56=%d%s"
                , client.getClientId(), soh, fixValue, soh, soh, soh, soh, soh, soh, client.getClientId()
                , soh, client.getClientId(), soh, marketId, soh);
        if (_pocket > 0)
            return (message);
        else
            return ("disconnect");
    }

    public static void update(boolean buySell) {
        if (buySell) {
            _quantity += 4;
            _pocket -= 100;
        } else {
            _quantity -= 4;
            _pocket += 66;
        }
    }

    public static boolean processResponse(String response) {
        String data[]  = response.split("\u0001");
        String tag = null;
        String buySell = null;

        for (String _data:
             data) {
            if (_data.contains("35="))
                tag = _data.split("=")[1];
            if (_data.contains("39="))
                buySell = _data.split("=")[1];
        }
        if (tag != null && tag.equals("8") && tag.equals("8")) {
            System.out.println("\nMarket[" + marketId + "]: Order rejected.");
            return false;
        }
        if (tag != null && tag.equals("8") && tag.equals("2")) {
            System.out.println("\nMarket[" + marketId + "]: Order approved.");
            return true;
        }
        return false;
    }
}
