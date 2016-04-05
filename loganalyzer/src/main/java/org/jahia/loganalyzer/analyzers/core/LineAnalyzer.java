package org.jahia.loganalyzer.analyzers.core;

import java.io.IOException;
import java.util.Date;

/**
 * Common interface for all line analyzer implementations
 * User: Serge Huber
 * Date: August 22th, 2007
 * Time: 12:51:57
 */
public interface LineAnalyzer {

    /**
     * Get a unique key to identify this line analyzer
     *
     * @return
     */
    String getKey();

    /**
     * Checks whether the current line is going to be sent to a specific analyzer.
     * @return
     */
    boolean isForThisAnalyzer(LineAnalyzerContext context);

    /**
     * Process the line with the analyzer
     * @throws IOException
     */
    Date parseLine(LineAnalyzerContext context) throws IOException;

    /**
     * This method is called when this analyzer is being switched to another one by the composite analyzer,
     * so that it can cleanup it's state before passing control to another analyzer
     */
    void finishPreviousState(LineAnalyzerContext context);

    /**
     * Used to free any resources used by the line analyzer and write summary information
     */
    void stop() throws IOException;
}
