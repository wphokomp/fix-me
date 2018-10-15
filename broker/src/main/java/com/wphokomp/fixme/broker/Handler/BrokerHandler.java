package com.wphokomp.fixme.broker.Handler;

import com.wphokomp.fixme.broker.Controller.BrokerController;
import com.wphokomp.fixme.broker.Model.Client;

import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class BrokerHandler implements CompletionHandler<Integer, Client> {
    @Override
    public void completed(Integer result, Client attach) {
        if (result == -1) {
            attach.mainThread.interrupt();
            System.out.println("Server shutdown unexpectedly, Broker going offline...");
            return;
        }
        if (attach.isRead) {
            attach.byteBuffer.flip();
            Charset cs = Charset.forName("UTF-8");
            int limits = attach.byteBuffer.limit();
            byte bytes[] = new byte[limits];
            attach.byteBuffer.get(bytes, 0, limits);
            String msg = new String(bytes, cs);
            if (attach.clientId == 0) {
                attach.clientId = Integer.parseInt(msg);
                System.out.println("Server responded with Id: " + attach.clientId);
            } else
                System.out.println("Server Responded: " + msg.replace((char) 1, '|'));
            try {
                boolean s = BrokerController.proccessReply(msg);
                if (s == true && BrokerController.bs == 1)
                    BrokerController.updateData(true);
                if (s == true && BrokerController.bs == 0)
                    BrokerController.updateData(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            attach.byteBuffer.clear();
            msg = testMe(attach);
            if (msg.contains("bye") || i > 3) {
                attach.mainThread.interrupt();
                return;
            }
            i++;
            System.out.println("\nBroker response:" + msg.replace((char) 1, '|'));
            byte[] data = msg.getBytes(cs);
            attach.byteBuffer.put(data);
            attach.byteBuffer.flip();
            attach.isRead = false; // It is a write
            attach.asynchronousSocketChannel.write(attach.byteBuffer, attach, this);
        } else {
            attach.isRead = true;
            attach.byteBuffer.clear();
            attach.asynchronousSocketChannel.read(attach.byteBuffer, attach, this);
        }
    }

    @Override
    public void failed(Throwable e, Client attach) {
        e.printStackTrace();
    }

    private String testMe(Client attach) {
        String msg;

        if (BrokerController.bs == 1)
            msg = BrokerController.buyProduct(BrokerController.dstId);
        else
            msg = BrokerController.sellProduct(BrokerController.dstId);
        return msg + getCheckSum(msg);
    }

    private String getCheckSum(String msg) {
        int j = 0;
        char t[];
        String soh = "" + (char) 1;
        String datum[] = msg.split(soh);
        for (int k = 0; k < datum.length; k++) {
            t = datum[k].toCharArray();
            for (int i = 0; i < t.length; i++) {
                j += (int) t[i];
            }
            j += 1;
        }
        return ("10=" + (j % 256) + soh);
    }

    private static int i = 0;

}
