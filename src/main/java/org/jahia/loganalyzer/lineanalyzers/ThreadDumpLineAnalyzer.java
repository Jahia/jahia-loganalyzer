package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.LogParserConfiguration;
import org.jahia.loganalyzer.ThreadDumpLogEntry;
import org.jahia.loganalyzer.ThreadDumpThreadLogEntry;

import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    boolean inThreadDump = false;
    long currentThreadCount = 0;
    Date currentThreadDumpDate = null;
    ThreadDumpLogEntry currentSummaryLogEntry = null;
    ThreadDumpLogEntry lastSummaryLogEntry = null;
    List<ThreadDumpLogEntry> allThreadDumps = new ArrayList<ThreadDumpLogEntry>();
    long threadDumpCount = 0;
    List<String> currentStackTrace = new ArrayList<String>();
    Pattern sunJDK5ThreadInfoPattern;
    Pattern sunJDK6ThreadInfoPattern;
    Pattern sunJDK7ThreadInfoPattern;
    Pattern sunJDK8ThreadInfoPattern;
    Pattern threadDumpDatePattern = Pattern.compile("(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d)");
    ThreadDumpThreadLogEntry threadDumpThreadLogEntry = null;
    Date lastValidDateParsed = null;
    
    public ThreadDumpLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getThreadDetailsOutputFile(), logParserConfiguration.getThreadSummaryOutputFile(), logParserConfiguration.getCsvSeparatorChar(), new ThreadDumpThreadLogEntry(), new ThreadDumpLogEntry(), logParserConfiguration);
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
        return (line.startsWith("Full thread dump")) ||
                (line.contains("Full Java thread dump"));
    }

    private boolean lineMatches(String line) {
        return line.startsWith("Full thread dump") ||
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
                "".equals(line.trim());
    }

    public Date parseLine(String line, String nextLine, String nextNextLine, Deque<String> contextLines, LineNumberReader lineNumberReader, Date lastValidDateParsed) throws IOException {

        this.lastValidDateParsed = lastValidDateParsed;
        if (!lineMatches(line) && lineMatches(nextLine)) {
            return null;
        }

        Date dateToUse = lastValidDateParsed;
        if (line.startsWith("Full thread dump") ||
            line.contains("Full Java thread dump")) {
            if (inThreadDump) {
                // we found another thread dump just after the one we are currently analysing, let's
                // finish and recycle.
                finishPreviousState();
            }
            log.debug("Found thread dump, starting analysis...");
            inThreadDump = true;
            currentSummaryLogEntry = new ThreadDumpLogEntry();

            Matcher matcher = threadDumpDatePattern.matcher(contextLines.peekLast());
            if (matcher.matches()) {
                String dateToUseString = matcher.group(1);
                try {
                    dateToUse = DATE_FORMAT.parse(dateToUseString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            currentSummaryLogEntry.setThreadDumpDate(dateToUse);
            threadDumpCount++;
        } else if (line.startsWith("\"")) {
            // we are in the case of a new thread
            if (threadDumpThreadLogEntry != null) {
                // we were in another thread, let's cleanup before processing the new one.
                if (log.isTraceEnabled()) {
                    log.trace("Thread " + currentThreadCount + " : " + threadDumpThreadLogEntry);
                }
                writeDetails(threadDumpThreadLogEntry);
                currentSummaryLogEntry.getThreadDumpLogEntries().add(threadDumpThreadLogEntry);
                currentSummaryLogEntry.getThreadNames().put(threadDumpThreadLogEntry.getTid(), threadDumpThreadLogEntry.getName());
            }
            threadDumpThreadLogEntry = new ThreadDumpThreadLogEntry();
            threadDumpThreadLogEntry.setLineNumber(lineNumberReader.getLineNumber() - 1);
            threadDumpThreadLogEntry.setDumpNumber(threadDumpCount);
            threadDumpThreadLogEntry.setTimestamp(lastValidDateParsed);
            threadDumpThreadLogEntry.setThreadNumber(++currentThreadCount);

            // replace all this parsing with patterns
            Matcher matcher = sunJDK5ThreadInfoPattern.matcher(line);
            boolean matches = matcher.matches();
            if (!matches) {
                matcher = sunJDK6ThreadInfoPattern.matcher(line);
                matches = matcher.matches();
                if (!matches) {
                    matcher = sunJDK7ThreadInfoPattern.matcher(line);
                    if (matcher.matches()) {
                        threadDumpThreadLogEntry.setName(matcher.group(1));
                        threadDumpThreadLogEntry.setNid(matcher.group(2));
                        threadDumpThreadLogEntry.setTid(matcher.group(2));
                        threadDumpThreadLogEntry.setState(matcher.group(3).trim());
                        threadDumpThreadLogEntry.setStateInfo(matcher.group(4));
                    } else {
                        matcher = sunJDK8ThreadInfoPattern.matcher(line);
                        if (matcher.matches()) {
                            threadDumpThreadLogEntry.setName(matcher.group(1));
                            threadDumpThreadLogEntry.setNid(matcher.group(7));
                            threadDumpThreadLogEntry.setTid(matcher.group(6));
                            threadDumpThreadLogEntry.setState(matcher.group(8).trim());
                            threadDumpThreadLogEntry.setStateInfo(matcher.group(9));
                        } else {
                            log.warn("Line " + lineNumberReader.getLineNumber() + " doesn't match any Sun JDK Regexp : " + line);
                            threadDumpThreadLogEntry.setName(line);
                            threadDumpThreadLogEntry.setTid("invalid");
                            return null;
                        }
                    }
                } else {
                    // using JDK 6 format
                    threadDumpThreadLogEntry.setName(matcher.group(1));
                    threadDumpThreadLogEntry.setTid(matcher.group(2));
                    threadDumpThreadLogEntry.setState(matcher.group(3).trim());
                    threadDumpThreadLogEntry.setStateInfo(matcher.group(4));
                }
            } else {
                threadDumpThreadLogEntry.setName(matcher.group(1));
                threadDumpThreadLogEntry.setType(matcher.group(2));
                if (threadDumpThreadLogEntry.getType() != null) {
                    threadDumpThreadLogEntry.setType(threadDumpThreadLogEntry.getType().trim());
                }
                try {
                    threadDumpThreadLogEntry.setPriority(Integer.parseInt(matcher.group(3)));
                } catch (NumberFormatException nfe) {
                    log.error("Couldn't parse priority for thread " + threadDumpThreadLogEntry + " in line : " + line, nfe);
                }
                threadDumpThreadLogEntry.setTid(matcher.group(4));
                threadDumpThreadLogEntry.setNid(matcher.group(5));
                threadDumpThreadLogEntry.setState(matcher.group(6).trim());
                threadDumpThreadLogEntry.setStateInfo(matcher.group(8));
            }
        } else if (line.trim().startsWith("java.lang.Thread.State: ")) {
            threadDumpThreadLogEntry.setState(line.substring("java.lang.Thread.State: ".length()));
        } else if (line.trim().startsWith("at ")) {
            threadDumpThreadLogEntry.getStackTrace().add(line.substring(1));
        } else if (line.trim().startsWith("- locked ")) {
            threadDumpThreadLogEntry.getStackTrace().add(line.substring(1));
            threadDumpThreadLogEntry.getHoldingLocks().add(line.substring(1));
        } else if (line.trim().startsWith("- waiting")) {
            threadDumpThreadLogEntry.getStackTrace().add(line.substring(1));
            threadDumpThreadLogEntry.getWaitingOnLocks().add(line.substring(1));
        } else if (line.trim().startsWith("owned by")) {
            threadDumpThreadLogEntry.getStackTrace().add(line.substring(1));
            threadDumpThreadLogEntry.getLockOwners().add(line.substring(1));
        } else if ("".equals(line.trim())) {

        }

        return dateToUse;
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
        threadDumpThreadLogEntry = null;
        currentThreadCount = 0;
        lastSummaryLogEntry = currentSummaryLogEntry;
    }

    public void stop() throws IOException {
        super.stop();
        log.info("Found " + allThreadDumps.size() + " thread dumps.");
    }

}
