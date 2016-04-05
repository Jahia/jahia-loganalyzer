package org.jahia.loganalyzer.analyzers.core;

import java.io.IOException;
import java.util.Date;
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

    public String getKey() {
        return "composite";
    }


    public boolean isForThisAnalyzer(LineAnalyzerContext context) {
        for (LineAnalyzer lineAnalyzer : lineAnalyzers) {
            if (lineAnalyzer.isForThisAnalyzer(context)) {
                if (lineAnalyzer != currentlyActiveAnalyzer) {
                    if (currentlyActiveAnalyzer != null) {
                        currentlyActiveAnalyzer.finishPreviousState(context);
                    }
                    currentlyActiveAnalyzer = lineAnalyzer;
                }
                return true;
            }
        }
        return false;
    }

    public Date parseLine(LineAnalyzerContext context) throws IOException {
        if (isForThisAnalyzer(context)) {
            return currentlyActiveAnalyzer.parseLine(context);
        }
        return null;
    }

    public void finishPreviousState(LineAnalyzerContext context) {
        for (LineAnalyzer lineAnalyzer : lineAnalyzers) {
            lineAnalyzer.finishPreviousState(context);
        }
    }

    public void stop() throws IOException {
        for (LineAnalyzer lineAnalyzer : lineAnalyzers) {
            lineAnalyzer.stop();
        }
    }
}
