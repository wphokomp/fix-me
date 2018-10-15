package com.wphokomp.fixme.broker;

import com.wphokomp.fixme.broker.Controller.BrokerController;

public class Broker {
    public static void main(String[] args) {
        BrokerController brokerController = new BrokerController(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

        try {
            brokerController.connect();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
