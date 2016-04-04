package org.jahia.loganalyzer.analyzers.core;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.Deque;

/**
 * This is the default line analyzer, that will always be called as a last resort, just to ignore lines that
 * aren't picked up by any other line analyzer
 *
 * User: Serge Huber
 * Date: July 8th, 2008
 * Time: 11:16:57
 */
public class DefaultDummyLineAnalyzer implements LineAnalyzer {

    public String getKey() {
        return "dummy";
    }

    public boolean isForThisAnalyzer(String line, String nextLine, String nextNextLine, File file, String jvmIdentifier) {
        return true;
    }

    public Date parseLine(String line, String nextLine, String nextNextLine, Deque<String> contextLines, LineNumberReader lineNumberReader, Date lastValidDateParsed, File file, String jvmIdentifier) {
        return null;
    }

    public void finishPreviousState() {
    }

    public void stop() throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
