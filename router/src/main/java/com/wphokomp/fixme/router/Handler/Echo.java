package com.wphokomp.fixme.router.Handler;

import com.wphokomp.fixme.router.Interface.IVerify;
import com.wphokomp.fixme.router.Model.Client;

public class Echo implements IVerify {
    private int ECHO = IVerify.ECHO;

    public void performAction(Client client, int response) {
        if (response != ECHO)
            return ;
        client.setRead(false);
        client.getAsynchronousSocketChannel().write(client.getByteBuffer(), client, client.getRouterHandler());
    }
}
