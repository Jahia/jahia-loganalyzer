package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.LogEntry;
import org.jahia.loganalyzer.LogEntryWriter;
import org.jahia.loganalyzer.ExceptionLogEntry;

import java.io.LineNumberReader;
import java.io.IOException;
import java.io.FileWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 11:08:16
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionLineAnalyzer extends CSVOutputLineAnalyzer {

    public ExceptionLineAnalyzer(String exceptionsOutputFileName, char csvOutputSeparatorChar) throws IOException {
        super(exceptionsOutputFileName, csvOutputSeparatorChar, new ExceptionLogEntry());
    }

    public boolean isForThisAnalyzer(String line) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void parseLine(String line, LineNumberReader lineNumberReader) {
    }

    public void finishPreviousState() {
    }
}
