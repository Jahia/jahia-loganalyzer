package org.jahia.loganalyzer.api;

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
