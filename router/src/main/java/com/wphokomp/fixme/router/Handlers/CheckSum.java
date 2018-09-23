package com.wphokomp.fixme.router.Handlers;

import com.wphokomp.fixme.router.Interface.IVerify;
import com.wphokomp.fixme.router.Model.Client;

public class CheckSum implements IVerify {
    @Override
    public void performAction(Client client, int response) {
        if (response != IVerify.CHECKSUM) {
            new Redirect().performAction(client, response);
            return;
        }

        int messageSize = messageSize(client.getMessage());
        int checksum = getChecksum(client.getMessage()[client.getMessage().length - 1]);
        int action = IVerify.ECHO;
        if (messageSize % 256 == checksum)
            action = IVerify.DISPATCH;
        new Redirect().performAction(client, action);
    }

    private int messageSize(String message[]) {
        int j = 0;
        char t[];

        for (int k = 0; k < message.length - 1; k++) {
            t = message[k].toCharArray();
            for (int i = 0; i < t.length; i++) {
                j += (int) t[i];
            }
            j += 1;
        }
        return j;
    }

    private int getChecksum(String check) {
        int tag, value;

        try {
            String operations[] = check.split("=");
            tag = Integer.parseInt(operations[0]);
            value = Integer.parseInt(operations[1]);
            if (tag == 10)
                return value;
        } catch (Exception ex) {
            System.out.println("Error: Message passed");
        }
        return 0;
    }

}
