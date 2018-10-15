package com.wphokomp.fixme.router.Handler;

import com.wphokomp.fixme.router.Controller.RouterController;
import com.wphokomp.fixme.router.Interface.IVerify;
import com.wphokomp.fixme.router.Model.Client;

public class Redirect implements IVerify {
    private int DISPATCH = IVerify.DISPATCH;

    public void performAction(Client client, int response) {
        if (response != DISPATCH) {
            new Echo().performAction(client, response);
            return;
        }
        int clientId = getDestination(client.message);
        int sourceId = getSource(client.message);
        if (sourceId != client.clientId) {
            System.out.println("Source: " + sourceId + "Client ID: " + clientId);
            new Echo().performAction(client, IVerify.ECHO);
            return;
        }

        try {
            if (client.asynchronousSocketChannel.isOpen() && RouterController.getSize() > 1) {
                Client client1 = RouterController.getClient(clientId);
                if (client1 == null) {
                    new Echo().performAction(client, IVerify.ECHO);
                    return;
                }
                client1.isRead = false;
                client1.asynchronousSocketChannel.write(client.byteBuffer, client1, client1.routerHandler);
            }
        } catch (Exception ex) {
            new Echo().performAction(client, IVerify.ECHO);
        }
    }

    private int getDestination(String message[]) {
        try {
            for (int i = 0; i < message.length; i++) {
                if (message[i].contains("56"))
                    return Integer.parseInt(message[i].split("=")[1]);
            }
        } catch (Exception ex) {
        }
        return -1;
    }

    private int getSource(String message[]) {
        try {
            if (message[0].split("=")[0].equalsIgnoreCase("id"))
                return Integer.parseInt(message[0].split("=")[1]);
        } catch (Exception ex) {}
        return -1;
    }
}
