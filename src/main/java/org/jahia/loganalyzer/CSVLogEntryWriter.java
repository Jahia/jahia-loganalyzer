package org.jahia.loganalyzer;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.Writer;
import java.io.IOException;
import java.util.ResourceBundle;
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
    DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public CSVLogEntryWriter(Writer writer, char separatorChar, LogEntry logEntry) {
        csvWriter = new CSVWriter(writer, separatorChar);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("loganalyzer_messages");
        String[] columnKeys = logEntry.getColumnKeys();
        String[] columnNames = new String[columnKeys.length];
        for (int i=0; i < columnKeys.length; i++) {
            columnNames[i] = resourceBundle.getString("org.jahia.loganalyzer.logentry.column.header." + columnKeys[i]);
        }
        csvWriter.writeNext(columnNames);
    }

    public void write(LogEntry logEntry) {
        if (logEntry == null) {
            return;
        }
        csvWriter.writeNext(logEntry.toStringArray(dateFormat));
    }

    public void flush() {

    }

    public void close() throws IOException {
        csvWriter.close();
    }
}
