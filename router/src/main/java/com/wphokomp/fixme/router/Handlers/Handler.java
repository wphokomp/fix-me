package com.wphokomp.fixme.router.Handlers;

import com.wphokomp.fixme.router.Controller.Route;
import com.wphokomp.fixme.router.Interface.IVerify;
import com.wphokomp.fixme.router.Model.Client;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class Handler implements CompletionHandler<Integer, Client> {
    private String soh;

    public Handler() {
        soh = "" + (char) 1;
    }

    @Override
    public void completed(Integer res, Client client) {
        if (res == -1) {
            try {
                client.getAsynchronousSocketChannel().close();
                Route.removeClient(client.getClientId());
                String port = client.getAsynchronousServerSocketChannel()
                        .getLocalAddress().toString().split(":")[1];

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }
        readable(res, client);
    }

    private void readable(Integer res, Client client) {
        if (client.isRead()) {
            client.getByteBuffer().flip();
            int limits = client.getByteBuffer().limit();
            byte bytes[] = new byte[limits];

            Charset charset = Charset.forName("UTF-8");
            String message = new String(bytes, charset);
            String date[] = message.split(soh);
            client.setMessage(date);

            try {
                String port = client.getAsynchronousServerSocketChannel().toString().split(":")[1];
                String clientName = (port == "5000" ? "Broker" : "Market");
                System.out.format(String.format("[%s] Client at %%s: %%s%%n", clientName), client.getSocketAddress());
                message.replace((char) 1, '|');
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            client.setRead(false);
            client.getByteBuffer().rewind();
            client.getByteBuffer().clear();
            byte[] data = message.getBytes(charset);
            client.getByteBuffer().put(data);
            client.getByteBuffer().flip();

            if (client.getAsynchronousSocketChannel().isOpen() && Route.getClients().size() > 1) {
                new CheckSum().performAction(client, IVerify.CHECKSUM);
            } else {
                client.setRead(true);
                client.getByteBuffer().clear();
                client.getAsynchronousSocketChannel().read(client.getByteBuffer(), client, this);
            }
        }
    }

    @Override
    public void failed(Throwable throwable, Client client) {
        throwable.printStackTrace();
    }

}
