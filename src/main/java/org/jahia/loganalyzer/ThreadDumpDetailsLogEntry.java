package org.jahia.loganalyzer;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 16:14:37
 * To change this template use File | Settings | File Templates.
 */
public class ThreadDumpDetailsLogEntry extends AbstractDetailsLogEntry {

    private long dumpNumber;
    private Date dumpDate;
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

    public ThreadDumpDetailsLogEntry() {
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("logLine=");
        buffer.append(getLineNumber());
        buffer.append(",dump=");
        buffer.append(dumpNumber);
        buffer.append(",date=");
        buffer.append(dumpDate);
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

    public Date getDumpDate() {
        return dumpDate;
    }

    public void setDumpDate(Date dumpDate) {
        this.dumpDate = dumpDate;
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
        if (dumpDate != null) {
            result[2] = dateFormat.format(dumpDate);
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

    public String[] getColumnKeys() {
        String[] result = new String[17];
        result[0] = "threaddump.details.logLine";
        result[1] = "threaddump.details.dumpNumber";
        result[2] = "threaddump.details.dumpDate";
        result[3] = "threaddump.details.threadNumber";
        result[4] = "threaddump.details.name";
        result[5] = "threaddump.details.type";
        result[6] = "threaddump.details.tid";
        result[7] = "threaddump.details.nid";
        result[8] = "threaddump.details.priority";
        result[9] = "threaddump.details.state";
        result[10] = "threaddump.details.stateInfo";
        result[11] = "threaddump.details.stackTrace";
        result[12] = "threaddump.details.totalWaiting";
        result[13] = "threaddump.details.waitingOnLocks";
        result[14] = "threaddump.details.totalHeld";
        result[15] = "threaddump.details.holdingLocks";
        result[16] = "threaddump.details.lockOwners";
        return result;
    }
}
