package org.jahia.loganalyzer.stacktrace;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for StackTraceService
 */
public class StackTraceServiceTest {

    @Test
    public void testGetDefinitionsIdFromStackTrace() throws IOException {
        StackTraceService stackTraceService = StackTraceService.getInstance();
        List<String> stackTrace = getLines("/stacktrace/thread-pool-stack.txt");
        List<String> definitionIds = stackTraceService.getDefinitionIdsFromStackTrace(stackTrace);
        assertTrue("There should be at least one definition ID", definitionIds.size() > 0);
        assertTrue("Couldn't find definitionID thread-pool-executor-parking in definition IDs", definitionIds.contains("thread-pool-executor-parking"));
        stackTrace = getLines("/stacktrace/ldap-socket-read.txt");
        definitionIds = stackTraceService.getDefinitionIdsFromStackTrace(stackTrace);
        assertTrue("There should be at least two definition ID", definitionIds.size() > 1);
        assertTrue("Couldn't find definitionID ldap-socket-reading in definition IDs", definitionIds.contains("ldap-socket-reading"));
        assertTrue("Couldn't find definitionID socket-reading in definition IDs", definitionIds.contains("socket-reading"));
    }

    private List<String> getLines(String resourcePath) throws IOException {
        InputStream resourceStream = this.getClass().getResourceAsStream(resourcePath);
        return IOUtils.readLines(resourceStream);
    }
}
