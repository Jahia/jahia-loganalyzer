package org.jahia.loganalyzer.services.stacktrace;

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

import org.apache.commons.io.IOUtils;
import org.jahia.loganalyzer.services.stacktrace.internal.StackTraceServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Unit test for StackTraceService
 */
public class StackTraceServiceTest {

    @Test
    public void testGetDefinitionsIdFromStackTrace() throws IOException {
        StackTraceServiceImpl stackTraceServiceImpl = new StackTraceServiceImpl();
        stackTraceServiceImpl.start();
        List<String> stackTrace = getLines("/stacktrace/thread-pool-stack.txt");
        List<String> definitionIds = stackTraceServiceImpl.getDefinitionIdsFromStackTrace(stackTrace);
        Assert.assertTrue("There should be at least one definition ID", definitionIds.size() > 0);
        Assert.assertTrue("Couldn't find definitionID thread-pool-executor-parking in definition IDs", definitionIds.contains("thread-pool-executor-parking"));
        stackTrace = getLines("/stacktrace/ldap-socket-read.txt");
        definitionIds = stackTraceServiceImpl.getDefinitionIdsFromStackTrace(stackTrace);
        Assert.assertTrue("There should be at least two definition ID", definitionIds.size() > 1);
        Assert.assertTrue("Couldn't find definitionID ldap-socket-reading in definition IDs", definitionIds.contains("ldap-socket-reading"));
        Assert.assertTrue("Couldn't find definitionID socket-reading in definition IDs", definitionIds.contains("socket-reading"));
        stackTraceServiceImpl.stop();
    }

    private List<String> getLines(String resourcePath) throws IOException {
        InputStream resourceStream = this.getClass().getResourceAsStream(resourcePath);
        return IOUtils.readLines(resourceStream);
    }
}
