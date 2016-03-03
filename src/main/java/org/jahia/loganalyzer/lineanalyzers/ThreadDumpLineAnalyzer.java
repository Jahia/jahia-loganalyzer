package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.LineNumberReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 11:07:20
 * To change this template use File | Settings | File Templates.
 */
public class ThreadDumpLineAnalyzer extends WritingLineAnalyzer {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(ThreadDumpLineAnalyzer.class);

    boolean inThreadDump = false;
    long currentThreadCount = 0;
    Date currentThreadDumpDate = null;
    ThreadSummaryLogEntry currentSummaryLogEntry = null;
    ThreadSummaryLogEntry lastSummaryLogEntry = null;
    List<ThreadSummaryLogEntry> allThreadDumps = new ArrayList<ThreadSummaryLogEntry>();
    long threadDumpCount = 0;
    List<String> currentStackTrace = new ArrayList<String>();
    Pattern sunJDK5ThreadInfoPattern;
    Pattern sunJDK6ThreadInfoPattern;
    Pattern sunJDK7ThreadInfoPattern;
    Pattern sunJDK8ThreadInfoPattern;

    ThreadDumpDetailsLogEntry threadDumpDetailsLogEntry = null;
    Date lastValidDateParsed = null;
    
    public ThreadDumpLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getThreadDetailsOutputFileName(), logParserConfiguration.getThreadSummaryOutputFileName(), logParserConfiguration.getCsvSeparatorChar(), new ThreadDumpDetailsLogEntry(), new ThreadSummaryLogEntry());
        sunJDK5ThreadInfoPattern = Pattern.compile(logParserConfiguration.getSunJDK5ThreadThreadInfoPattern());
        sunJDK6ThreadInfoPattern = Pattern.compile(logParserConfiguration.getSunJDK6ThreadThreadInfoPattern());
        sunJDK7ThreadInfoPattern = Pattern.compile(logParserConfiguration.getSunJDK7ThreadThreadInfoPattern());
        sunJDK8ThreadInfoPattern = Pattern.compile(logParserConfiguration.getSunJDK8ThreadThreadInfoPattern());
    }

    public boolean isForThisAnalyzer(String line, String nextLine, String nextNextLine) {
        if (inThreadDump) {
            // we must now check that it is indeed a valid thread dump line
            if (lineMatches(line)) {
                return true;
            } else if (lineMatches(nextLine)) {
                // this is done for corrupted logs.
                return true;
            } else if (lineMatches(nextNextLine)) {
                return true;
            }
            return false;
        }
        if ((line.startsWith("Full thread dump")) ||
            (line.contains("Full Java thread dump"))) {
            return true;
        }
        return false;
    }

    private boolean lineMatches(String line) {
        if (line.startsWith("Full thread dump") ||
                line.contains("Full Java thread dump") ||
                line.startsWith("\"") ||
                line.startsWith("\tat") ||
                line.trim().startsWith("at") ||
                line.startsWith("\t- locked") ||
                line.trim().startsWith("- locked") ||
                line.startsWith("\t- waiting") ||
                 line.trim().startsWith("- waiting") ||
                line.trim().startsWith("owned by") ||
                line.contains("STDOUT") ||
                "".equals(line.trim())) {
                return true;
        } else {
            return false;
        }
    }

    public Date parseLine(String line, String nextLine, String nextNextLine, LineNumberReader lineNumberReader, Date lastValidDateParsed) throws IOException {

        this.lastValidDateParsed = lastValidDateParsed;
        if (!lineMatches(line) && lineMatches(nextLine)) {
            return null;
        }

        if (line.startsWith("Full thread dump") ||
            line.contains("Full Java thread dump")) {
            if (inThreadDump) {
                // we found another thread dump just after the one we are currently analysing, let's
                // finish and recycle.
                finishPreviousState();
            }
            log.debug("Found thread dump, starting analysis...");
            inThreadDump = true;
            currentSummaryLogEntry = new ThreadSummaryLogEntry();
            currentSummaryLogEntry.setThreadDumpDate(lastValidDateParsed);
            threadDumpCount++;
        } else if (line.startsWith("\"")) {
            // we are in the case of a new thread
            if (threadDumpDetailsLogEntry != null) {
                // we were in another thread, let's cleanup before processing the new one.
                if (log.isTraceEnabled()) {
                    log.trace("Thread " + currentThreadCount + " : " + threadDumpDetailsLogEntry);
                }
                writeDetails(threadDumpDetailsLogEntry);
                currentSummaryLogEntry.getThreadDumpLogEntries().add(threadDumpDetailsLogEntry);
                currentSummaryLogEntry.getThreadNames().put(threadDumpDetailsLogEntry.getTid(), threadDumpDetailsLogEntry.getName());
            }
            threadDumpDetailsLogEntry = new ThreadDumpDetailsLogEntry();
            threadDumpDetailsLogEntry.setLineNumber(lineNumberReader.getLineNumber()-1);
            threadDumpDetailsLogEntry.setDumpNumber(threadDumpCount);
            threadDumpDetailsLogEntry.setDumpDate(lastValidDateParsed);
            threadDumpDetailsLogEntry.setThreadNumber(++currentThreadCount);

            // replace all this parsing with patterns
            Matcher matcher = sunJDK5ThreadInfoPattern.matcher(line);
            boolean matches = matcher.matches();
            if (!matches) {
                matcher = sunJDK6ThreadInfoPattern.matcher(line);
                matches = matcher.matches();
                if (!matches) {
                    matcher = sunJDK7ThreadInfoPattern.matcher(line);
                    if (matcher.matches()) {
                        threadDumpDetailsLogEntry.setName(matcher.group(1));
                        threadDumpDetailsLogEntry.setNid(matcher.group(2));
                        threadDumpDetailsLogEntry.setTid(matcher.group(2));
                        threadDumpDetailsLogEntry.setState(matcher.group(3).trim());
                        threadDumpDetailsLogEntry.setStateInfo(matcher.group(4));
                    } else {
                        matcher = sunJDK8ThreadInfoPattern.matcher(line);
                        if (matcher.matches()) {
                            threadDumpDetailsLogEntry.setName(matcher.group(1));
                            threadDumpDetailsLogEntry.setNid(matcher.group(7));
                            threadDumpDetailsLogEntry.setTid(matcher.group(6));
                            threadDumpDetailsLogEntry.setState(matcher.group(8).trim());
                            threadDumpDetailsLogEntry.setStateInfo(matcher.group(9));
                        } else {
                            log.warn("Line " + lineNumberReader.getLineNumber() + " doesn't match any Sun JDK Regexp : " + line);
                            threadDumpDetailsLogEntry.setName(line);
                            threadDumpDetailsLogEntry.setTid("invalid");
                            return null;
                        }
                    }
                } else {
                    // using JDK 6 format
                    threadDumpDetailsLogEntry.setName(matcher.group(1));
                    threadDumpDetailsLogEntry.setTid(matcher.group(2));
                    threadDumpDetailsLogEntry.setState(matcher.group(3).trim());
                    threadDumpDetailsLogEntry.setStateInfo(matcher.group(4));
                }
            } else {
                threadDumpDetailsLogEntry.setName(matcher.group(1));
                threadDumpDetailsLogEntry.setType(matcher.group(2));
                if (threadDumpDetailsLogEntry.getType() != null) {
                    threadDumpDetailsLogEntry.setType(threadDumpDetailsLogEntry.getType().trim());
                }
                try {
                    threadDumpDetailsLogEntry.setPriority(Integer.parseInt(matcher.group(3)));
                } catch (NumberFormatException nfe) {
                    log.error("Couldn't parse priority for thread " + threadDumpDetailsLogEntry + " in line : " + line, nfe);
                }
                threadDumpDetailsLogEntry.setTid(matcher.group(4));
                threadDumpDetailsLogEntry.setNid(matcher.group(5));
                threadDumpDetailsLogEntry.setState(matcher.group(6).trim());
                threadDumpDetailsLogEntry.setStateInfo(matcher.group(8));
            }
        } else if (line.trim().startsWith("java.lang.Thread.State: ")) {
            threadDumpDetailsLogEntry.setState(line.substring("java.lang.Thread.State: ".length()));
        } else if (line.trim().startsWith("at ")) {
            threadDumpDetailsLogEntry.getStackTrace().add(line.substring(1));
        } else if (line.trim().startsWith("- locked ")) {
            threadDumpDetailsLogEntry.getStackTrace().add(line.substring(1));
            threadDumpDetailsLogEntry.getHoldingLocks().add(line.substring(1));
        } else if (line.trim().startsWith("- waiting")) {
            threadDumpDetailsLogEntry.getStackTrace().add(line.substring(1));
            threadDumpDetailsLogEntry.getWaitingOnLocks().add(line.substring(1));
        } else if (line.trim().startsWith("owned by")) {
            threadDumpDetailsLogEntry.getStackTrace().add(line.substring(1));
            threadDumpDetailsLogEntry.getLockOwners().add(line.substring(1));
        } else if ("".equals(line.trim())) {

        }

        return null;
    }

    public void finishPreviousState() {
        log.debug("Found end of thread dump, cleaning up...");
        if (currentSummaryLogEntry != null) {
            log.info("Found " + currentSummaryLogEntry.getThreadDumpLogEntries().size() + " threads in thread dump.");
            currentSummaryLogEntry.setDumpNumber(threadDumpCount);
            currentSummaryLogEntry.computeDifferences(lastSummaryLogEntry);
            writeSummary(currentSummaryLogEntry);
            allThreadDumps.add(currentSummaryLogEntry);
        }
        inThreadDump = false;
        threadDumpDetailsLogEntry = null;
        currentThreadCount = 0;
        lastSummaryLogEntry = currentSummaryLogEntry;
    }

    public void stop() throws IOException {
        super.stop();
        log.info("Found " + allThreadDumps.size() + " thread dumps.");
    }

}
