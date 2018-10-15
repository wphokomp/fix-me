package com.wphokomp.fixme.router;

import com.wphokomp.fixme.router.Controller.RouterController;

public class Router {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        RouterController routerController = new RouterController(host, port);

        try {
            routerController.startServers();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
