package org.jahia.loganalyzer.analyzers.threaddumps;

import org.jahia.loganalyzer.BaseLogEntry;
import org.jahia.loganalyzer.stacktrace.StackTraceService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class represents a single thread inside a thread dump
 */
public class ThreadDumpThreadLogEntry extends BaseLogEntry {

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
    private List<String> stackTraceDefinitionIds = new ArrayList<>();
    private List<String> waitingOnLocks = new ArrayList<String>();
    private List<String> holdingLocks = new ArrayList<String>();
    private List<String> lockOwners = new ArrayList<String>();

    public ThreadDumpThreadLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("logLine=");
        buffer.append(getStartLineNumber());
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
        this.stackTraceDefinitionIds = StackTraceService.getInstance().getDefinitionIdsFromStackTrace(stackTrace);
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

    public List<String> toStringList(DateFormat dateFormat) {
        this.stackTraceDefinitionIds = StackTraceService.getInstance().getDefinitionIdsFromStackTrace(stackTrace);
        List<String> result = super.toStringList(dateFormat);
        result.add(Long.toString(threadDumpNumber));
        result.add(Long.toString(threadNumber));
        result.add(threadName);
        result.add(threadType);
        result.add(threadId);
        result.add(threadNativeId);
        result.add(Integer.toString(threadPriority));
        result.add(threadState);
        result.add(threadStateInfo);
        result.add(stackTraceToString());
        result.add(Integer.toString(stackTrace.size()));
        result.add(Integer.toString(stackTrace.hashCode()));
        result.add(stackTraceDefinitionIds.toString());
        result.add(Integer.toString(waitingOnLocks.size()));
        result.add(waitingOnLocksToString());
        result.add(Integer.toString(holdingLocks.size()));
        result.add(holdingLocksToString());
        result.add(lockOwnersToString());
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        this.stackTraceDefinitionIds = StackTraceService.getInstance().getDefinitionIdsFromStackTrace(stackTrace);
        LinkedHashMap<String, Object> result = super.getValues();
        result.put("threadDumpNumber", threadDumpNumber);
        result.put("threadNumber", threadNumber);
        result.put("threadName", threadName);
        result.put("threadType", threadType);
        result.put("threadId", threadId);
        result.put("threadNativeId", threadNativeId);
        result.put("threadPriority", threadPriority);
        result.put("threadState", threadState);
        result.put("threadStateInfo", threadStateInfo);
        result.put("stackTrace", stackTrace);
        result.put("stackTraceLength", stackTrace.size());
        result.put("stackTraceHashCode", stackTrace.hashCode());
        result.put("stackTraceDefinitionIds", stackTraceDefinitionIds);
        result.put("waitingOnLockCount", waitingOnLocks.size());
        result.put("waitingOnLocks", waitingOnLocks);
        result.put("heldLockCount", holdingLocks.size());
        result.put("holdingLocks", holdingLocks);
        result.put("lockOwners", lockOwners);
        return result;
    }

}
