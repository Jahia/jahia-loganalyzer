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
public class ThreadDumpLogEntry implements LogEntry {

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

    public ThreadDumpLogEntry() {
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("dump=");
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

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[15];
        result[0] = Long.toString(dumpNumber);
        if (dumpDate != null) {
            result[1] = dateFormat.format(dumpDate);
        } else {
            result[1] = "";
        }
        result[2] = Long.toString(threadNumber);
        result[3] = name;
        result[4] = type;
        result[5] = tid;
        result[6] = nid;
        result[7] = Integer.toString(priority);
        result[8] = state;
        result[9] = stateInfo;
        result[10] = stackTraceToString();
        result[11] = Integer.toString(waitingOnLocks.size());
        result[12] = waitingOnLocksToString();
        result[13] = Integer.toString(holdingLocks.size());
        result[14] = holdingLocksToString();
        return result;
    }

    public String[] getColumnKeys() {
        String[] result = new String[15];
        result[0] = "dumpNumber";
        result[1] = "dumpDate";
        result[2] = "threadNumber";
        result[3] = "name";
        result[4] = "type";
        result[5] = "tid";
        result[6] = "nid";
        result[7] = "priority";
        result[8] = "state";
        result[9] = "stateInfo";
        result[10] = "stackTrace";
        result[11] = "totalWaiting";
        result[12] = "waitingOnLocks";
        result[13] = "totalHeld";
        result[14] = "holdingLocks";
        return result;
    }
}
