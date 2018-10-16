package com.wphokomp.fixme.broker.Handler;

import com.wphokomp.fixme.broker.Controller.BrokerController;
import com.wphokomp.fixme.core.Controller.CoreControl;
import com.wphokomp.fixme.core.Model.Client;

import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class BrokerHandler implements CompletionHandler<Integer, Client> {
    @Override
    public void completed(Integer result, Client attach) {
        if (result == -1) {
            attach.getMainThread().interrupt();
            System.out.format("Server shutdown unexpectedly, Broker[] going offline...", attach.getClientId());
            return;
        }
        if (attach.isRead()) {
            attach.getByteBuffer().flip();
            Charset charset = Charset.defaultCharset();
            int limits = attach.getByteBuffer().limit();
            byte bytes[] = new byte[limits];
            attach.getByteBuffer().get(bytes, 0, limits);
            String msg = new String(bytes, charset);
            if (attach.getClientId() == 0) {
                attach.setClientId(Integer.parseInt(msg));
                System.out.println("Allocated ID: " + attach.getClientId());
            } else
                System.out.println("Server:" + msg.replace((char) 1, '|'));
            try {
                boolean s = BrokerController.processReply(msg);
                if (s == true && BrokerController.bs == 1)
                    BrokerController.updateData(true);
                if (s == true && BrokerController.bs == 0)
                    BrokerController.updateData(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            attach.getByteBuffer().clear();
            msg = makeTransaction();
            if (msg.contains("bye") || i > 3) {
                attach.getMainThread().interrupt();
                return;
            }
            i++;
            System.out.println("\nBroker:" + msg.replace((char) 1, '|'));
            byte[] data = msg.getBytes(charset);
            attach.getByteBuffer().put(data);
            attach.getByteBuffer().flip();
            attach.setRead(false); // It is a write
            attach.getAsynchronousSocketChannel().write(attach.getByteBuffer(), attach, this);
        } else {
            attach.setRead(true);
            attach.getByteBuffer().clear();
            attach.getAsynchronousSocketChannel().read(attach.getByteBuffer(), attach, this);
        }
    }

    @Override
    public void failed(Throwable e, Client attach) {
        e.printStackTrace();
    }

    private String makeTransaction() {
        String msg;

        if (BrokerController.bs == 1)
            msg = BrokerController.buyProduct(BrokerController.dstId);
        else
            msg = BrokerController.sellProduct(BrokerController.dstId);
        return msg + CoreControl.getCheckSum(msg);
    }

    private static int i = 0;

}
