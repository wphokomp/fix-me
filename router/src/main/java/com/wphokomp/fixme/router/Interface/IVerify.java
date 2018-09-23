package com.wphokomp.fixme.router.Interface;

import com.wphokomp.fixme.router.Model.Client;

public interface IVerify {
    public static int CHECKSUM = 1;
    public static int DISPATCH = 2;
    public static int ECHO = 3;

    public void performAction(Client client, int response);
}
