package com.jsifuentes.archive;

import com.jsifuentes.core.Output;
import java.util.Hashtable;
import java.util.UUID;

/**
 * Created by Jacob on 11/18/2014.
 */
public class QueueManager implements Runnable {
    private Object lastQueueItemIndex;
    public static Hashtable<String, Thread> threadList = new Hashtable<String, Thread>();

    public static void threadFinished(String threadIndex, String queueIndex) {
        Queue.remove(queueIndex);
        threadList.remove(threadIndex);
        Queue.markFinished(queueIndex);

        Output.debug("Thread " + threadIndex + " has finished.");
    }

    public void run() {
        this.listen();
    }

    public void listen() {
        while(true) {
            String firstKey = Queue.firstAvailableKey();
            if(this.lastQueueItemIndex != firstKey && firstKey != null) {
                // new item!
                if(threadList.size() < 10) {
                    String uniqueIndex = UUID.randomUUID().toString();
                    Queue.markInUse(firstKey);
                    Thread newSpawn = new Thread(new ArchiveWorker(Queue.get(firstKey), uniqueIndex, firstKey));
                    newSpawn.start();

                    threadList.put(uniqueIndex, newSpawn);

                    Output.debug("Spawned new thread! " + threadList.size() + " active threads.");

                    this.lastQueueItemIndex = firstKey;
                }
            }

            try {
                Thread.currentThread().sleep(1000); // sleep for 1 second
            } catch(InterruptedException inExc) {
                Output.error("Failed to put thread to sleep: " + inExc.getMessage());
            }
        }
    }
}
