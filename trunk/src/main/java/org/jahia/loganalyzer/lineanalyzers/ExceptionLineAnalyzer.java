package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.*;

import java.io.LineNumberReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 11:08:16
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionLineAnalyzer extends CSVOutputLineAnalyzer {

    public ExceptionLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getExceptionsOutputFileName(), logParserConfiguration.getExceptionsSummaryOutputFileName(),logParserConfiguration.getCsvSeparatorChar(), new ExceptionLogEntry(), new ExceptionSummaryLogEntry());
    }

    public boolean isForThisAnalyzer(String line) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Date parseLine(String line, LineNumberReader lineNumberReader, Date lastValidDateParsed) {
        return null;
    }

    public void finishPreviousState() {
    }
}
