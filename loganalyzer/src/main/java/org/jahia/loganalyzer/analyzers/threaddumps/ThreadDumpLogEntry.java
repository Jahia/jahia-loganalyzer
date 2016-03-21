package org.jahia.loganalyzer.analyzers.threaddumps;

import org.jahia.loganalyzer.BaseLogEntry;

import java.text.DateFormat;
import java.util.*;

/**
 * This class represents a full Thread dump
 */
public class ThreadDumpLogEntry extends BaseLogEntry {

    private long threadDumpNumber;
    private long threadTotal;
    private List<ThreadDumpThreadLogEntry> threadDumpDetailsLogEntries = new ArrayList<ThreadDumpThreadLogEntry>();
    private Map<String,String> threadNames = new TreeMap<String,String>();
    private String newThreadsList;
    private String deadThreadsList;
    private List<String> locks = new ArrayList<>();

    private List<ThreadInfo> stuckThreads = new ArrayList<ThreadInfo>();

    public ThreadDumpLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
    }


    public long getThreadDumpNumber() {
        return threadDumpNumber;
    }

    public void setThreadDumpNumber(long threadDumpNumber) {
        this.threadDumpNumber = threadDumpNumber;
    }

    public long getThreadTotal() {
        return threadTotal;
    }

    public void setThreadTotal(long threadTotal) {
        this.threadTotal = threadTotal;
    }

    public List<ThreadDumpThreadLogEntry> getThreadDumpLogEntries() {
        return threadDumpDetailsLogEntries;
    }

    public void setThreadDumpLogEntries(List<ThreadDumpThreadLogEntry> threadDumpDetailsLogEntries) {
        this.threadDumpDetailsLogEntries = threadDumpDetailsLogEntries;
    }

    public Map<String, String> getThreadNames() {
        return threadNames;
    }

    public void setThreadNames(Map<String, String> threadNames) {
        this.threadNames = threadNames;
    }

    public List<ThreadInfo> getStuckThreads() {
        return stuckThreads;
    }

    public void setStuckThreads(List<ThreadInfo> stuckThreads) {
        this.stuckThreads = stuckThreads;
    }

    public void computeDifferences(ThreadDumpLogEntry lastSummaryLogEntry) {
        Map<String,String> newThreads;
        Map<String,String> deadThreads;

        newThreads = new TreeMap<String,String>(this.threadNames);
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

    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = super.toStringList(dateFormat);
        result.add(Long.toString(threadDumpNumber));
        result.add(Long.toString(threadDumpDetailsLogEntries.size()));
        result.add(newThreadsList);
        result.add(deadThreadsList);
        result.add("Not implemented.");
        result.add(stuckThreads.toString());
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("threadDumpNumber", threadDumpNumber);
        result.put("threadCount", threadDumpDetailsLogEntries.size());
        result.put("timestamp", getTimestamp());
        result.put("newThreadsInDump", newThreadsList);
        result.put("deadThreadsInDump", deadThreadsList);
        result.put("threadDumps", threadDumpDetailsLogEntries);
        result.put("stuckThreads", stuckThreads);
        return result;
    }

}
