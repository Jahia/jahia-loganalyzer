package org.jahia.loganalyzer;

/*
 * #%L
 * Jahia Log Analyzer
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2007 - 2016 Jahia
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 24 aout 2007
 * Time: 08:43:00
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
        File inputFile = null;
        URL url = this.getClass().getResource("/jahia-tomcat/catalina.out");
        try {
            inputFile = new File(url.toURI());
        } catch (URISyntaxException e) {
            inputFile = new File(url.getPath());
        }
        logParserConfiguration.setInputFile(inputFile);
        logParserConfiguration.getOutputDirectory().mkdirs();
        logParserConfiguration.setPatternList(new ArrayList());
        logParserConfiguration.setDateFormatString(DEFAULT_DATE_FORMAT_STRING);
        logParserConfiguration.setContextMapping("");
        logParserConfiguration.setServletMapping("/PFUE");
        logParser.setLogParserConfiguration(logParserConfiguration);
        logParser.parse(reader, inputFile, "jvm", -1);
        logParser.stop();
    }
}

