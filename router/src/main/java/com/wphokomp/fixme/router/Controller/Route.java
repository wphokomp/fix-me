package com.wphokomp.fixme.router.Controller;

import com.wphokomp.fixme.router.Handlers.Server;
import com.wphokomp.fixme.router.Model.Client;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Route {
    private String hostName;
    private int port;
    @Getter
    private static List<Client> clients = new ArrayList<>();

    public Route(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public static void addClient(Client client) {
        clients.add(client);
    }

    public static Client getClient(int id) {
        for (Client client:
             clients) {
            if (client.getClientId() == id)
                return client;
        }
        return null;
    }

    public void start() throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Server(this.hostName, this.port));
        executorService.submit(new Server(this.hostName, ++this.port));
        executorService.shutdown();
    }

    public static void removeClient(int id) {
        try {
            clients.remove(getClient(id));
        } catch (Exception ex) {}
    }
}
