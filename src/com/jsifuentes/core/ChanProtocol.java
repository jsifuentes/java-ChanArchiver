package com.jsifuentes.core;

import com.jsifuentes.archive.Queue;
import com.jsifuentes.archive.QueueItem;
import com.jsifuentes.fourchan.FourChanThread;
import org.json.JSONObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jacob on 11/18/2014.
 */
public class ChanProtocol {

    public Object interpret(String input, ChanClient client) {
        JSONObject command;
        try {
            command = new JSONObject(input);

            if(!command.has("action")) {
                return "action is missing from payload";
            }
            if(!command.has("args")) {
                return "args is missing from payload";
            }

            String action = command.getString("action");
            JSONObject args = command.getJSONObject("args");

            Actions actions = new Actions(client);

            if(actions.isValidAction(action)) {
                try {
                    Class parameters[] = {JSONObject.class};
                    Method method = actions.getClass().getMethod(action, parameters);
                    Object arguments[] = {args};
                    return method.invoke(actions, arguments);
                } catch(Exception exc) {
                    return "Something went wrong when attempting to call action. " + exc.getMessage();
                }
            } else {
                return action + " is not a valid action";
            }
        } catch(Exception exc) {
            return "Not valid payload";
        }
    }
}
