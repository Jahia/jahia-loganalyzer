package org.jahia.loganalyzer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.*;
import java.text.DateFormat;
import java.util.ResourceBundle;

/**
 * Created by loom on 01.03.16.
 */
public class HTMLLogEntryWriter implements LogEntryWriter {

    Writer htmlWriter = null;
    DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public HTMLLogEntryWriter(File htmlFile, LogEntry logEntry, LogParserConfiguration logParserConfiguration) throws IOException {
        htmlWriter = new FileWriter(htmlFile);
        File targetDirectory = htmlFile.getParentFile();
        for (String htmlResourceToCopy : logParserConfiguration.getHtmlResourcesToCopy()) {
            copyResourceToFile(htmlResourceToCopy, targetDirectory);
        }
        ResourceBundle resourceBundle = ResourceBundle.getBundle("loganalyzer_messages");
        String[] columnKeys = logEntry.getColumnKeys();
        String[] columnNames = new String[columnKeys.length];
        String htmlTemplate = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("html/template.html"));
        try {
            htmlWriter.append("<!DOCTYPE html>");
            htmlWriter.append("<html lang=\"en\">\n");
            htmlWriter.append("  <head>\n");

            htmlWriter.append("    <meta charset=\"utf-8\">");
            htmlWriter.append("    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
            htmlWriter.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
            htmlWriter.append("    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\">\n");
            htmlWriter.append("    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css\" integrity=\"sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r\" crossorigin=\"anonymous\">\n");
            htmlWriter.append("    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.10.1/bootstrap-table.min.css\">\n");
            htmlWriter.append("    <link rel=\"stylesheet\" href=\"log-analyzer.css\">\n");
            htmlWriter.append("    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>");
            htmlWriter.append("    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\" integrity=\"sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS\" crossorigin=\"anonymous\"></script>\n");
            /*
            htmlWriter.append("    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.10.1/bootstrap-table.min.js\"></script>\n");
            htmlWriter.append("    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.10.1/locale/bootstrap-table-en-US.min.js\"></script>\n");
            htmlWriter.append("    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.10.1/extensions/export/bootstrap-table-export.js\"></script>\n");
            htmlWriter.append("    <script src=\"https://rawgit.com/hhurz/tableExport.jquery.plugin/master/tableExport.js\"></script>\n");
            */
            htmlWriter.append("  </head>\n");
            htmlWriter.append("  <body>\n");
            htmlWriter.append("  <div class=\"container-fluid\">");
            /*
            htmlWriter.append("    <table class=\"laTable\" data-toggle=\"table\" data-toolbar=\"#toolbar\"\n" +
                    "           data-search=\"true\"\n" +
                    "           data-show-toggle=\"true\"\n" +
                    "           data-show-columns=\"true\"\n" +
                    "           data-show-export=\"true\"\n" +
                    "           data-minimum-count-columns=\"2\"\n" +
                    "           data-show-pagination-switch=\"true\"\n" +
                    "           data-pagination=\"true\"\n" +
                    "           data-id-field=\"id\"\n" +
                    "           data-page-list=\"[5, 10, 25, 50, 100, ALL]\"\n" +
                    "           data-show-footer=\"false\">\n");
                    */
            htmlWriter.append("    <table class=\"table table-striped laTable\">\n");
            htmlWriter.append("      <thead>\n");
            htmlWriter.append("        <tr>");
            for (int i=0; i < columnKeys.length; i++) {
                columnNames[i] = resourceBundle.getString("org.jahia.loganalyzer.logentry.column.header." + columnKeys[i]);
                htmlWriter.append("<th data-sortable=\"true\">");
                htmlWriter.append(StringEscapeUtils.escapeHtml(columnNames[i]));
                htmlWriter.append("</th>");
            }
            htmlWriter.append("</tr>\n");
            htmlWriter.append("      </thead>\n");
            htmlWriter.append("      <tbody>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(LogEntry logEntry) {
        try {
            htmlWriter.append("        <tr>");
            for (String columnValue : logEntry.toStringArray(dateFormat)) {
                htmlWriter.append("<td>");
                if (columnValue != null && columnValue.contains("\n")) {
                    htmlWriter.append("<pre>");
                    htmlWriter.append(StringEscapeUtils.escapeHtml(columnValue));
                    htmlWriter.append("</pre>");
                } else {
                    htmlWriter.append(StringEscapeUtils.escapeHtml(columnValue));
                }
                htmlWriter.append("</td>");
            }
            htmlWriter.append("</tr>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        htmlWriter.append("      </tbody>\n");
        htmlWriter.append("    </table>\n");
        htmlWriter.append("  </div>\n");
        htmlWriter.append("  </body>\n");
        htmlWriter.append("</html>\n");
        htmlWriter.close();
        IOUtils.closeQuietly(htmlWriter);
    }

    private boolean copyResourceToFile(String resourceLocation, File targetDirectory) {
        InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream(resourceLocation);
        String fileName = FilenameUtils.getName(resourceLocation);
        File targetFile = new File(targetDirectory, fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(targetFile);
            IOUtils.copy(resourceStream, fileOutputStream);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
            IOUtils.closeQuietly(resourceStream);
        }
        return false;
    }
}
