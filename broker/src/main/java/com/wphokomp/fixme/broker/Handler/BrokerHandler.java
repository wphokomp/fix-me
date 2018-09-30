package com.wphokomp.fixme.broker.Handler;

import com.wphokomp.fixme.broker.Controller.BrokerControler;
import com.wphokomp.fixme.core.Control.CoreControl;
import com.wphokomp.fixme.core.Models.Client;

import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class BrokerHandler implements CompletionHandler<Integer, Client> {
    private static int i = 0;
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
            String message = new String(bytes, cs);
            if (client.getClientId() == 0)
            {
                client.setClientId(Integer.parseInt(message));
                System.out.printf("Server response: %d%n", client.getClientId());
            }
            else
                System.out.printf("Server response: %s%n", message.replace((char) 1, '|'));
            try {
                boolean s = BrokerControler.processResponse(message);
                if (s == true && BrokerControler.broketStatus == 1)
                    BrokerControler.update(true);
                if (s == true && BrokerControler.broketStatus == 0)
                    BrokerControler.update(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            client.getByteBuffer().clear();
            message = getMessage();
            if (message.contains("disconnect") || i > 3) {
                client.getMainThread().interrupt();
                return;
            }
            i++;
            System.out.println("\nBroker response:" + message.replace((char)1, '|'));
            byte[] data = message.getBytes(cs);
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

    private String getMessage() {
        String message = "";

        if (BrokerControler.broketStatus == 1)
            message = BrokerControler.buyInstrument(BrokerControler.marketId);
        else
            message = BrokerControler.sellInstrument(BrokerControler.marketId);
        return message + CoreControl.getChecksum(message);
    }
}
