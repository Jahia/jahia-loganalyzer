package org.jahia.loganalyzer.analyzers.requestload;

import org.jahia.loganalyzer.BaseLogEntry;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A single request load average log line entry
 */
public class RequestLoadLogEntry extends BaseLogEntry {

    private Double oneMinuteLoad;
    private Double fiveMinuteLoad;
    private Double fifteenMinuteLoad;

    public RequestLoadLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
    }

    public Double getOneMinuteLoad() {
        return oneMinuteLoad;
    }

    public void setOneMinuteLoad(Double oneMinuteLoad) {
        this.oneMinuteLoad = oneMinuteLoad;
    }

    public Double getFiveMinuteLoad() {
        return fiveMinuteLoad;
    }

    public void setFiveMinuteLoad(Double fiveMinuteLoad) {
        this.fiveMinuteLoad = fiveMinuteLoad;
    }

    public Double getFifteenMinuteLoad() {
        return fifteenMinuteLoad;
    }

    public void setFifteenMinuteLoad(Double fifteenMinuteLoad) {
        this.fifteenMinuteLoad = fifteenMinuteLoad;
    }

    @Override
    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = super.toStringList(dateFormat);
        result.add(Double.toString(oneMinuteLoad));
        result.add(Double.toString(fiveMinuteLoad));
        result.add(Double.toString(fifteenMinuteLoad));
        return result;
    }

    @Override
    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = super.getValues();
        result.put("oneMinuteLoad", oneMinuteLoad);
        result.put("fiveMinuteLoad", fiveMinuteLoad);
        result.put("fifteenMinuteLoad", fifteenMinuteLoad);
        return result;
    }
}
