package com.jsifuentes;

import com.jsifuentes.archive.QueueManager;
import com.jsifuentes.core.ChanServer;
import com.jsifuentes.core.Configuration;
import com.jsifuentes.core.Output;

public class Main {

    public static void main(String[] args) {
        Output.raw("//////////////////////////////////////////////////////");
        Output.raw("//                 A 4chan fetcher                  //");
        Output.raw("//                    Jacob S.                      //");
        Output.raw("//                  4archive.org                    //");
        Output.raw("//////////////////////////////////////////////////////");

        // Read config
        Configuration.parseConfig();
        Output.debug("Configuration read!");

        // Start server
        (new Thread(ChanServer.getInstance())).start();
        Output.debug("Server thread started!");

        // Queue manager
        (new Thread(new QueueManager())).start();
        Output.debug("Queue Manager thread started!");
    }
}
