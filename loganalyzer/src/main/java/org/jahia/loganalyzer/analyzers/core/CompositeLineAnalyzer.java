package org.jahia.loganalyzer.analyzers.core;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.Deque;
import java.util.List;

/**
 * A line analyzer that delegates to an array of line analyzers
 * User: Serge Huber
 * Date: July 8th, 2008
 * Time: 10:31:22
 */
public class CompositeLineAnalyzer implements LineAnalyzer {

    List<LineAnalyzer> lineAnalyzers;
    LineAnalyzer currentlyActiveAnalyzer = null;

    public CompositeLineAnalyzer(List<LineAnalyzer> lineAnalyzers) {
        this.lineAnalyzers = lineAnalyzers;        
    }

    public boolean isForThisAnalyzer(String line, String nextLine, String nextNextLine, File file, String jvmIdentifier) {
        for (LineAnalyzer lineAnalyzer : lineAnalyzers) {
            if (lineAnalyzer.isForThisAnalyzer(line, nextLine, nextNextLine, file, jvmIdentifier)) {
                if (lineAnalyzer != currentlyActiveAnalyzer) {
                    if (currentlyActiveAnalyzer != null) {
                        currentlyActiveAnalyzer.finishPreviousState();
                    }
                    currentlyActiveAnalyzer = lineAnalyzer;
                }
                return true;
            }
        }
        return false;
    }

    public Date parseLine(String line, String nextLine, String nextNextLine, Deque<String> contextLines, LineNumberReader lineNumberReader, Date lastValidDateParsed, File file, String jvmIdentifier) throws IOException {
        if (isForThisAnalyzer(line, nextLine, nextNextLine, file, jvmIdentifier)) {
            return currentlyActiveAnalyzer.parseLine(line, nextLine, nextNextLine, contextLines, lineNumberReader, lastValidDateParsed, file, jvmIdentifier);
        }
        return null;
    }

    public void finishPreviousState() {
        for (LineAnalyzer lineAnalyzer : lineAnalyzers) {
            lineAnalyzer.finishPreviousState();
        }
    }

    public void stop() throws IOException {
        for (LineAnalyzer lineAnalyzer : lineAnalyzers) {
            lineAnalyzer.stop();
        }
    }
}
