package org.jahia.loganalyzer.writers;

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
