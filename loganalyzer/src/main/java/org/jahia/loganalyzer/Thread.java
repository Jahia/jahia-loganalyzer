package org.jahia.loganalyzer;

import java.util.List;

/**
 * Created by loom on 21.03.16.
 */
public class Thread {

    public boolean deamon;
    public List<String> stackTrace;
    public List<String> holdsLocks;
    public List<String> waitingOnLocks;
    private String threadId;
    private String threadNativeId;
    private String threadName;


}
