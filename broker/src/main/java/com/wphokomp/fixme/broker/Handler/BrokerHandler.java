package com.wphokomp.fixme.broker.Handler;

import com.wphokomp.fixme.broker.Controller.BrokerControler;
import com.wphokomp.fixme.core.Models.Client;

import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class BrokerHandler implements CompletionHandler<Integer, Client> {
    @Override
    public void completed(Integer result, Client client) {
        if (result == -1)
        {
            client.getMainThread().interrupt();
            System.out.println("Broker disconnected...");
            return ;
        }
        if (client.isRead()) {
            client.getByteBuffer().flip();
            Charset cs = Charset.defaultCharset();
            int byteBufferLimit = client.getByteBuffer().limit();
            byte bytes[] = new byte[byteBufferLimit];
            client.getByteBuffer().get(bytes, 0, byteBufferLimit);
            String msg = new String(bytes, cs);
            if (client.getClientId() == 0)
            {
                client.setClientId(Integer.parseInt(msg));
                System.out.printf("Server response: %d%n", client.getClientId());
            }
            else
                System.out.printf("Server response: %s%n", msg.replace((char) 1, '|'));
            try {
                boolean s = BrokerControler.processResponse(msg);
                if (s == true && BrokerControler.broketStatus == 1)
                    BrokerControler.update(true);
                if (s == true && BrokerControler.broketStatus == 0)
                    BrokerControler.update(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            client.getByteBuffer().clear();
//            msg = testMe(client);
            if (msg.contains("disconnect")) {
                client.getMainThread().interrupt();
                return;
            }
//            i++;
            System.out.println("\nBroker response:" + msg.replace((char)1, '|'));
            byte[] data = msg.getBytes(cs);
            client.getByteBuffer().put(data);
            client.getByteBuffer().flip();
            client.setRead(false);
            client.getAsynchronousSocketChannel().write(client.getByteBuffer(), client, this);
        }else {
            client.setRead(true);
            client.getByteBuffer().clear();
            client.getAsynchronousSocketChannel().read(client.getByteBuffer(), client, this);
        }
    }

    @Override
    public void failed(Throwable exc, Client client) {
        exc.printStackTrace();
    }
}
