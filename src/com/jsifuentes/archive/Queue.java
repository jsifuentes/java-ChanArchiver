package com.jsifuentes.archive;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Jacob on 11/18/2014.
 */
public class Queue {
    public static Hashtable<Object, QueueItem> queue = new Hashtable<Object, QueueItem>() {{
//        put("abcdef", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdea", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdeb", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdec", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcded", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdee", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdef", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdeg", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdeh", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdei", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdej", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdek", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdel", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcdem", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
//        put("abcden", new QueueItem() {{ setChan("4chan"); addProperty("board", "hello"); addProperty("thread_id", "12345646"); }});
    }};
    public static ArrayList<Object> inUse = new ArrayList<Object>();

    public static void add(Object index, QueueItem item) {
        queue.put(index, item);
    }

    public static void remove(Object index) {
        if(queue.containsKey(index)) {
            queue.remove(index);
        }
    }

    public static QueueItem get(Object index) {
        if (queue.containsKey(index)) {
            return queue.get(index);
        }
        return null;
    }

    public static int size() {
        return queue.size();
    }

    public static void markInUse(Object index) {
        inUse.add(index);
    }

    public static void markFinished(Object index) {
        int i = inUse.indexOf(index);
        if(i >= 0) {
            inUse.remove(i);
        }
    }

    public static String firstAvailableKey() {
        for (Object key : queue.keySet()) {
            if(!inUse.contains(key)) {
                return key.toString();
            }
        }

        return null;
    }
}
