package org.jahia.loganalyzer;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;

/**
 * Represents a log file that has already been processed.
 */
@XmlRootElement
public class ProcessedLogFile {

    long length;
    long lastModificationTime;
    String relativePath;
    long lastParsedTimestamp;

    public ProcessedLogFile() {
    }

    public ProcessedLogFile(File outputDirectory, File file) throws IOException {
        String relativePath = file.getCanonicalPath();
        if (relativePath.startsWith(outputDirectory.getCanonicalPath() + File.separator)) {
            relativePath = relativePath.substring(outputDirectory.getCanonicalPath().length() + File.separator.length());
        }
        if (relativePath.startsWith(outputDirectory.getCanonicalPath())) {
            relativePath = relativePath.substring(outputDirectory.getCanonicalPath().length());
        }
        this.relativePath = relativePath;
        this.length = file.length();
        this.lastModificationTime = file.lastModified();
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(long lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public long getLastParsedTimestamp() {
        return lastParsedTimestamp;
    }

    public void setLastParsedTimestamp(long lastParsedTimestamp) {
        this.lastParsedTimestamp = lastParsedTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessedLogFile that = (ProcessedLogFile) o;

        if (length != that.length) return false;
        if (lastModificationTime != that.lastModificationTime) return false;
        return relativePath.equals(that.relativePath);

    }

    @Override
    public int hashCode() {
        int result = (int) (length ^ (length >>> 32));
        result = 31 * result + (int) (lastModificationTime ^ (lastModificationTime >>> 32));
        result = 31 * result + relativePath.hashCode();
        return result;
    }

}
