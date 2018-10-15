package com.wphokomp.fixme.broker.Controller;

import com.wphokomp.fixme.broker.Model.Client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import com.wphokomp.fixme.broker.Handler.BrokerHandler;

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
        attach.asynchronousSocketChannel = channel;
        attach.byteBuffer = ByteBuffer.allocate(2048);
        attach.isRead = true;

        attach.mainThread = Thread.currentThread();

        /*Charset cs = Charset.forName("UTF-8");
        String msg = "Hello";
        byte[] data = msg.getBytes(cs);
        attach.buffer.put(data);
        attach.buffer.flip();*/

        BrokerHandler readWriteHandler = new BrokerHandler();
        channel.read(attach.byteBuffer, attach, readWriteHandler);
        try {
            Thread.currentThread().join();
        } catch (Exception e) {

        }
    }

    public static String sellProduct(int dst) {
        String soh = "" + (char) 1;
        String msg = "id=" + attach.clientId + soh + fixv + soh + "35=D" + soh + "54=2" + soh + "38=2" + soh + "44=55" + soh + "55=WTCSOCKS" + soh;
        msg += "50=" + attach.clientId + soh + "49=" + attach.clientId + soh + "56=" + dst + soh;
        if (qty > 0)
            return msg;
        else
            return "bye";
    }

    public static String buyProduct(int dst) {
        String soh = "" + (char) 1;
        String msg = "id=" + attach.clientId + soh + fixv + soh + "35=D" + soh + "54=1" + soh + "38=2" + soh + "44=90" + soh + "55=WTCSHIRTS" + soh;
        msg += "50=" + attach.clientId + soh + "49=" + attach.clientId + soh + "56=" + dst + soh;
        if (cash > 0)
            return msg;
        else
            return "bye";
    }

    public static boolean proccessReply(String reply) {
        String data[] = reply.split("" + (char) 1);
        String tag = "";
        String state = "";
        for (String dat : data) {
            if (dat.contains("35="))
                tag = dat.split("=")[1];
            if (dat.contains("39="))
                state = dat.split("=")[1];
        }
        if (tag.equals("8") && state.equals("8")) {
            System.out.println("\nMarket[" + dstId + "] rejected order\n");
            return false;
        }
        if (tag.equals("8") && state.equals("2")) {
            System.out.println("\nMarket[" + dstId + "] accepted order\n");
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
