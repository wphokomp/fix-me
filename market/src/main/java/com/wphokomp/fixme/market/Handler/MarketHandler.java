package com.wphokomp.fixme.market.Handler;

import com.wphokomp.fixme.core.Models.Client;
import com.wphokomp.fixme.market.Controller.Market;

import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class MarketHandler implements CompletionHandler<Integer, Client> {
    @Override
    public void completed(Integer result, Client client) {
        if (result == -1) {
            client.getMainThread().interrupt();
            System.out.println("Market disconnected...");
            return;
        }

        if (client.isRead()) {
            client.getByteBuffer().flip();
            Charset charset = Charset.defaultCharset(); //On this computer (MacOS) it's UTF-8
            int byteBufferLimit = client.getByteBuffer().limit();
            byte[] bytes = new byte[byteBufferLimit];
            client.getByteBuffer().get(bytes, 0, byteBufferLimit);
            String message = new String(bytes, charset);

            if (client.getClientId() == 0) {
                client.setClientId(Integer.parseInt(message));
                System.out.printf("Server response: %d%n", client.getClientId());
                client.setRead(false);
                client.getAsynchronousSocketChannel().read(client.getByteBuffer(), client, this);
                return;
            } else
                System.out.printf("Server response: %s%n", message.replace((char) 1, '|'));

            client.getByteBuffer().clear();
            message = Market.processRequest(message);
            if (message.contains("disconnect")) {
                client.getMainThread().interrupt();
                return;
            }

            try {
                System.out.printf("\nMarket response: %s%n", message.replace((char) 1, '|'));
            } catch (Exception ex) {
            }

            byte[] data = message.getBytes(charset);
            client.getByteBuffer().put(data);
            client.getByteBuffer().flip();
            client.setRead(false);
            client.getAsynchronousSocketChannel().write(client.getByteBuffer(), client, this);
        } else {
            client.setRead(true);
            client.getByteBuffer().clear();
            client.getAsynchronousSocketChannel().read(client.getByteBuffer(), client, this);
        }
    }

    @Override
    public void failed(Throwable throwable, Client client) {
        throwable.printStackTrace();
    }

}
