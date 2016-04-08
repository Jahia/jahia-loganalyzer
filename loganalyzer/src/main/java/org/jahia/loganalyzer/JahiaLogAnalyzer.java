package org.jahia.loganalyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jahia.loganalyzer.gui.swing.LogAnalyzerMainDialog;

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
public class JahiaLogAnalyzer {

    private static final Log log =
            LogFactory.getLog(JahiaLogAnalyzer.class);

    private String buildNumber = "Unknown";
    private Date buildTimestamp = null;
    private String version = "DEVELOPMENT";

    private LogParserConfiguration logParserConfiguration = new LogParserConfiguration();

    public static void main(String[] args) {
        JahiaLogAnalyzer jahiaLogAnalyzer = new JahiaLogAnalyzer();
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

    public void start(File inputFile) {
        retrieveBuildInformation();
        if (inputFile == null) {
            LogAnalyzerMainDialog dialog = new LogAnalyzerMainDialog(this);
            dialog.pack();
            dialog.setVisible(true);
            System.exit(0);
        } else {
            try {
                logParserConfiguration.setInputFile(inputFile);
                if (analyze(null)) {
                    log.info("Analysis completed. ");
                } else {
                    log.info("Previous analysis found, using previous data.");
                }
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
            } catch (IOException e) {
                log.error("Error during log analysis", e);
            }
        }
    }

    public boolean analyze(java.awt.Component uiComponent) throws IOException {
        File inputFile = logParserConfiguration.getInputFile();
        if (!inputFile.exists()) {
            return true;
        }
        String jvmIdentifier = "jvm";
        if (inputFile.isDirectory()) {
            SortedSet<File> sortedFiles = new TreeSet<File>(FileUtils.listFiles(inputFile, null, true));
            if (!logParserConfiguration.getOutputDirectory().exists()) {
                createOutputDirectory(logParserConfiguration);
            }
            List<ProcessedLogFile> processedLogFiles = getProcessedLogFiles();
            LogParser logParser = new LogParser();
            logParser.setLogParserConfiguration(logParserConfiguration);
            for (File file : sortedFiles) {
                if (file.getCanonicalPath().startsWith(logParserConfiguration.getOutputDirectory().getCanonicalPath())) {
                    continue;
                }

                ProcessedLogFile processedLogFile = new ProcessedLogFile(logParserConfiguration.getInputFile(), file);
                if (processedLogFiles.contains(processedLogFile)) {
                    continue;
                } else {
                    // @todo here we should check if we have a timestamp for the file in which case we will skip all
                    // entries before the timestamp
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
                Date lastValidDateParsed = logParser.parse(reader, file, jvmIdentifier);
                log.info("Parsing of file " + file + " completed.");
                reader.close();
                if (lastValidDateParsed != null) {
                    processedLogFile.setLastParsedTimestamp(lastValidDateParsed.getTime());
                }
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
                LogParser logParser = new LogParser();
                logParser.setLogParserConfiguration(logParserConfiguration);
                logParser.stop();
            } else {
                // @todo here we should check if we have a timestamp for the file in which case we will skip all
                // entries before the timestamp
                LogParser logParser = new LogParser();
                logParser.setLogParserConfiguration(logParserConfiguration);
                Reader reader = getFileReader(uiComponent, file);
                log.info("Parsing file " + file + "...");
                Date lastValidDateParsed = logParser.parse(reader, file, jvmIdentifier);
                log.info("Parsing of file " + file + " completed.");
                reader.close();
                logParser.stop();
                if (lastValidDateParsed != null) {
                    processedLogFile.setLastParsedTimestamp(lastValidDateParsed.getTime());
                }
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
            List<ProcessedLogFile> processedLogFiles = (List<ProcessedLogFile>) mapper.readValue(processedFilesFile, List.class);
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

    public void retrieveBuildInformation() {
        try {
            URL urlToMavenPom = LogAnalyzerMainDialog.class.getResource("/META-INF/jahia-loganalyzer-marker.txt");
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

    public String getBuildNumber() {
        return buildNumber;
    }

    public Date getBuildTimestamp() {
        return buildTimestamp;
    }

    public String getVersion() {
        return version;
    }

    public LogParserConfiguration getLogParserConfiguration() {
        return logParserConfiguration;
    }
}
