package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class represents a single thread inside a thread dump
 */
public class ThreadDumpThreadLogEntry extends AbstractDetailsLogEntry {

    private long threadDumpNumber;
    private long threadNumber;
    private String threadName;
    private String threadType = "normal";
    private String threadId;
    private String threadNativeId;
    private int threadPriority;
    private String threadState;
    private String threadStateInfo;
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
        buffer.append(threadDumpNumber);
        buffer.append(",date=");
        buffer.append(getTimestamp());
        buffer.append(",threadNumber=");
        buffer.append(threadNumber);
        buffer.append(",name=");
        buffer.append(threadName);
        buffer.append(",type=");
        buffer.append(threadType);
        buffer.append(",tid=");
        buffer.append(threadId);
        buffer.append(",nid=");
        buffer.append(threadNativeId);
        buffer.append(",priority=");
        buffer.append(threadPriority);
        buffer.append(",state=");
        buffer.append(threadState);
        buffer.append(",state info=");
        buffer.append(threadStateInfo);
        return buffer.toString();
    }

    public long getThreadDumpNumber() {
        return threadDumpNumber;
    }

    public void setThreadDumpNumber(long threadDumpNumber) {
        this.threadDumpNumber = threadDumpNumber;
    }

    public long getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(long threadNumber) {
        this.threadNumber = threadNumber;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<String> stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getThreadType() {
        return threadType;
    }

    public void setThreadType(String threadType) {
        this.threadType = threadType;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getThreadNativeId() {
        return threadNativeId;
    }

    public void setThreadNativeId(String threadNativeId) {
        this.threadNativeId = threadNativeId;
    }

    public int getThreadPriority() {
        return threadPriority;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public String getThreadState() {
        return threadState;
    }

    public void setThreadState(String threadState) {
        this.threadState = threadState;
    }

    public String getThreadStateInfo() {
        return threadStateInfo;
    }

    public void setThreadStateInfo(String threadStateInfo) {
        this.threadStateInfo = threadStateInfo;
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
        result[1] = Long.toString(threadDumpNumber);
        if (getTimestamp() != null) {
            result[2] = dateFormat.format(getTimestamp());
        } else {
            result[2] = "";
        }
        result[3] = Long.toString(threadNumber);
        result[4] = threadName;
        result[5] = threadType;
        result[6] = threadId;
        result[7] = threadNativeId;
        result[8] = Integer.toString(threadPriority);
        result[9] = threadState;
        result[10] = threadStateInfo;
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
        result.put("lineNumber", getLineNumber());
        result.put("threadDumpNumber", threadDumpNumber);
        result.put("timestamp", getTimestamp());
        result.put("threadNumber", threadNumber);
        result.put("threadName", threadName);
        result.put("threadType", threadType);
        result.put("threadId", threadId);
        result.put("threadNativeId", threadNativeId);
        result.put("threadPriority", threadPriority);
        result.put("threadState", threadState);
        result.put("threadStateInfo", threadStateInfo);
        result.put("stackTrace", stackTrace);
        result.put("waitingOnLockCount", waitingOnLocks.size());
        result.put("waitingOnLocks", waitingOnLocks);
        result.put("heldLockCount", holdingLocks.size());
        result.put("holdingLocks", holdingLocks);
        result.put("lockOwners", lockOwners);
        return result;
    }

    public String[] getColumnKeys() {
        LinkedHashMap<String, Object> fakeValues = getValues();
        List<String> columnKeyList = new ArrayList<String>(fakeValues.keySet());
        return columnKeyList.toArray(new String[columnKeyList.size()]);
    }

}
