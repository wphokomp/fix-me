package com.wphokomp.fixme.market.Handler;

import com.wphokomp.fixme.core.Controller.CoreControl;
import com.wphokomp.fixme.core.Model.Client;
import com.wphokomp.fixme.market.Controller.MarketController;
//import com.wphokomp.fixme.market.Model.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class MarketHandler implements CompletionHandler<Integer, Client> {
    @Override
    public void completed(Integer result, Client attach) {
        if (result == -1) {
            attach.getMainThread().interrupt();
            System.out.println("Server shutdown unexpectedly, Market going offline...");
            return;
        }
        if (attach.isRead()) {
            attach.getByteBuffer().flip();
            Charset charset = Charset.forName("UTF-8");
            int limits = attach.getByteBuffer().limit();
            byte bytes[] = new byte[limits];
            attach.getByteBuffer().get(bytes, 0, limits);
            String msg = new String(bytes, charset);

            if (attach.getClientId() == 0) {
                attach.setClientId(Integer.parseInt(msg));
                System.out.println("Allocated ID: " + attach.getClientId());
                attach.setRead(false);
                attach.getAsynchronousSocketChannel().read(attach.getByteBuffer(), attach, this);
                System.out.println("Awaiting transaction...");
                return;
            } else
                System.out.println("Server: " + msg.replace((char) 1, '|'));


            attach.getByteBuffer().clear();
            msg = MarketController.processRequest(msg);
            if (msg.contains("bye")) {
                attach.getMainThread().interrupt();
                return;
            }
            try {
                System.out.println("\nMarket: " + msg.replace((char) 1, '|'));
            } catch (Exception e) {
            }
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
}
