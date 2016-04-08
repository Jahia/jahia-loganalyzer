package org.jahia.loganalyzer.analyzers.threaddumps;

import org.jahia.loganalyzer.LogParserConfiguration;
import org.jahia.loganalyzer.analyzers.core.LineAnalyzerContext;
import org.jahia.loganalyzer.analyzers.core.WritingLineAnalyzer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A log analyzer that parses Thread dumps from logs
 * User: Serge Huber
 * Date: July 8th, 2008
 * Time: 11:07:20
 */
public class ThreadDumpLineAnalyzer extends WritingLineAnalyzer {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(ThreadDumpLineAnalyzer.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    boolean inThreadDump = false;
    long currentThreadCount = 0;
    Date currentThreadDumpDate = null;
    ThreadDumpLogEntry currentThreadDumpLogEntry = null;
    ThreadDumpLogEntry lastThreadDumpLogEntry = null;
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

    Map<String, ThreadInfo> threads = new HashMap<String, ThreadInfo>();
    
    public ThreadDumpLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getThreadDetailsOutputFile(), logParserConfiguration.getThreadSummaryOutputFile(), logParserConfiguration.getCsvSeparatorChar(), new ThreadDumpThreadLogEntry(0, 0, null, null, null), new ThreadDumpLogEntry(0, 0, null, null, null), logParserConfiguration);
        sunJDK5ThreadInfoPattern = Pattern.compile(logParserConfiguration.getSunJDK5ThreadThreadInfoPattern());
        sunJDK6ThreadInfoPattern = Pattern.compile(logParserConfiguration.getSunJDK6ThreadThreadInfoPattern());
        sunJDK7ThreadInfoPattern = Pattern.compile(logParserConfiguration.getSunJDK7ThreadThreadInfoPattern());
        sunJDK8ThreadInfoPattern = Pattern.compile(logParserConfiguration.getSunJDK8ThreadThreadInfoPattern());
    }

    public String getKey() {
        return "threaddump";
    }

    public boolean isForThisAnalyzer(LineAnalyzerContext context) {
        if (inThreadDump) {
            // we must now check that it is indeed a valid thread dump line
            if (lineMatches(context.getLine())) {
                return true;
            } else if (lineMatches(context.getNextLine())) {
                // this is done for corrupted logs.
                return true;
            } else if (lineMatches(context.getNextNextLine())) {
                return true;
            }
            return false;
        }
        return (context.getLine().startsWith("Full thread dump")) ||
                (context.getLine().contains("Full Java thread dump"));
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

    public Date parseLine(LineAnalyzerContext context) throws IOException {

        this.lastValidDateParsed = context.getLastValidDateParsed();
        if (!lineMatches(context.getLine()) && lineMatches(context.getNextLine())) {
            return null;
        }

        Date dateToUse = lastValidDateParsed;
        if (context.getLine().startsWith("Full thread dump") ||
                context.getLine().contains("Full Java thread dump")) {
            if (inThreadDump) {
                // we found another thread dump just after the one we are currently analysing, let's
                // finish and recycle.
                finishPreviousState(context);
            }
            log.debug("Found thread dump, starting analysis...");
            inThreadDump = true;
            currentThreadDumpLogEntry = new ThreadDumpLogEntry(context.getLineNumber(), context.getLineNumber(), lastValidDateParsed, context.getJvmIdentifier(), context.getFile().getName());

            Matcher matcher = threadDumpDatePattern.matcher(context.getContextLines().peekLast());
            if (matcher.matches()) {
                String dateToUseString = matcher.group(1);
                try {
                    dateToUse = DATE_FORMAT.parse(dateToUseString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            currentThreadDumpLogEntry.setTimestamp(dateToUse);
            threadDumpCount++;
        } else if (context.getLine().startsWith("\"")) {
            // we are in the case of a new thread
            if (threadDumpThreadLogEntry != null) {
                completeLastThread(context);

            }
            threadDumpThreadLogEntry = new ThreadDumpThreadLogEntry(context.getLineNumber(), context.getLineNumber(), lastValidDateParsed, context.getJvmIdentifier(), context.getFile().getName());
            threadDumpThreadLogEntry.setThreadDumpNumber(threadDumpCount);
            threadDumpThreadLogEntry.setThreadNumber(++currentThreadCount);

            // replace all this parsing with patterns
            Matcher matcher = sunJDK5ThreadInfoPattern.matcher(context.getLine());
            boolean matches = matcher.matches();
            if (!matches) {
                matcher = sunJDK6ThreadInfoPattern.matcher(context.getLine());
                matches = matcher.matches();
                if (!matches) {
                    matcher = sunJDK7ThreadInfoPattern.matcher(context.getLine());
                    if (matcher.matches()) {
                        threadDumpThreadLogEntry.setThreadName(matcher.group(1));
                        threadDumpThreadLogEntry.setThreadNativeId(matcher.group(2));
                        threadDumpThreadLogEntry.setThreadId(matcher.group(2));
                        threadDumpThreadLogEntry.setThreadState(matcher.group(3).trim());
                        threadDumpThreadLogEntry.setThreadStateInfo(matcher.group(4));
                    } else {
                        matcher = sunJDK8ThreadInfoPattern.matcher(context.getLine());
                        if (matcher.matches()) {
                            threadDumpThreadLogEntry.setThreadName(matcher.group(1));
                            threadDumpThreadLogEntry.setThreadNativeId(matcher.group(9));
                            threadDumpThreadLogEntry.setThreadId(matcher.group(8));
                            threadDumpThreadLogEntry.setThreadState(matcher.group(10).trim());
                            threadDumpThreadLogEntry.setThreadStateInfo(matcher.group(10));
                            if (matcher.group(5) != null) {
                                threadDumpThreadLogEntry.setThreadPriority(Integer.parseInt(matcher.group(5)));
                            }
                            if (matcher.group(3) != null && matcher.group(3).length() > 0) {
                                threadDumpThreadLogEntry.setThreadType(matcher.group(3));
                            }
                        } else {
                            log.warn("Line " + context.getLineNumber() + " doesn't match any Sun JDK Regexp : " + context.getLine());
                            threadDumpThreadLogEntry.setThreadName(context.getLine());
                            threadDumpThreadLogEntry.setThreadId("invalid");
                            return null;
                        }
                    }
                } else {
                    // using JDK 6 format
                    threadDumpThreadLogEntry.setThreadName(matcher.group(1));
                    threadDumpThreadLogEntry.setThreadId(matcher.group(2));
                    threadDumpThreadLogEntry.setThreadState(matcher.group(3).trim());
                    threadDumpThreadLogEntry.setThreadStateInfo(matcher.group(4));
                }
            } else {
                threadDumpThreadLogEntry.setThreadName(matcher.group(1));
                threadDumpThreadLogEntry.setThreadType(matcher.group(2));
                if (threadDumpThreadLogEntry.getThreadType() != null) {
                    threadDumpThreadLogEntry.setThreadType(threadDumpThreadLogEntry.getThreadType().trim());
                }
                try {
                    threadDumpThreadLogEntry.setThreadPriority(Integer.parseInt(matcher.group(3)));
                } catch (NumberFormatException nfe) {
                    log.error("Couldn't parse priority for thread " + threadDumpThreadLogEntry + " in line : " + context.getLine(), nfe);
                }
                threadDumpThreadLogEntry.setThreadId(matcher.group(4));
                threadDumpThreadLogEntry.setThreadNativeId(matcher.group(5));
                threadDumpThreadLogEntry.setThreadState(matcher.group(6).trim());
                threadDumpThreadLogEntry.setThreadStateInfo(matcher.group(8));
            }
        } else if (context.getLine().trim().startsWith("java.lang.Thread.State: ")) {
            threadDumpThreadLogEntry.setThreadState(context.getLine().substring("java.lang.Thread.State: ".length()));
        } else if (context.getLine().trim().startsWith("at ")) {
            threadDumpThreadLogEntry.getStackTrace().add(context.getLine().substring(1));
        } else if (context.getLine().trim().startsWith("- locked ")) {
            //threadDumpThreadLogEntry.getStackTrace().add(line.substring(1));
            threadDumpThreadLogEntry.getHoldingLocks().add(context.getLine().substring(1));
        } else if (context.getLine().trim().startsWith("- waiting")) {
            //threadDumpThreadLogEntry.getStackTrace().add(line.substring(1));
            threadDumpThreadLogEntry.getWaitingOnLocks().add(context.getLine().substring(1));
        } else if (context.getLine().trim().startsWith("owned by")) {
            //threadDumpThreadLogEntry.getStackTrace().add(line.substring(1));
            threadDumpThreadLogEntry.getLockOwners().add(context.getLine().substring(1));
        } else if ("".equals(context.getLine().trim())) {

        } else {
            threadDumpThreadLogEntry.getStackTrace().add(context.getLine().substring(1));
        }

        return dateToUse;
    }

    private void completeLastThread(LineAnalyzerContext context) {
        // we were in another thread, let's cleanup before processing the new one.
        if (log.isTraceEnabled()) {
            log.trace("Thread " + currentThreadCount + " : " + threadDumpThreadLogEntry);
        }
        threadDumpThreadLogEntry.setEndLineNumber(context.getLineNumber());
        writeDetails(threadDumpThreadLogEntry, context.getMinimalTimestamp());

        ThreadInfo newThreadInfo = new ThreadInfo(threadDumpThreadLogEntry.getThreadId());
        newThreadInfo.setThreadName(threadDumpThreadLogEntry.getThreadName());
        newThreadInfo.setThreadNativeId(threadDumpThreadLogEntry.getThreadNativeId());
        newThreadInfo.setDeamon(!"normal".equals(threadDumpThreadLogEntry.getThreadType()));
        newThreadInfo.setStackTrace(threadDumpThreadLogEntry.getStackTrace());
        newThreadInfo.setHoldsLocks(threadDumpThreadLogEntry.getHoldingLocks());
        newThreadInfo.setWaitingOnLocks(threadDumpThreadLogEntry.getWaitingOnLocks());
        newThreadInfo.setLastModificationTime(threadDumpThreadLogEntry.getTimestamp());

        ThreadInfo oldThreadInfo = threads.get(newThreadInfo.getThreadId());
        if (oldThreadInfo == null) {

        } else {
            // we must compare the old and the new thread info
            if (oldThreadInfo.equals(newThreadInfo)) {
                if (newThreadInfo.getLastModificationTime() != null) {
                    if (oldThreadInfo.getLastModificationTime() == null) {
                        // we don't have a timestamp for the old thread, so we can't time them. This shouldn't happen
                        // normally !
                    } else {
                        long timeStuck = newThreadInfo.getLastModificationTime().getTime() - oldThreadInfo.getLastModificationTime().getTime();
                        if (timeStuck > 2000 && timeStuck < 1000 * 60 * 60 * 24) {
                            oldThreadInfo.setStuckTime(timeStuck);
                            currentThreadDumpLogEntry.getStuckThreads().add(oldThreadInfo);
                        }
                    }
                }

                // we keep simply the oldThreadInfo
                newThreadInfo = oldThreadInfo;
            }
        }

        threads.put(newThreadInfo.getThreadId(), newThreadInfo);

        currentThreadDumpLogEntry.getThreadDumpLogEntries().add(threadDumpThreadLogEntry);
        currentThreadDumpLogEntry.getThreadNames().put(threadDumpThreadLogEntry.getThreadId(), threadDumpThreadLogEntry.getThreadName());
    }

    public void finishPreviousState(LineAnalyzerContext context) {
        log.debug("Found end of thread dump, cleaning up...");
        if (currentThreadDumpLogEntry != null) {
            if (threadDumpThreadLogEntry != null) {
                completeLastThread(context);
            }
            log.info("Found " + currentThreadDumpLogEntry.getThreadDumpLogEntries().size() +
                    " threads in thread dump for jvm=" + currentThreadDumpLogEntry.getJvmIdentifier() +
                    " source=" + currentThreadDumpLogEntry.getSource() +
                    " stuck=" + currentThreadDumpLogEntry.getStuckThreads().size() + ".");
            currentThreadDumpLogEntry.setThreadDumpNumber(threadDumpCount);
            currentThreadDumpLogEntry.computeDifferences(lastThreadDumpLogEntry);
            currentThreadDumpLogEntry.setEndLineNumber(context.getLineNumber());
            writeSummary(currentThreadDumpLogEntry, context.getMinimalTimestamp());
            allThreadDumps.add(currentThreadDumpLogEntry);
        }
        inThreadDump = false;
        threadDumpThreadLogEntry = null;
        currentThreadCount = 0;
        lastThreadDumpLogEntry = currentThreadDumpLogEntry;
    }

    public void stop() throws IOException {
        super.stop();
        log.info("Found " + allThreadDumps.size() + " thread dumps.");
    }

}
