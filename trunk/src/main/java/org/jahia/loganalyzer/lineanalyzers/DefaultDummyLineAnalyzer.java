package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.LogEntry;

import java.io.LineNumberReader;
import java.io.IOException;

/**
 * This is the default line analyzer, that will always be called as a last resort, just to ignore lines that
 * aren't picked up by any other line analyzer
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 11:16:57
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDummyLineAnalyzer implements LineAnalyzer {
    public boolean isForThisAnalyzer(String line) {
        return true;
    }

    public void parseLine(String line, LineNumberReader lineNumberReader) {
    }

    public void finishPreviousState() {
    }

    public void stop() throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
