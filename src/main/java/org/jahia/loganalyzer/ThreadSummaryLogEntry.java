package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 9 juil. 2008
 * Time: 10:12:18
 * To change this template use File | Settings | File Templates.
 */
public class ThreadSummaryLogEntry implements LogEntry {

    private long dumpNumber;
    private long threadTotal;
    private Date threadDumpDate;
    private List<ThreadDumpLogEntry> threadDumpLogEntries = new ArrayList<ThreadDumpLogEntry>();
    private Map<String,String> threadNames = new TreeMap<String,String>();
    private String newThreadsList;
    private String deadThreadsList;

    public ThreadSummaryLogEntry() {
        
    }

    public long getDumpNumber() {
        return dumpNumber;
    }

    public void setDumpNumber(long dumpNumber) {
        this.dumpNumber = dumpNumber;
    }

    public long getThreadTotal() {
        return threadTotal;
    }

    public void setThreadTotal(long threadTotal) {
        this.threadTotal = threadTotal;
    }

    public Date getThreadDumpDate() {
        return threadDumpDate;
    }

    public void setThreadDumpDate(Date threadDumpDate) {
        this.threadDumpDate = threadDumpDate;
    }

    public List<ThreadDumpLogEntry> getThreadDumpLogEntries() {
        return threadDumpLogEntries;
    }

    public void setThreadDumpLogEntries(List<ThreadDumpLogEntry> threadDumpLogEntries) {
        this.threadDumpLogEntries = threadDumpLogEntries;
    }

    public Map<String, String> getThreadNames() {
        return threadNames;
    }

    public void setThreadNames(Map<String, String> threadNames) {
        this.threadNames = threadNames;
    }

    public void computeDifferences(ThreadSummaryLogEntry lastSummaryLogEntry) {
        Map<String,String> newThreads;
        Map<String,String> deadThreads;

        newThreads = new TreeMap(this.threadNames);
        if (lastSummaryLogEntry != null) {
            deadThreads = new TreeMap<String,String>(lastSummaryLogEntry.getThreadNames());
        } else {
            newThreadsList="all";
            deadThreadsList="";
            return;
        }

        Set<String> lastThreadNamesIDSet = deadThreads.keySet();
        for (String lastThreadID : lastThreadNamesIDSet) {
            newThreads.remove(lastThreadID);
        }

        Set<String> newThreadNamesIDSet = this.threadNames.keySet();
        for (String newThreadID : newThreadNamesIDSet) {
            deadThreads.remove(newThreadID);
        }

        // now let's generate the string containing the names of the new threads
        StringBuffer newThreadsBuffer = new StringBuffer();
        for (Map.Entry<String,String> currentEntry : newThreads.entrySet()) {
            newThreadsBuffer.append(currentEntry.getValue());
            newThreadsBuffer.append(",");
        }
        newThreadsList = newThreadsBuffer.toString();

        // now let's generate the string containing the names of the dead threads
        StringBuffer deadThreadsBuffer = new StringBuffer();
        for (Map.Entry<String,String> currentEntry : deadThreads.entrySet()) {
            deadThreadsBuffer.append(currentEntry.getValue());
            deadThreadsBuffer.append(",");
        }
        deadThreadsList = deadThreadsBuffer.toString();
    }

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[5];
        result[0] = Long.toString(dumpNumber);
        result[1] = Long.toString(threadDumpLogEntries.size());
        if (threadDumpDate != null) {
            result[2] = dateFormat.format(threadDumpDate);
        } else {
            result[2] = "";
        }
        result[3] = newThreadsList;
        result[4] = deadThreadsList;
        return result;
    }

    public String[] getColumnKeys() {
        String[] result = new String[5];
        result[0] = "summaryDumpNumber";
        result[1] = "summaryThreadTotal";
        result[2] = "summaryThreadDumpDate";
        result[3] = "summaryNewThreads";
        result[4] = "summaryDeadThreads";
        return result;
    }
}
