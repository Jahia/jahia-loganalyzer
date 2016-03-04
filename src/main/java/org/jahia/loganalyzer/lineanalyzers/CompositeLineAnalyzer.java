package org.jahia.loganalyzer.lineanalyzers;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.Deque;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 10:31:22
 * To change this template use File | Settings | File Templates.
 */
public class CompositeLineAnalyzer implements LineAnalyzer {

    List<LineAnalyzer> lineAnalyzers;
    LineAnalyzer currentlyActiveAnalyzer = null;

    public CompositeLineAnalyzer(List<LineAnalyzer> lineAnalyzers) {
        this.lineAnalyzers = lineAnalyzers;        
    }

    public boolean isForThisAnalyzer(String line, String nextLine, String nextNextLine) {
        for (LineAnalyzer lineAnalyzer : lineAnalyzers) {
            if (lineAnalyzer.isForThisAnalyzer(line, nextLine, nextNextLine)) {
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

    public Date parseLine(String line, String nextLine, String nextNextLine, Deque<String> contextLines, LineNumberReader lineNumberReader, Date lastValidDateParsed) throws IOException {
        if (isForThisAnalyzer(line, nextLine, nextNextLine)) {
            return currentlyActiveAnalyzer.parseLine(line, nextLine, nextNextLine, contextLines, lineNumberReader, lastValidDateParsed);
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
