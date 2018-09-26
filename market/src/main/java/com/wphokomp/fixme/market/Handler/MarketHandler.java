package com.wphokomp.fixme.market.Handler;

import com.wphokomp.fixme.core.Models.Client;

import java.nio.channels.CompletionHandler;

public class MarketHandler implements CompletionHandler<Integer, Client> {
    @Override
    public void completed(Integer result, Client client) {

    }

    @Override
    public void failed(Throwable throwable, Client client) {
        throwable.printStackTrace();
    }
}
