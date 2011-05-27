package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.LogEntry;

import java.io.LineNumberReader;
import java.io.IOException;
import java.util.Date;

/**
 * This is the default line analyzer, that will always be called as a last resort, just to ignore lines that
 * aren't picked up by any other line analyzer
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 11:16:57
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDummyLineAnalyzer implements LineAnalyzer {
    public boolean isForThisAnalyzer(String line, String nextLine, String nextNextLine) {
        return true;
    }

    public Date parseLine(String line, String nextLine, String nextNextLine, LineNumberReader lineNumberReader, Date lastValidDateParsed) {
        return null;
    }

    public void finishPreviousState() {
    }

    public void stop() throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
