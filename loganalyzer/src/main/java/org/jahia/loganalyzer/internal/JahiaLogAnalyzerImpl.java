package org.jahia.loganalyzer.internal;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jahia.loganalyzer.api.JahiaLogAnalyzer;
import org.jahia.loganalyzer.api.LogEntryWriterFactory;
import org.jahia.loganalyzer.api.ProcessedLogFile;
import org.jahia.loganalyzer.configuration.LogParserConfiguration;
import org.jahia.loganalyzer.writers.ElasticSearchService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import javax.swing.*;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Main class of the Jahia Log Analyzer, this is where the application is started and performs all the most high-level
 * operations
 */
public class JahiaLogAnalyzerImpl implements JahiaLogAnalyzer {

    private static final Log log =
            LogFactory.getLog(JahiaLogAnalyzerImpl.class);
    ElasticSearchService elasticSearchService;
    private String buildNumber = "Unknown";
    private Date buildTimestamp = null;
    private String version = "DEVELOPMENT";
    private LogParserConfiguration logParserConfiguration = new LogParserConfiguration();
    private BundleContext bundleContext;

    public JahiaLogAnalyzerImpl() {
    }

    public static void main(String[] args) {
        JahiaLogAnalyzer jahiaLogAnalyzer = new JahiaLogAnalyzerImpl();
        File inputFile = null;
        if (args != null && args.length > 0) {
            inputFile = new File(args[0]);
            if (inputFile.exists()) {
                log.info("Analyzing logs in " + inputFile + "...");
            } else {
                inputFile = null;
            }
        } else {
            initUI();
        }
        jahiaLogAnalyzer.start(inputFile);
    }

    public static void initUI() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
            try {
                UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
            } catch (Throwable t) {
                log.error("Error setting look and feel", t);
            }
        }
    }

    public void setElasticSearchService(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void start() {
        System.out.println("Starting Jahia Log Analyzer main service...");
        retrieveBuildInformation();
        System.out.println("Jahia Log Analyzer main service started !");
    }

    public void stop() {
        System.out.println("Stopping Jahia Log Analyzer main service...");
    }

    @Override
    public void start(File inputFile) {
        retrieveBuildInformation();
        if (inputFile == null) {
            log.error("Missing input file !");
            System.exit(0);
        } else {
            try {
                Collection<ServiceReference<LogEntryWriterFactory>> logEntryWriterFactoryReferences = bundleContext.getServiceReferences(LogEntryWriterFactory.class, null);
                List<LogEntryWriterFactory> logEntryWriterFactories = new ArrayList<>();
                for (ServiceReference<LogEntryWriterFactory> logEntryWriterFactoryServiceReference : logEntryWriterFactoryReferences) {
                    LogEntryWriterFactory logEntryWriterFactory = bundleContext.getService(logEntryWriterFactoryServiceReference);
                    if (logEntryWriterFactory != null) {
                        logEntryWriterFactories.add(logEntryWriterFactory);
                    }
                }
                logParserConfiguration.setLogEntryWriterFactories(logEntryWriterFactories);
                logParserConfiguration.setInputFile(inputFile);
                if (analyze(null)) {
                    log.info("Analysis completed. ");
                } else {
                    log.info("Previous analysis found, using previous data.");
                }
                /*
                if (!elasticSearchService.isRemote()) {
                    log.info("You can access ElasticSearch plugin at http://localhost:9200/_plugin/loganalyzer");
                    log.info("If you have Kibana 4+ running, you can access it at http://localhost:5601");
                    log.info("Keeping embedded ElasticSearch server running, press CTRL-C to quit...");
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
                */
            } catch (IOException e) {
                log.error("Error during log analysis", e);
            } catch (InvalidSyntaxException e) {
                log.error("Error during log analysis", e);
            }
        }
    }

    @Override
    public boolean analyze(java.awt.Component uiComponent) throws IOException {
        File inputFile = logParserConfiguration.getInputFile();
        if (!inputFile.exists()) {
            log.warn("Couldn't find file " + inputFile);
            return true;
        }
        String jvmIdentifier = "jvm";
        if (inputFile.isDirectory()) {
            SortedSet<File> sortedFiles = new TreeSet<File>(FileUtils.listFiles(inputFile, null, true));
            if (!logParserConfiguration.getOutputDirectory().exists()) {
                createOutputDirectory(logParserConfiguration);
            }
            List<ProcessedLogFile> processedLogFiles = getProcessedLogFiles();
            LogParserImpl logParser = new LogParserImpl();
            logParser.setElasticSearchService(elasticSearchService);
            logParser.setLogParserConfiguration(logParserConfiguration);
            for (File file : sortedFiles) {
                if (file.getCanonicalPath().startsWith(logParserConfiguration.getOutputDirectory().getCanonicalPath())) {
                    continue;
                }

                ProcessedLogFile processedLogFile = new ProcessedLogFile(logParserConfiguration.getInputFile(), file);
                if (processedLogFiles.contains(processedLogFile)) {
                    log.info("File " + file + " already parsed previously, skipping");
                    continue;
                }
                String filePath = file.getPath();
                int jvmIdentifierPos = filePath.indexOf("jvm-");
                if (jvmIdentifierPos >= 0) {
                    int fileSeparatorPos = filePath.indexOf(File.separator, jvmIdentifierPos);
                    if (fileSeparatorPos >= 0) {
                        jvmIdentifier = filePath.substring(jvmIdentifierPos + "jvm-".length(), fileSeparatorPos);
                    } else {
                        jvmIdentifier = filePath.substring(jvmIdentifierPos + "jvm-".length());
                    }
                }
                Reader reader = getFileReader(uiComponent, file);
                log.info("Parsing file " + file + "...");
                processedLogFile = logParser.parse(reader, file, jvmIdentifier, processedLogFile.getLastParsedTimestamp());
                log.info("Parsing of file " + file + " completed.");
                reader.close();
                processedLogFiles.add(processedLogFile);
            }
            logParser.stop();
            saveProcessedLogFiles(processedLogFiles);
        } else {
            File file = logParserConfiguration.getInputFile();
            if (!logParserConfiguration.getOutputDirectory().exists()) {
                createOutputDirectory(logParserConfiguration);
            }
            List<ProcessedLogFile> processedLogFiles = getProcessedLogFiles();
            ProcessedLogFile processedLogFile = new ProcessedLogFile(logParserConfiguration.getInputFile().getParentFile(), file);
            if (processedLogFiles.contains(processedLogFile)) {
                // we instantiate the log parser just to make sure the embedded ElasticSearch is started.
                LogParserImpl logParser = new LogParserImpl();
                logParser.setElasticSearchService(elasticSearchService);
                logParser.setLogParserConfiguration(logParserConfiguration);
                logParser.stop();
            } else {
                LogParserImpl logParser = new LogParserImpl();
                logParser.setElasticSearchService(elasticSearchService);
                logParser.setLogParserConfiguration(logParserConfiguration);
                Reader reader = getFileReader(uiComponent, file);
                log.info("Parsing file " + file + "...");
                processedLogFile = logParser.parse(reader, file, jvmIdentifier, processedLogFile.getLastParsedTimestamp());
                log.info("Parsing of file " + file + " completed.");
                reader.close();
                logParser.stop();
                processedLogFiles.add(processedLogFile);
                saveProcessedLogFiles(processedLogFiles);
            }
        }
        return true;
    }

    private void createOutputDirectory(LogParserConfiguration logParserConfiguration) {
        File outputDirectory = logParserConfiguration.getOutputDirectory();
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
    }

    private List<ProcessedLogFile> getProcessedLogFiles() {
        File outputDirectory = logParserConfiguration.getOutputDirectory();
        if (!outputDirectory.exists()) {
            return new ArrayList<ProcessedLogFile>();
        }
        File processedFilesFile = new File(outputDirectory, "processed.json");
        if (!processedFilesFile.exists()) {
            return new ArrayList<ProcessedLogFile>();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ProcessedLogFile> processedLogFiles = mapper.readValue(processedFilesFile, new TypeReference<List<ProcessedLogFile>>() {
            });
            return processedLogFiles;
        } catch (IOException e) {
            log.error("Error reading loading processed file list from " + processedFilesFile, e);
        }
        return new ArrayList<ProcessedLogFile>();
    }

    private void saveProcessedLogFiles(List<ProcessedLogFile> processedLogFiles) {
        File outputDirectory = logParserConfiguration.getOutputDirectory();
        if (!outputDirectory.exists()) {
            return;
        }
        File processedFilesFile = new File(outputDirectory, "processed.json");
        if (!processedFilesFile.getParentFile().exists()) {
            processedFilesFile.getParentFile().mkdirs();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(processedFilesFile, processedLogFiles);
        } catch (IOException e) {
            log.error("Error writing processed log file " + processedFilesFile, e);
        }
    }

    private Reader getFileReader(java.awt.Component uiComponent, File file) throws FileNotFoundException {
        if (uiComponent != null) {
            InputStream in = new BufferedInputStream(
                    new ProgressMonitorInputStream(
                            uiComponent,
                            "Reading " + file,
                            new FileInputStream(file)));
            return new InputStreamReader(in);
        } else {
            return new BufferedReader(new FileReader(file));
        }
    }

    @Override
    public void retrieveBuildInformation() {
        try {
            URL urlToMavenPom = JahiaLogAnalyzerImpl.class.getClassLoader().getResource("/META-INF/jahia-loganalyzer-marker.txt");
            if (urlToMavenPom != null) {
                URLConnection urlConnection = urlToMavenPom.openConnection();
                if (urlConnection instanceof JarURLConnection) {
                    JarURLConnection conn = (JarURLConnection) urlConnection;
                    JarFile jarFile = conn.getJarFile();
                    Manifest manifest = jarFile.getManifest();
                    Attributes attributes = manifest.getMainAttributes();
                    buildNumber = attributes.getValue("Implementation-Build");
                    version = attributes.getValue("Implementation-Version");
                    String buildTimestampValue = attributes.getValue("Implementation-Timestamp");
                    if (buildTimestampValue != null) {
                        try {
                            long buildTimestampTime = Long.parseLong(buildTimestampValue);
                            buildTimestamp = new Date(buildTimestampTime);
                        } catch (NumberFormatException nfe) {
                            nfe.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            log.error("Error while trying to retrieve build information", ioe);
        } catch (NumberFormatException nfe) {
            log.error("Error while trying to retrieve build information", nfe);
        }
    }

    @Override
    public String getBuildNumber() {
        return buildNumber;
    }

    @Override
    public Date getBuildTimestamp() {
        return buildTimestamp;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public LogParserConfiguration getLogParserConfiguration() {
        return logParserConfiguration;
    }
}
