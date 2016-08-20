package com.jsifuentes.core;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.*;

/**
 * Created by Jacob on 11/18/2014.
 */
public class ChanServer implements Runnable {
    private static ChanServer Instance;

    private int Port;
    private ServerSocket ServerSocket;
    private static Hashtable<Object, ChanClient> Clients = new Hashtable<Object, ChanClient>();
    private boolean Started = false;

    public static ChanServer getInstance() {
        if(Instance == null) {
            Object port = Configuration.get("server.port");
            Integer p = Integer.parseInt((String)port);

            Instance = new ChanServer(p);
        }

        return Instance;
    }

    private ChanServer(int port) {
        this.Port = port;
    }

    public void run() {
        try {
            Output.debug("Attempting to start server on port " + this.Port + "...");
            this.start(this.Port);
            Output.debug("Server started! Listening for connections...");


            while(this.Started) {
                final ChanClient client = this.acceptConnection();

                if(client == null) {
                    continue;
                }

                Clients.put(client.getUniqueID(), client);

                (new Thread(new Runnable() {
                    public void run() {
                        Output.debug("Connection received from " + client.getClientSocket().getRemoteSocketAddress() + " -- " + ChanServer.countConnections() + " current sessions");
                        // client.writeToClient("Connected!");
                        // Initialize protocol.
                        ChanProtocol chanProtocol = new ChanProtocol();
                        String inputLine;
                        while (!client.isClosed() && (inputLine = client.readInput()) != null) {
                            if(inputLine.length() == 0) {
                                continue;
                            }

                            Output.debug("Received input: " + inputLine);
                            Object interpretation = chanProtocol.interpret(inputLine, client);

                            if(interpretation != null)
                                client.writeToClient(interpretation.toString());
                        }

                        ChanServer.removeClient(client.getUniqueID());
                        Output.debug("Connection(" + client.getUniqueID() + ") lost");
                    }
                })).start();
            }
        } catch(Exception exc) {
            Output.error(exc.getMessage());
            //System.exit(1);
        }
    }

    public void start(int port) {
        try {
            this.ServerSocket = new ServerSocket(port);
            this.Started = true;
        } catch(IOException ioExc) {
            Output.error("Failed to start up server. " + ioExc.getMessage());
            System.exit(1);
        }
    }

    public ChanClient acceptConnection() {
        try {
            final ChanClient client = new ChanClient();
            client.setUniqueID(UUID.randomUUID().toString());
            client.setSocket(this.ServerSocket.accept());
            client.setReader(new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream())));
            client.setWriter(new PrintWriter(client.getClientSocket().getOutputStream(), true));
            client.setIP(client.getSocket().getRemoteSocketAddress().toString());

            return client;
        } catch(Exception ioExc) {
            Output.error("Failed to accept client socket! " + ioExc.getMessage());
            return null;
        }
    }

    public static void replaceClient(Object index, ChanClient client) {
        if(Clients.containsKey(index)) {
            Clients.remove(index);
            Clients.put(index, client);
        }
    }

    public static void removeClient(Object uniqueID) {
        if(Clients.containsKey(uniqueID)) {
            Clients.remove(uniqueID);
        }
    }

    public static Hashtable<Object, ChanClient> getClients() {
        return Clients;
    }

    public void stop() {
        try {
            this.Started = false;
            for (ChanClient client : Clients.values()) {
                client.close();
            }
            this.ServerSocket.close();

            Output.debug("Server is now down.");
        } catch(IOException ioExc) {
            Output.error("Failed to close server socket! " + ioExc.getMessage());
        }
    }

    public static Integer countConnections() {
        return Clients.size();
    }
}
