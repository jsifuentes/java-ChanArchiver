package com.jsifuentes.core;

import com.jsifuentes.archive.Queue;
import com.jsifuentes.archive.QueueItem;
import com.jsifuentes.fourchan.FourChanThread;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Jacob on 11/20/2014.
 */
public class Actions{
    private ChanClient Client;
    private ArrayList<String> AvailableActions = new ArrayList<String>() {{
        add("archive");
        add("authenticate");
    }};
    private ArrayList<String> AvailableAdminActions = new ArrayList<String>() {{
        add("stopServer");
        add("showClients");
        add("dropClient");
        add("reload");
    }};
    private ArrayList<String> AvailableChans = new ArrayList<String>() {{
        add("4chan");
    }};
    private HashMap<String, String> Logins = new HashMap<String, String>() {{
        put("jacob", "password");
    }};


    public Actions(ChanClient client) {
        this.Client = client;
    }

    public boolean isValidAction(String action) {
        return this.AvailableActions.contains(action) || (this.AvailableAdminActions.contains(action) && this.Client.isAdmin());
    }

    public Object authenticate(JSONObject args) {
        if(args.has("username") && args.has("password")) {
            if(this.Logins.containsKey(args.getString("username")) && this.Logins.get(args.getString("username")).equals(args.getString("password"))) {
                this.Client.setAdmin(true);
                ChanServer.replaceClient(this.Client.getUniqueID(), this.Client);
                return "You are now an admin.";
            }
        }

        return "authenticate is not a valid action"; // hide this action by saying it doesn't exist
    }

    public Object archive(JSONObject args) {
        if(Configuration.get("archive.enabled").equals("false")) {
            String disabledMsg = ((String)Configuration.get("archive.disabled_msg")).trim();
            if(disabledMsg.length() > 0) {
                return "Archiving is currently disabled.";
            } else {
                return disabledMsg;
            }
        }


        if(!args.has("chan")) {
            return "chan is missing from args";
        }
        if(!args.has("board")) {
            return "board is missing from args";
        }
        if(!args.has("thread_id")) {
            return "thread_id is missing from args";
        }
        if(!args.has("user_ip")) {
            return "user_ip is missing from args";
        }
        String chan = args.getString("chan");
        String board = args.getString("board");
        Long threadID = args.getLong("thread_id");

        if(!this.AvailableChans.contains(chan)) {
            return chan + " is not a real chan";
        }

        if(Queue.size() > 100) {
            return "The queue is a bit overloaded right now. Please try again later.";
        }

        if(chan.equals("4chan")) {
            FourChanThread fourChanThread = new FourChanThread();

            if(Queue.get("/" + board + "/" + threadID) != null) {
                return "This thread is already in the queue.";
            }

            String verify = fourChanThread.verify(board, threadID);
            if(verify != null) { // if null, means no error message
                return verify;
            }

            Object uniqueID = "/" + board + "/" + threadID;
            QueueItem queueItem = new QueueItem();
            queueItem.setUniqueID(uniqueID);
            queueItem.setChan(chan);
            queueItem.addProperty("thread", fourChanThread);
            queueItem.addProperty("requested_by", args.get("user_ip"));
            Queue.add(uniqueID, queueItem);
        }

        return "Added to queue!";
    }

    public Object stopServer(JSONObject args) {
        Output.debug("The server is now going down. Reason: user command");
        ChanServer.getInstance().stop();

        return null;
    }

    public Object showClients(JSONObject args) {
        Client.writeToClient("Current clients connected:");
        for(ChanClient c : ChanServer.getInstance().getClients().values()) {
            Client.writeToClient(c.getIP() + " (" + c.getUniqueID() + ")" + ((c.getUniqueID() == Client.getUniqueID()) ? " (current)" : ""));
        }
        return null;
    }

    public Object dropClient(JSONObject args) {
        if(!args.has("id")) {
            return "id is required";
        }

        String id = args.getString("id");

        Hashtable<Object, ChanClient> Clients = ChanServer.getInstance().getClients();
        if(Clients.containsKey(id)) {
            Clients.get(id).close();
            return id + " has been closed.";
        } else {
            return id + " is not a valid client ID";
        }
    }

    public Object reload(JSONObject args) {
        Configuration.parseConfig();
        return "Configuration reloaded!";
    }
}
