package org.jahia.loganalyzer.analyzers.jackrabbitbundlecache;

import org.jahia.loganalyzer.BaseLogEntry;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by loom on 06.04.16.
 */
public class JackrabbitBundleCacheLogEntry extends BaseLogEntry {

    private String cacheName;
    private Long elementCount;
    private Long usedMemoryKb;
    private Long maxMemoryKb;
    private Long accessCount;
    private Long missCount;

    public JackrabbitBundleCacheLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public void setElementCount(Long elementCount) {
        this.elementCount = elementCount;
    }

    public void setUsedMemoryKb(Long usedMemoryKb) {
        this.usedMemoryKb = usedMemoryKb;
    }

    public void setMaxMemoryKb(Long maxMemoryKb) {
        this.maxMemoryKb = maxMemoryKb;
    }

    public void setAccessCount(Long accessCount) {
        this.accessCount = accessCount;
    }

    public void setMissCount(Long missCount) {
        this.missCount = missCount;
    }

    @Override
    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = super.toStringList(dateFormat);
        result.add(cacheName);
        result.add(Long.toString(elementCount));
        result.add(Long.toString(usedMemoryKb));
        result.add(Long.toString(maxMemoryKb));
        result.add(Long.toString(accessCount));
        result.add(Long.toString(missCount));
        return result;
    }

    @Override
    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = super.getValues();
        result.put("cacheName", cacheName);
        result.put("elementCount", elementCount);
        result.put("usedMemoryKb", usedMemoryKb);
        result.put("maxMemoryKb", maxMemoryKb);
        result.put("accessCount", accessCount);
        result.put("missCount", missCount);
        return result;
    }

}
