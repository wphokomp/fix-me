package com.wphokomp.fixme.router;

import com.wphokomp.fixme.router.Controller.Route;

public class Router {
    public static void main(String[] args) {
        String hostName = "127.0.0.1";
        int initPort = 5000;

        Route router = new Route(hostName, initPort);
        try {
            router.start();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
