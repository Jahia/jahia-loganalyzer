package org.jahia.loganalyzer;

import java.util.List;
import java.util.ArrayList;
import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 16:14:37
 * To change this template use File | Settings | File Templates.
 */
public class ThreadDumpLogEntry implements LogEntry {

    private long number;
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
        buffer.append("number=");
        buffer.append(number);
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

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
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
        String[] result = new String[11];
        result[0] = Long.toString(number);
        result[1] = name;
        result[2] = type;
        result[3] = tid;
        result[4] = nid;
        result[5] = Integer.toString(priority);
        result[6] = state;
        result[7] = stateInfo;
        result[8] = stackTraceToString();
        result[9] = waitingOnLocksToString();
        result[10] = holdingLocksToString();
        return result;
    }

    public String[] getColumnKeys() {
        String[] result = new String[11];
        result[0] = "number";
        result[1] = "name";
        result[2] = "type";
        result[3] = "tid";
        result[4] = "nid";
        result[5] = "priority";
        result[6] = "state";
        result[7] = "stateInfo";
        result[8] = "stackTrace";
        result[9] = "waitingOnLocks";
        result[10] = "holdingLocks";
        return result;
    }
}
