package com.wphokomp.fixme.router.Interface;

import com.wphokomp.fixme.router.Model.Client;

public interface IVerify {
    int CHECKSUM = 1;
    int DISPATCH = 2;
    int ECHO = 3;
    void performAction(Client client, int response);
}
