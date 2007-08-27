package org.jahia.loganalyzer;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 24 août 2007
 * Time: 08:43:00
 * To change this template use File | Settings | File Templates.
 */
public class LogParserTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public LogParserTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(LogParserTest.class);
    }

    public void testLogParser() throws IOException {
        LogParser logParser = new LogParser();
        logParser.setCsvOutputSeparatorChar(';');
        InputStream jahiaLogStream = this.getClass().getResourceAsStream("/jahia-tomcat/catalina.out");
        Reader reader = new InputStreamReader(jahiaLogStream);
        Writer writer = new FileWriter("jahia-log-report.csv");
        logParser.parse(reader, writer, new ArrayList());
    }
}

