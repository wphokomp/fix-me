package com.wphokomp.fixme.market.Controller;

import com.wphokomp.fixme.core.Controller.CoreControl;
import com.wphokomp.fixme.core.Model.Client;
import com.wphokomp.fixme.market.Handler.MarketHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Random;
import java.util.concurrent.Future;

public class MarketController {
    private static int qty;
    private static int price;
    private static int req;
    private static int dstId;
    private static final String fixv = "8=FIX.4.2";
    private static Client attach;

    public MarketController(int _qty, int _pr) {
        qty = _qty;
        price = _pr;
        try {
            Random rand = new Random();
            req = rand.nextInt(3) + 1;
        } catch (Exception e) {
            req = 2;
        }
    }

    public void connect() throws Exception {
        AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5001);
        Future<Void> result = asynchronousSocketChannel.connect(serverAddr);
        result.get();
        System.out.println("Connected");
        attach = new Client();
        attach.setAsynchronousSocketChannel(asynchronousSocketChannel);
        attach.setByteBuffer(ByteBuffer.allocate(2048));
        attach.setRead(true);
        attach.setMainThread(Thread.currentThread());

        MarketHandler marketHandler = new MarketHandler();
        asynchronousSocketChannel.read(attach.getByteBuffer(), attach, marketHandler);
        try {
            Thread.currentThread().join();
        } catch (Exception e) {

        }
    }

    public static String processRequest(String res) {
        String data[] = res.split("" + (char) 1);
        String msgType = "";
        String reqType = "";
        String price = "";
        String quant = "";
        for (String dat : data) {
            if (dat.contains("35="))
                msgType = dat.split("=")[1];
            else if (dat.contains("54="))
                reqType = dat.split("=")[1];
            else if (dat.contains("44="))
                price = dat.split("=")[1];
            else if (dat.contains("38="))
                quant = dat.split("=")[1];
            else if (dat.contains("id="))
                dstId = Integer.parseInt(dat.split("=")[1]);
        }
        return process(msgType, reqType, price, quant);
    }

    private static String process(String msgType, String reqType, String pric, String quant) {
        int p = Integer.parseInt(pric);
        int q = Integer.parseInt(quant);
        if (msgType.equals("D") && reqType.equals("2") && p < price && (req == 2 || req == 3))
            return getMessage(3, Integer.parseInt(quant)); //buy from broker
        else if (msgType.equals("D") && reqType.equals("1") && p >= price && qty - q >= 0 && (req == 2 || req == 3))
            return getMessage(2, Integer.parseInt(quant)); //sell to broker
        else
            return getMessage(1, Integer.parseInt(quant)); //reject broker request
    }

    private static String getMessage(int code, int quant) {
        String soh = "" + (char) 1;
        String msg = "";
        if (code == 1)
            msg = String.format("id=%d%s%s%s35=8%s39=8%s50=%d%s49=%d%s56=%d%s", attach.getClientId(), soh, fixv
                    , soh, soh, soh, attach.getClientId(), soh, attach.getClientId(), soh, dstId, soh);
        if (code == 2) {
            msg = String.format("id=%d%s%s%s35=8%s39=2%s50=%d%s49=%d%s56=%d%s", attach.getClientId(), soh, fixv
                    , soh, soh, soh, attach.getClientId(), soh, attach.getClientId(), soh, dstId, soh);
            qty -= quant;
        }
        if (code == 3) {
            msg = String.format("id=%d%s%s%s35=8%s39=2%s50=%d%s49=%d%s56=%d%s", attach.getClientId(), soh, fixv
                    , soh, soh, soh, attach.getClientId(), soh, attach.getClientId(), soh, dstId, soh);
            qty += quant;
        }
        return msg + CoreControl.getCheckSum(msg);
    }
}
