package com.jsifuentes.core;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

/**
 * Created by Jacob on 12/10/2014.
 */
public class Configuration {

    private static final String CONFIG_PATH = new File("").getAbsolutePath() + "/config.yaml";
    private static Map Config;

    public static void parseConfig() {
        YamlReader reader;
        try {
            reader = new YamlReader(new FileReader(CONFIG_PATH));
        } catch (FileNotFoundException e) {
            Output.error("Config file not found!! ");
            Output.error(e.getMessage());
            Output.kill();
            return;
        }

        Map config;
        try {
            config = (Map)reader.read();
            reader.close();
        } catch(Exception e) {
            Output.error("Error parsing config file!! ");
            Output.error(e.getMessage());
            Output.kill();
            return;
        }

        Config = config;
    }

    protected static Object getValue(String key) {
        Map c = Config;
        return getValue(key, c);
    }

    protected static Object getValue(String key, Map c) {
        if(key.contains(".")) {
            //we need to go deeper
            String t = key;
            String k;
            if(t.contains(".")) {
                k = t.substring(0, t.indexOf("."));
                if(!c.containsKey(k)) {
                    Output.error("Key " + k + " does not exist in Config");
                    Output.kill();
                    return null;
                }

                c = (Map)c.get(k);
                t = t.substring(t.indexOf(".") + 1);
                return getValue(t, c);
            }
        }

        return c.get(key);
    }

    public static Object get(String key) {
        if(Config == null) {
            parseConfig();
        }
        return getValue(key);
    }
}
