package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.LineNumberReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 11:07:20
 * To change this template use File | Settings | File Templates.
 */
public class ThreadDumpLineAnalyzer extends CSVOutputLineAnalyzer {

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
    private static final String DEFAULT_THREADINFO_PATTERN = "\"(.*?)\" (daemon )?prio=(\\d*) tid=(.*?) nid=(.*?) ([\\w\\.\\(\\) ]*)(\\[(.*)\\])?";
    Pattern threadInfoPattern;

    ThreadDumpLogEntry threadDumpLogEntry = null;
    Date lastValidDateParsed = null;
    
    public ThreadDumpLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getThreadDetailsOutputFileName(), logParserConfiguration.getThreadSummaryOutputFileName(), logParserConfiguration.getCsvSeparatorChar(), new ThreadDumpLogEntry(), new ThreadSummaryLogEntry());
        threadInfoPattern = Pattern.compile(DEFAULT_THREADINFO_PATTERN);        
    }

    public boolean isForThisAnalyzer(String line, String nextLine) {
        if (inThreadDump) {
            // we must now check that it is indeed a valid thread dump line
            if (line.startsWith("Full thread dump") ||
                line.startsWith("\"") ||
                line.startsWith("\tat") ||
                line.startsWith("\t- locked") ||
                line.startsWith("\t- waiting") ||
                "".equals(line)) {
                return true;
            }
            return false;
        }
        if (line.startsWith("Full thread dump")) {
            return true;
        }
        return false;
    }

    public Date parseLine(String line, String nextLine, LineNumberReader lineNumberReader, Date lastValidDateParsed) throws IOException {

        this.lastValidDateParsed = lastValidDateParsed;
        if (line.startsWith("Full thread dump")) {
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
            if (threadDumpLogEntry != null) {
                // we were in another thread, let's cleanup before processing the new one.
                if (log.isTraceEnabled()) {
                    log.trace("Thread " + currentThreadCount + " : " + threadDumpLogEntry);
                }
                getLogEntryWriter().write(threadDumpLogEntry);
                currentSummaryLogEntry.getThreadDumpLogEntries().add(threadDumpLogEntry);
                currentSummaryLogEntry.getThreadNames().put(threadDumpLogEntry.getTid(), threadDumpLogEntry.getName());
            }
            threadDumpLogEntry = new ThreadDumpLogEntry();
            threadDumpLogEntry.setDumpNumber(threadDumpCount);
            threadDumpLogEntry.setDumpDate(lastValidDateParsed);
            threadDumpLogEntry.setThreadNumber(++currentThreadCount);

            // replace all this parsing with patterns
            Matcher matcher = threadInfoPattern.matcher(line);
            boolean matches = matcher.matches();
            if (!matches) {
                log.warn("Line doesn't match : " + line);
                return null;
            }
            threadDumpLogEntry.setName(matcher.group(1));
            threadDumpLogEntry.setType(matcher.group(2));
            if (threadDumpLogEntry.getType() != null) {
                threadDumpLogEntry.setType(threadDumpLogEntry.getType().trim());
            }
            try {
                threadDumpLogEntry.setPriority(Integer.parseInt(matcher.group(3)));
            } catch (NumberFormatException nfe) {
                log.error("Couldn't parse priority for thread " + threadDumpLogEntry + " in line : " + line, nfe);
            }
            threadDumpLogEntry.setTid(matcher.group(4));
            threadDumpLogEntry.setNid(matcher.group(5));
            threadDumpLogEntry.setState(matcher.group(6).trim());
            threadDumpLogEntry.setStateInfo(matcher.group(8));
        } else if (line.startsWith("\tat")) {
            threadDumpLogEntry.getStackTrace().add(line.substring(1));
        } else if (line.startsWith("\t- locked")) {
            threadDumpLogEntry.getStackTrace().add(line.substring(1));
            threadDumpLogEntry.getHoldingLocks().add(line.substring(1));
        } else if (line.startsWith("\t- waiting")) {
            threadDumpLogEntry.getStackTrace().add(line.substring(1));
            threadDumpLogEntry.getWaitingOnLocks().add(line.substring(1));
        } else if ("".equals(line)) {

        }

        return null;
    }

    public void finishPreviousState() {
        log.debug("Found end of thread dump, cleaning up...");
        if (currentSummaryLogEntry != null) {
            log.info("Found " + currentSummaryLogEntry.getThreadDumpLogEntries().size() + " threads in thread dump.");
            currentSummaryLogEntry.setDumpNumber(threadDumpCount);
            currentSummaryLogEntry.computeDifferences(lastSummaryLogEntry);
            getSummaryLogEntryWriter().write(currentSummaryLogEntry);
            allThreadDumps.add(currentSummaryLogEntry);
        }
        inThreadDump = false;
        threadDumpLogEntry = null;
        currentThreadCount = 0;
        lastSummaryLogEntry = currentSummaryLogEntry;
    }

    public void stop() throws IOException {
        super.stop();
        log.info("Found " + allThreadDumps.size() + " thread dumps.");
    }

}