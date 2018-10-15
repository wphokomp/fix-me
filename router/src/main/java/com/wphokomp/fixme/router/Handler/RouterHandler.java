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
    public void completed(Integer result, Client attach) {

        if (result == -1) {
            try {
                attach.asynchronousSocketChannel.close();
                RouterController.removeClient(attach.clientId);
                String port = attach.asynchronousServerSocketChannel.getLocalAddress().toString().split(":")[1];
                System.out.format("[" + getServerName(port) + "]Stopped   listening to the   client %s%n",
                        attach.socketAddress);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }

        if (attach.isRead) {
            attach.byteBuffer.flip();
            int limits = attach.byteBuffer.limit();
            byte bytes[] = new byte[limits];
            attach.byteBuffer.get(bytes, 0, limits);
            Charset cs = Charset.forName("UTF-8");
            String msg = new String(bytes, cs);
            String datum[] = msg.split(SOH);
            attach.message = datum;
            try {
                String port = attach.asynchronousServerSocketChannel.getLocalAddress().toString().split(":")[1];
                System.out.format("[" + getServerName(port) + "]Client at  %s  says: %s%n", attach.socketAddress,
                        msg.replace((char) 1, '|'));
            } catch (Exception e) {
                System.out.println(e);
            }
            attach.isRead = false; // It is a write
            attach.byteBuffer.rewind();
            attach.byteBuffer.clear();
            byte[] data = msg.getBytes(cs);
            attach.byteBuffer.put(data);
            attach.byteBuffer.flip();
            if (attach.asynchronousSocketChannel.isOpen() && RouterController.getSize() > 1) {
                new Checksum().performAction(attach, IVerify.CHECKSUM);
                //attach.client.write(attach.byteBuffer, attach, this);
            }
        /*attach.byteBuffer.put(bytes);
        attach.client.write(attach.byteBuffer, attach, this);*/

        } else {
            // Write to the client
            //System.out.println("Hello");
            //attach.client.write(attach.byteBuffer, attach, this);
            attach.isRead = true;
            attach.byteBuffer.clear();
            attach.asynchronousSocketChannel.read(attach.byteBuffer, attach, this);

        }
    }
     @Override
    public void failed(Throwable e, Client attach) {
        e.printStackTrace();
    }

    private String getServerName(String port) {
        if (port.equals("5000"))
            return "Broker Server";
        else
            return "Market Server";
    }

}
