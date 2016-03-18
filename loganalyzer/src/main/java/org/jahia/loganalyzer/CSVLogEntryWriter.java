package org.jahia.loganalyzer;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 ao�t 2007
 * Time: 12:09:25
 * To change this template use File | Settings | File Templates.
 */
public class CSVLogEntryWriter implements LogEntryWriter {

    CSVWriter csvWriter;
    FileWriter writer;
    DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public CSVLogEntryWriter(File csvFile, char separatorChar, LogEntry logEntry) throws IOException {
        FileWriter writer = new FileWriter(csvFile);
        csvWriter = new CSVWriter(writer, separatorChar);
        String[] columnKeys = logEntry.getColumnKeys();
        String[] columnNames = new String[columnKeys.length];
        for (int i=0; i < columnKeys.length; i++) {
            columnNames[i] = ResourceUtils.getBundleString("org.jahia.loganalyzer.logentry.column.header." + columnKeys[i]);
        }
        csvWriter.writeNext(columnNames);
    }

    public void write(LogEntry logEntry) {
        if (logEntry == null) {
            return;
        }
        csvWriter.writeNext(logEntry.toStringArray(dateFormat));
    }

    public void close() throws IOException {
        csvWriter.close();
        IOUtils.closeQuietly(writer);
    }
}
