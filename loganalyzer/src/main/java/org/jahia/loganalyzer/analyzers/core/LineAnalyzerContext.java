package org.jahia.loganalyzer.analyzers.core;

import java.io.File;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.Deque;

/**
 * The context of the line analyzer, all the information needed to process the current line.
 */
public class LineAnalyzerContext {
    private String line;
    private String nextLine;
    private String nextNextLine;
    private Deque<String> contextLines;
    private LineNumberReader lineNumberReader;
    private Date lastValidDateParsed;
    private File file;
    private String jvmIdentifier;
    private long lineNumber;

    public LineAnalyzerContext() {
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getNextLine() {
        return nextLine;
    }

    public void setNextLine(String nextLine) {
        this.nextLine = nextLine;
    }

    public String getNextNextLine() {
        return nextNextLine;
    }

    public void setNextNextLine(String nextNextLine) {
        this.nextNextLine = nextNextLine;
    }

    public Deque<String> getContextLines() {
        return contextLines;
    }

    public void setContextLines(Deque<String> contextLines) {
        this.contextLines = contextLines;
    }

    public LineNumberReader getLineNumberReader() {
        return lineNumberReader;
    }

    public void setLineNumberReader(LineNumberReader lineNumberReader) {
        this.lineNumberReader = lineNumberReader;
    }

    public Date getLastValidDateParsed() {
        return lastValidDateParsed;
    }

    public void setLastValidDateParsed(Date lastValidDateParsed) {
        this.lastValidDateParsed = lastValidDateParsed;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getJvmIdentifier() {
        return jvmIdentifier;
    }

    public void setJvmIdentifier(String jvmIdentifier) {
        this.jvmIdentifier = jvmIdentifier;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }
}
