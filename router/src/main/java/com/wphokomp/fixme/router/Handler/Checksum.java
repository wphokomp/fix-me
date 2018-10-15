package com.wphokomp.fixme.router.Handler;

import com.wphokomp.fixme.router.Interface.IVerify;
import com.wphokomp.fixme.router.Model.Client;

public class Checksum implements IVerify {
    private int CHECKSUM = IVerify.CHECKSUM;

    public void performAction(Client client, int response) {
        if (response != CHECKSUM) {
            new Redirect().performAction(client, response);
            return;
        }
        int size = getMessageSize(client.message);
        int checksum = getChecksum(client.message[client.message.length - 1]);
        int action = IVerify.ECHO;
        if (size % 256 != checksum)
            action = IVerify.ECHO;
        else
            action = IVerify.DISPATCH;
        new Redirect().performAction(client, action);
    }

    private int getMessageSize(String message[]) {
        int j = 0;
        char t[];
        for (int k = 0; k < message.length - 1; k++) {
            t = message[k].toCharArray();
            for (int i = 0; i < t.length; i++) {
                j += (int) t[i];
            }
            j += 1;
        }
        return (j);
    }

    private int getChecksum(String part) {
        int tag, value;
        try {
            String ops[] = part.split("=");
            tag = Integer.parseInt(ops[0]);
            value = Integer.parseInt(ops[1]);
            if (tag == 10)
                return value;
        } catch (Exception e) {
            System.out.println("Error message passed");
        }
        return (0);
    }
}
