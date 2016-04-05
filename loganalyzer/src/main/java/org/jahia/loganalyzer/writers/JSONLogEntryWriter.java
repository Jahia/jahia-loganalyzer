package org.jahia.loganalyzer.writers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.jahia.loganalyzer.LogEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A log writer that output to JSON files
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
        jsonGenerator.setCodec(new ObjectMapper());
        jsonGenerator.useDefaultPrettyPrinter();
        jsonGenerator.writeStartArray();
    }

    public String getFileExtension() {
        return "json";
    }

    public void write(LogEntry logEntry) {
        try {
            jsonGenerator.writeStartObject();
            LinkedHashMap<String, Object> values = logEntry.getValues();
            for (Map.Entry<String, Object> valueEntry : values.entrySet()) {
                jsonGenerator.writeObjectField(valueEntry.getKey(), valueEntry.getValue());
            }
            /*
            String[] fieldValues = logEntry.toStringArray(dateFormat);
            String[] fieldNames = logEntry.getColumnKeys();
            for (int i = 0; i < fieldNames.length; i++) {
                jsonGenerator.writeStringField(fieldNames[i], fieldValues[i]);
            }
            */
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
