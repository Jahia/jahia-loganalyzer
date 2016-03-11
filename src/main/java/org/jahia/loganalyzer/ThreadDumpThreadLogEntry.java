package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class represents a single thread inside a thread dump
 */
public class ThreadDumpThreadLogEntry extends AbstractDetailsLogEntry {

    private long dumpNumber;
    private long threadNumber;
    private String name;
    private String type = "normal";
    private String tid;
    private String nid;
    private int priority;
    private String state;
    private String stateInfo;
    private List<String> stackTrace = new ArrayList<String>();
    private List<String> waitingOnLocks = new ArrayList<String>();
    private List<String> holdingLocks = new ArrayList<String>();
    private List<String> lockOwners = new ArrayList<String>();

    public ThreadDumpThreadLogEntry() {
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("logLine=");
        buffer.append(getLineNumber());
        buffer.append(",dump=");
        buffer.append(dumpNumber);
        buffer.append(",date=");
        buffer.append(getTimestamp());
        buffer.append(",threadNumber=");
        buffer.append(threadNumber);
        buffer.append(",name=");
        buffer.append(name);
        buffer.append(",type=");
        buffer.append(type);
        buffer.append(",tid=");
        buffer.append(tid);
        buffer.append(",nid=");
        buffer.append(nid);
        buffer.append(",priority=");
        buffer.append(priority);
        buffer.append(",state=");
        buffer.append(state);
        buffer.append(",state info=");
        buffer.append(stateInfo);
        return buffer.toString();
    }

    public long getDumpNumber() {
        return dumpNumber;
    }

    public void setDumpNumber(long dumpNumber) {
        this.dumpNumber = dumpNumber;
    }

    public long getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(long threadNumber) {
        this.threadNumber = threadNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<String> stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public String stackTraceToString() {
        StringBuffer result = new StringBuffer();
        for (String stackLine : stackTrace) {
            result.append(stackLine);
            result.append("\n");
        }
        return result.toString();
    }

    public String waitingOnLocksToString() {
        StringBuffer result = new StringBuffer();
        for (String stackLine : waitingOnLocks) {
            result.append(stackLine);
            result.append("\n");
        }
        return result.toString();
    }

    public String holdingLocksToString() {
        StringBuffer result = new StringBuffer();
        for (String stackLine : holdingLocks) {
            result.append(stackLine);
            result.append("\n");
        }
        return result.toString();
    }

    public String lockOwnersToString() {
        StringBuffer result = new StringBuffer();
        for (String stackLine : lockOwners) {
            result.append(stackLine);
            result.append("\n");
        }
        return result.toString();
    }

    public List<String> getWaitingOnLocks() {
        return waitingOnLocks;
    }

    public void setWaitingOnLocks(List<String> waitingOnLocks) {
        this.waitingOnLocks = waitingOnLocks;
    }

    public List<String> getHoldingLocks() {
        return holdingLocks;
    }

    public void setHoldingLocks(List<String> holdingLocks) {
        this.holdingLocks = holdingLocks;
    }

    public List<String> getLockOwners() {
        return lockOwners;
    }

    public void setLockOwners(List<String> lockOwners) {
        this.lockOwners = lockOwners;
    }

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[17];
        result[0] = Long.toString(getLineNumber());
        result[1] = Long.toString(dumpNumber);
        if (getTimestamp() != null) {
            result[2] = dateFormat.format(getTimestamp());
        } else {
            result[2] = "";
        }
        result[3] = Long.toString(threadNumber);
        result[4] = name;
        result[5] = type;
        result[6] = tid;
        result[7] = nid;
        result[8] = Integer.toString(priority);
        result[9] = state;
        result[10] = stateInfo;
        result[11] = stackTraceToString();
        result[12] = Integer.toString(waitingOnLocks.size());
        result[13] = waitingOnLocksToString();
        result[14] = Integer.toString(holdingLocks.size());
        result[15] = holdingLocksToString();
        result[16] = lockOwnersToString();
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("threaddump.details.logLine", getLineNumber());
        result.put("threaddump.details.dumpNumber", dumpNumber);
        result.put("threaddump.details.dumpDate", getTimestamp());
        result.put("threaddump.details.threadNumber", threadNumber);
        result.put("threaddump.details.name", name);
        result.put("threaddump.details.type", type);
        result.put("threaddump.details.tid", tid);
        result.put("threaddump.details.nid", nid);
        result.put("threaddump.details.priority", priority);
        result.put("threaddump.details.state", state);
        result.put("threaddump.details.stateInfo", stateInfo);
        result.put("threaddump.details.stackTrace", stackTrace);
        result.put("threaddump.details.totalWaiting", waitingOnLocks.size());
        result.put("threaddump.details.waitingOnLocks", waitingOnLocks);
        result.put("threaddump.details.totalHeld", holdingLocks.size());
        result.put("threaddump.details.holdingLocks", holdingLocks);
        result.put("threaddump.details.lockOwners", lockOwners);
        return result;
    }

    public String[] getColumnKeys() {
        LinkedHashMap<String, Object> fakeValues = getValues();
        List<String> columnKeyList = new ArrayList<String>(fakeValues.keySet());
        return columnKeyList.toArray(new String[columnKeyList.size()]);
    }

}
