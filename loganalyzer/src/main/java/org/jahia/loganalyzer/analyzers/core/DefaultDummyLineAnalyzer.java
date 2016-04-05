package org.jahia.loganalyzer.analyzers.core;

import java.io.IOException;
import java.util.Date;

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

    public boolean isForThisAnalyzer(LineAnalyzerContext context) {
        return true;
    }

    public Date parseLine(LineAnalyzerContext context) {
        return null;
    }

    public void finishPreviousState(LineAnalyzerContext context) {
    }

    public void stop() throws IOException {
    }
}
