package org.jahia.loganalyzer.itests;

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

import org.jahia.loganalyzer.api.JahiaLogAnalyzer;
import org.jahia.loganalyzer.common.ResourceUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class BasicTest extends BaseTest {

    @Test
    public void testBaseSystem() throws IOException {
        assertNotNull("Bundle context is not available", bundleContext);
        for (Bundle bundle : bundleContext.getBundles()) {
            System.out.println("Test : found installed bundle " + bundle);
        }
    }

    @Test
    public void testLogAnalyzerService() throws IOException {
        assertNotNull("Bundle context is not available", bundleContext);
        ServiceReference<JahiaLogAnalyzer> jahiaLogAnalyzerServiceReference = bundleContext.getServiceReference(JahiaLogAnalyzer.class);
        assertNotNull("Jahia Log Analyzer service is not available", jahiaLogAnalyzerServiceReference);
        JahiaLogAnalyzer jahiaLogAnalyzer = bundleContext.getService(jahiaLogAnalyzerServiceReference);
        jahiaLogAnalyzer.retrieveBuildInformation();
        System.out.println("Jahia Log Analyzer version=" + jahiaLogAnalyzer.getVersion());

        Path tempDirectory = Files.createTempDirectory("loganalyzer-itest-temp");
        ResourceUtils.copyResourceToFile(this.getClass().getClassLoader(), "/jahia-tomcat/catalina.out", tempDirectory.toFile());
        File inputFile = new File(tempDirectory.toFile(), "catalina.out");
        jahiaLogAnalyzer.start(inputFile);

        File resultDirectory = new File(tempDirectory.toFile(), "catalina-loganalyzer-results");
        assertTrue("Results directory not found, expected at " + resultDirectory.getPath(), resultDirectory.exists());
    }

}
