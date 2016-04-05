package org.jahia.loganalyzer.writers;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.io.IOUtils;
import org.jahia.loganalyzer.LogEntry;
import org.jahia.loganalyzer.ResourceUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A log writer that outputs to a CSV file
 * User: Serge Huber
 * Date: August 22nd, 2007
 * Time: 12:09:25
 */
public class CSVLogEntryWriter implements LogEntryWriter {

    CSVWriter csvWriter;
    FileWriter writer;
    DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public CSVLogEntryWriter(File csvFile, char separatorChar, LogEntry logEntry) throws IOException {
        FileWriter writer = new FileWriter(csvFile);
        csvWriter = new CSVWriter(writer, separatorChar);
        List<String> columnKeys = logEntry.getColumnKeys();
        List<String> columnNames = new ArrayList<>();
        for (String columnKey : columnKeys) {
            columnNames.add(ResourceUtils.getBundleString("org.jahia.loganalyzer.logentry.column.header." + columnKey));
        }
        csvWriter.writeNext(columnNames.toArray(new String[columnNames.size()]));
    }

    public String getFileExtension() {
        return "csv";
    }

    public void write(LogEntry logEntry) {
        if (logEntry == null) {
            return;
        }
        List<String> columnStrings = logEntry.toStringList(dateFormat);
        csvWriter.writeNext(columnStrings.toArray(new String[columnStrings.size()]));
    }

    public void close() throws IOException {
        csvWriter.close();
        IOUtils.closeQuietly(writer);
    }
}
