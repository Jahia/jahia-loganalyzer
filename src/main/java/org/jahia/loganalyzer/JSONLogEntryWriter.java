package org.jahia.loganalyzer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;

/**
 * Created by loom on 08.03.16.
 */
public class JSONLogEntryWriter implements LogEntryWriter {

    FileWriter fileWriter;
    JsonGenerator jsonGenerator;
    DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public JSONLogEntryWriter(File htmlFile, LogEntry logEntry) throws IOException {
        JsonFactory factory = new JsonFactory();
        // configure, if necessary:
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        fileWriter = new FileWriter(htmlFile);
        jsonGenerator = factory.createGenerator(fileWriter);
        jsonGenerator.useDefaultPrettyPrinter();
        jsonGenerator.writeStartArray();
    }

    public void write(LogEntry logEntry) {
        try {
            jsonGenerator.writeStartObject();
            String[] fieldValues = logEntry.toStringArray(dateFormat);
            String[] fieldNames = logEntry.getColumnKeys();
            for (int i = 0; i < fieldNames.length; i++) {
                jsonGenerator.writeStringField(fieldNames[i], fieldValues[i]);
            }
            jsonGenerator.writeEndObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        jsonGenerator.writeEndArray();
        jsonGenerator.close();
        IOUtils.closeQuietly(fileWriter);
    }
}
