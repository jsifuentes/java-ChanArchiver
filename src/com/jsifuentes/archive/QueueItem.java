package com.jsifuentes.archive;

import java.util.Hashtable;

/**
 * Created by Jacob on 11/18/2014.
 */
public class QueueItem {
    private Object UniqueID;
    private String Chan;
    private Hashtable<String, Object> Properties = new Hashtable<String, Object>();

    public Object getUniqueID() {
        return this.UniqueID;
    }

    public String getChan() {
        return this.Chan;
    }

    public Hashtable<String, Object> getProperties() {
        return this.Properties;
    }

    public void setChan(String chan) {
        this.Chan = chan;
    }

    public void setProperties(Hashtable<String, Object> properties) {
        this.Properties = properties;
    }

    public void setUniqueID(Object id) {
        this.UniqueID = id;
    }

    public void addProperty(String key, Object value) {
        this.Properties.put(key, value);
    }

    public Object getProperty(String key) {
        if(this.Properties.containsKey(key)) {
            return this.Properties.get(key);
        } else {
            return null;
        }
    }

    public void removeProperty(String key) {
        this.Properties.remove(key);
    }

}
