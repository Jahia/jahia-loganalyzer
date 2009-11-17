package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.LogEntry;

import java.io.LineNumberReader;
import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 ao�t 2007
 * Time: 12:51:57
 * To change this template use File | Settings | File Templates.
 */
public interface LineAnalyzer {
    /**
     * Checks whether the current line is going to be sent to a specific analyzer.
     * @param line
     * @return
     */
    boolean isForThisAnalyzer(String line, String nextLine);

    /**
     * Process the line with the analyzer
     * @param line
     * @param reader
     * @throws IOException
     */
    Date parseLine(String line, String nextLine, LineNumberReader reader, Date lastValidDateParsed) throws IOException;

    /**
     * This method is called when this analyzer is being switched to another one by the composite analyzer,
     * so that it can cleanup it's state before passing control to another analyzer
     */
    void finishPreviousState();

    /**
     * Used to free any resources used by the line analyzer
     */
    void stop() throws IOException;
}