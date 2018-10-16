package com.wphokomp.fixme.broker.Controller;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import com.wphokomp.fixme.broker.Handler.BrokerHandler;
import com.wphokomp.fixme.core.Model.Client;

public class BrokerController {
    private static int qty = 10;
    private static int cash = 10000000;
    private static Client attach;
    private static final String fixv = "8=FIX.4.2";
    public static int bs;
    public static int dstId;

    public BrokerController(int id, int by) {
        dstId = id;
        bs = by;
    }

    public void connect() throws Exception {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5000);
        Future<Void> result = channel.connect(serverAddr);
        result.get();
        System.out.println("Connected");
        attach = new Client();
        attach.setAsynchronousSocketChannel(channel);
        attach.setByteBuffer(ByteBuffer.allocate(2048));
        attach.setRead(true);

        attach.setMainThread(Thread.currentThread());

        BrokerHandler readWriteHandler = new BrokerHandler();
        channel.read(attach.getByteBuffer(), attach, readWriteHandler);
        try {
            Thread.currentThread().join();
        } catch (Exception e) {

        }
    }

    public static String sellProduct(int dst) {
        String soh = "" + (char) 1;
        String msg = String.format("id=%d%s%s%s35=D%s54=2%s38=2%s44=55%s55=Nuts%s", attach.getClientId()
                , soh, fixv, soh, soh, soh, soh, soh, soh);
        msg += String.format("50=%d%s49=%d%s56=%d%s", attach.getClientId(), soh, attach.getClientId(), soh, dst, soh);
        if (qty > 0)
            return msg;
        else
            return "bye";
    }

    public static String buyProduct(int dst) {
        String soh = "" + (char) 1;
        String msg = String.format("id=%d%s%s%s35=D%s54=1%s38=2%s44=90%s55=Beer%s", attach.getClientId()
                , soh, fixv, soh, soh, soh, soh, soh, soh);
        msg += String.format("50=%d%s49=%d%s56=%d%s", attach.getClientId(), soh, attach.getClientId(), soh, dst, soh);
        if (cash > 0)
            return msg;
        else
            return "bye";
    }

    public static boolean processReply(String reply) {
        String data[] = reply.split("" + (char) 1);
        String tag= "", state = "";
        for (String dat : data) {
            if (dat.contains("35="))
                tag = dat.split("=")[1];
            if (dat.contains("39="))
                state = dat.split("=")[1];
        }
        if (tag.equals("8") && state.equals("8")) {
            System.out.printf("\nMarket[%d] rejected order\n%n", dstId);
            return false;
        }
        if (tag.equals("8") && state.equals("2")) {
            System.out.printf("\nMarket[%d] accepted order\n%n", dstId);
            return true;
        }
        return false;
    }

    public static void updateData(boolean state) {
        if (state == false) {
            qty -= 2;
            cash += 55;
        } else {
            qty += 2;
            cash -= 90;
        }
    }

}
