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
 * Date: 24 ao�t 2007
 * Time: 08:43:00
 * To change this template use File | Settings | File Templates.
 */
public class LogParserTest extends TestCase {

    private static final String DEFAULT_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss,SSS";
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
        LogParserConfiguration logParserConfiguration = new LogParserConfiguration();
        logParserConfiguration.setCsvSeparatorChar(';');
        InputStream jahiaLogStream = this.getClass().getResourceAsStream("/jahia-tomcat/catalina.out");
        Reader reader = new InputStreamReader(jahiaLogStream);
        logParserConfiguration.setInputFileName(this.getClass().getResource("/jahia-tomcat/catalina.out").toString());
        logParserConfiguration.setPatternList(new ArrayList());
        logParserConfiguration.setDateFormatString(DEFAULT_DATE_FORMAT_STRING);
        logParserConfiguration.setContextMapping("");
        logParserConfiguration.setServletMapping("/PFUE");
        logParser.setLogParserConfiguration(logParserConfiguration);
        logParser.parse(reader);
        logParser.stop();
    }
}

