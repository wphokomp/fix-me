package com.wphokomp.fixme.market;

public class Market {
    public static void main(String[] args) {
        com.wphokomp.fixme.market.Controller.Market market = new com.wphokomp.fixme.market.Controller.Market();
        try {
            market.connect();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
