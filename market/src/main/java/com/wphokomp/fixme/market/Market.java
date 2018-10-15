package com.wphokomp.fixme.market;

import com.wphokomp.fixme.market.Controller.MarketController;

public class Market {
    public static void main(String[] args) {
        MarketController market = new MarketController(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        try {
            market.connect();
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
