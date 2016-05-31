package org.jahia.loganalyzer.writers.internal;

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


import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.io.IOUtils;
import org.jahia.loganalyzer.api.LogEntry;
import org.jahia.loganalyzer.api.LogEntryWriter;

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
    boolean headersWritten = false;

    public CSVLogEntryWriter(File csvFile, char separatorChar) throws IOException {
        FileWriter writer = new FileWriter(csvFile);
        csvWriter = new CSVWriter(writer, separatorChar);
    }

    public String getFileExtension() {
        return "csv";
    }

    public void write(LogEntry logEntry) {
        if (logEntry == null) {
            return;
        }
        if (!headersWritten) {
            List<String> columnKeys = logEntry.getColumnKeys();
            List<String> columnNames = new ArrayList<>();
            for (String columnKey : columnKeys) {
                columnNames.add(columnKey);
            }
            csvWriter.writeNext(columnNames.toArray(new String[columnNames.size()]));
            headersWritten = true;
        }
        List<String> columnStrings = logEntry.toStringList(dateFormat);
        csvWriter.writeNext(columnStrings.toArray(new String[columnStrings.size()]));
    }

    public void close() throws IOException {
        csvWriter.close();
        IOUtils.closeQuietly(writer);
    }
}
