package com.wphokomp.fixme.broker;

import com.wphokomp.fixme.broker.Controller.BrokerControler;

public class Broker {
    public static void main(String[] args) {
        BrokerControler brokerControler = new BrokerControler(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

        try {
            brokerControler.connect();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
