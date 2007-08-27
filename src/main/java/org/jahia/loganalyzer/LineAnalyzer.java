package org.jahia.loganalyzer;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 août 2007
 * Time: 12:51:57
 * To change this template use File | Settings | File Templates.
 */
public interface LineAnalyzer {
    LogEntry parseLine(String line);
}
