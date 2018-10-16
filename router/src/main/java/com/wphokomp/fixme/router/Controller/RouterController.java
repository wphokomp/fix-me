package com.wphokomp.fixme.router.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wphokomp.fixme.router.Handler.StartServer;
import com.wphokomp.fixme.router.Model.Client;

public class RouterController {
    private String host;
    private int port;
    private static List<Client> clients = new ArrayList<Client>();

    public RouterController(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void addClient(Client client) {
        clients.add(client);
    }

    public static Client getClient(int clientId) {
        for (Client _client :
                clients) {
            if (_client.getClientId() == clientId)
                return _client;
        }
        return null;
    }

    public static int getSize() {
        return clients.size();
    }

    public void startServers() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new StartServer(this.host, this.port));
        executorService.submit(new StartServer(this.host, this.port + 1));
        executorService.shutdown();
    }

    public static void removeClient(int clientId) {
        try {
            clients.remove(getClient(clientId));
        } catch (Exception ex) {
        }
    }
}
