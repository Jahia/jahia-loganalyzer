package org.jahia.loganalyzer.analyzers.threaddumps;

/*
 * #%L
 * Jahia Log Analyzer
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2007 - 2016 Jahia
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Date;
import java.util.List;

/**
 * Represents a thread parseed from a Thread Dump
 */
public class ThreadInfo {

    public boolean deamon;
    private String threadId;
    private String threadNativeId;
    private String threadName;

    private List<String> stackTrace;
    private List<String> holdsLocks;
    private List<String> waitingOnLocks;

    private Date lastModificationTime = null;
    private long stuckTime = 0;

    public ThreadInfo(String threadId) {
        this.threadId = threadId;
    }

    public boolean isDeamon() {
        return deamon;
    }

    public void setDeamon(boolean deamon) {
        this.deamon = deamon;
    }

    public String getThreadNativeId() {
        return threadNativeId;
    }

    public void setThreadNativeId(String threadNativeId) {
        this.threadNativeId = threadNativeId;
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

    public List<String> getHoldsLocks() {
        return holdsLocks;
    }

    public void setHoldsLocks(List<String> holdsLocks) {
        this.holdsLocks = holdsLocks;
    }

    public List<String> getWaitingOnLocks() {
        return waitingOnLocks;
    }

    public void setWaitingOnLocks(List<String> waitingOnLocks) {
        this.waitingOnLocks = waitingOnLocks;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public Date getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(Date lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public long getStuckTime() {
        return stuckTime;
    }

    public void setStuckTime(long stuckTime) {
        this.stuckTime = stuckTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadInfo that = (ThreadInfo) o;

        if (deamon != that.deamon) return false;
        if (!threadId.equals(that.threadId)) return false;
        if (!threadName.equals(that.threadName)) return false;
        if (!stackTrace.equals(that.stackTrace)) return false;
        if (holdsLocks != null ? !holdsLocks.equals(that.holdsLocks) : that.holdsLocks != null) return false;
        return waitingOnLocks != null ? waitingOnLocks.equals(that.waitingOnLocks) : that.waitingOnLocks == null;

    }

    @Override
    public int hashCode() {
        int result = (deamon ? 1 : 0);
        result = 31 * result + threadId.hashCode();
        result = 31 * result + threadName.hashCode();
        result = 31 * result + stackTrace.hashCode();
        result = 31 * result + (holdsLocks != null ? holdsLocks.hashCode() : 0);
        result = 31 * result + (waitingOnLocks != null ? waitingOnLocks.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ThreadInfo{");
        sb.append("deamon=").append(deamon);
        sb.append(", threadId='").append(threadId).append('\'');
        sb.append(", threadNativeId='").append(threadNativeId).append('\'');
        sb.append(", threadName='").append(threadName).append('\'');
        sb.append(", stackTrace=").append(stackTrace);
        sb.append(", holdsLocks=").append(holdsLocks);
        sb.append(", waitingOnLocks=").append(waitingOnLocks);
        sb.append(", lastModificationTime=").append(lastModificationTime);
        sb.append(", stuckTime=").append(stuckTime);
        sb.append('}');
        return sb.toString();
    }
}
