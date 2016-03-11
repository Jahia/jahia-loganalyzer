package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.*;

/**
 * This class represents a full Thread dump
 */
public class ThreadDumpLogEntry implements LogEntry {

    private long dumpNumber;
    private long threadTotal;
    private Date threadDumpDate;
    private List<ThreadDumpThreadLogEntry> threadDumpDetailsLogEntries = new ArrayList<ThreadDumpThreadLogEntry>();
    private Map<String,String> threadNames = new TreeMap<String,String>();
    private String newThreadsList;
    private String deadThreadsList;

    public ThreadDumpLogEntry() {
        
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

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[5];
        result[0] = Long.toString(dumpNumber);
        result[1] = Long.toString(threadDumpDetailsLogEntries.size());
        if (threadDumpDate != null) {
            result[2] = dateFormat.format(threadDumpDate);
        } else {
            result[2] = "";
        }
        result[3] = newThreadsList;
        result[4] = deadThreadsList;
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("threaddump.summary.dumpNumber", dumpNumber);
        result.put("threaddump.summary.threadTotal", threadDumpDetailsLogEntries.size());
        result.put("threaddump.summary.date", threadDumpDate);
        result.put("threaddump.summary.newThreads", newThreadsList);
        result.put("threaddump.summary.deadThreads", deadThreadsList);
        return result;
    }

    public String[] getColumnKeys() {
        LinkedHashMap<String, Object> fakeValues = getValues();
        List<String> columnKeyList = new ArrayList<String>(fakeValues.keySet());
        return columnKeyList.toArray(new String[columnKeyList.size()]);
    }

}
