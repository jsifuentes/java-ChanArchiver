package com.jsifuentes.archive;

import com.jsifuentes.core.Output;
import com.jsifuentes.fourchan.FourChanThread;

/**
 * Created by Jacob on 11/18/2014.
 */
public class ArchiveWorker implements Runnable {
    private QueueItem Subject;
    private String ThreadIndex;
    private String QueueIndex;
    private String RequestedByIP;

    ArchiveWorker(QueueItem subject, String threadIndex, String queueIndex) {
        this.Subject = subject;
        this.ThreadIndex = threadIndex;
        this.QueueIndex = queueIndex;
        this.RequestedByIP = (String)subject.getProperty("requested_by");
    }

    public void run() {
        if (this.Subject.getChan().equals("4chan")) {
            this.FourChan((FourChanThread)this.Subject.getProperty("thread"));
        }

        // Finished. Report back to QueueManager
        QueueManager.threadFinished(this.ThreadIndex, this.QueueIndex);
    }

    public void FourChan(FourChanThread thread) {
        try {
            Output.debug("Archiving /" + thread.getBoard() + "/thread/" + thread.getThreadID());
            if(thread.archive(RequestedByIP)) {
                Output.debug("/" + thread.getBoard() + "/thread/" + thread.getThreadID() + " has finished archiving.");
            } else {
                Output.error("Archiving /" + thread.getBoard() + "/thread/" + thread.getThreadID() + " has failed!");
            }
        } catch(Exception exc) {
            Output.error("Archive error: " + exc.getMessage());
        }
    }
}
