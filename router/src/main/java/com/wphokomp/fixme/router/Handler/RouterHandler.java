package com.wphokomp.fixme.router.Handler;

import com.wphokomp.fixme.router.Controller.RouterController;
import com.wphokomp.fixme.router.Interface.IVerify;
import com.wphokomp.fixme.router.Model.Client;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class RouterHandler implements CompletionHandler<Integer, Client> {
    private String SOH;

    public RouterHandler() {
        SOH = "" + (char) 1;
    }

    @Override
    public void completed(Integer result, Client client) {

        if (result == -1) {
            try {
                client.getAsynchronousSocketChannel().close();
                RouterController.removeClient(client.getClientId());
                String port = client.getAsynchronousServerSocketChannel().getLocalAddress()
                        .toString().split(":")[1];
                String clientType = (port.equals("5000") ? "Broker" : "Market");
                System.out.format("Stopped listening to [" + clientType + "]%n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }

        if (client.isRead()) {
            client.getByteBuffer().flip();
            int limits = client.getByteBuffer().limit();
            byte bytes[] = new byte[limits];
            client.getByteBuffer().get(bytes, 0, limits);
            Charset charset = Charset.defaultCharset();
            String message = new String(bytes, charset);
            String datum[] = message.split(SOH);
            client.setMessage(datum);
            try {
                String port = client.getAsynchronousServerSocketChannel().getLocalAddress()
                        .toString().split(":")[1];
                String clientType = (port.equals("5000") ? "Broker" : "Market");
                System.out.format("[" + clientType + "] response: %s%n", message.replace((char) 1, '|'));
            } catch (Exception e) {
                System.out.println(e);
            }
            client.setRead(false); // It is a write
            client.getByteBuffer().rewind();
            client.getByteBuffer().clear();
            byte[] data = message.getBytes(charset);
            client.getByteBuffer().put(data);
            client.getByteBuffer().flip();
            if (client.getAsynchronousSocketChannel().isOpen() && RouterController.getSize() > 1) {
                new Checksum().performAction(client, IVerify.CHECKSUM);
            }
        } else {
            client.setRead(true);
            client.getByteBuffer().clear();
            client.getAsynchronousSocketChannel().read(client.getByteBuffer(), client, this);
        }
    }
     @Override
    public void failed(Throwable e, Client client) {
        e.printStackTrace();
    }
}
