package org.jahia.loganalyzer.analyzers.garbagecollection;

import org.jahia.loganalyzer.BaseLogEntry;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Represents a single garbage collection log entry
 */
public class GarbageCollectionLogEntry extends BaseLogEntry {

    private String gcType;
    private String gcMessage;
    private long fromSize;
    private long toSize;
    private long heapSize;
    private double gcTimeInSeconds;

    public GarbageCollectionLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
    }

    public String getGcType() {
        return gcType;
    }

    public void setGcType(String gcType) {
        this.gcType = gcType;
    }

    public String getGcMessage() {
        return gcMessage;
    }

    public void setGcMessage(String gcMessage) {
        this.gcMessage = gcMessage;
    }

    public long getFromSize() {
        return fromSize;
    }

    public void setFromSize(long fromSize) {
        this.fromSize = fromSize;
    }

    public long getToSize() {
        return toSize;
    }

    public void setToSize(long toSize) {
        this.toSize = toSize;
    }

    public long getHeapSize() {
        return heapSize;
    }

    public void setHeapSize(long heapSize) {
        this.heapSize = heapSize;
    }

    public double getGcTimeInSeconds() {
        return gcTimeInSeconds;
    }

    public void setGcTimeInSeconds(double gcTimeInSeconds) {
        this.gcTimeInSeconds = gcTimeInSeconds;
    }

    @Override
    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = super.getValues();
        result.put("gcType", gcType);
        result.put("gcMessage", gcMessage);
        result.put("fromSize", fromSize);
        result.put("toSize", toSize);
        result.put("heapSize", heapSize);
        result.put("gcTimeInSeconds", gcTimeInSeconds);
        return result;
    }

    @Override
    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = super.toStringList(dateFormat);
        result.add(gcType);
        result.add(gcMessage);
        result.add(Long.toString(fromSize));
        result.add(Long.toString(toSize));
        result.add(Long.toString(heapSize));
        result.add(Double.toString(gcTimeInSeconds));
        return result;
    }
}
