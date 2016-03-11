package org.jahia.loganalyzer;

import org.apache.commons.io.FilenameUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by loom on 11.03.16.
 */
public class ElasticSearchLogEntryWriter implements LogEntryWriter {

    Client client = ElasticSearchService.getInstance().getClient();
    String indexName = null;
    String typeName = null;
    AtomicLong idGenerator = new AtomicLong(0);

    public ElasticSearchLogEntryWriter(File htmlFile, LogEntry logEntry, LogParserConfiguration logParserConfiguration) {
        indexName = FilenameUtils.getBaseName(logParserConfiguration.getInputFile().getName()).toLowerCase();
        if (indexName.contains(".")) {
            indexName = indexName.replaceAll("\\.", "-");
        }
        typeName = FilenameUtils.getBaseName(htmlFile.getPath()).toLowerCase();
        if (typeName.contains(".")) {
            typeName = typeName.replaceAll("\\.", "-");
        }
    }

    @Override
    public void write(LogEntry logEntry) {
        LinkedHashMap<String, Object> json = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Object> valueEntry : logEntry.getValues().entrySet()) {
            String key = valueEntry.getKey();
            if (key.contains(".")) {
                key = key.replaceAll("\\.", "-");
            }
            json.put(key, valueEntry.getValue());
        }
        IndexResponse response = client.prepareIndex(indexName, typeName, Long.toString(idGenerator.incrementAndGet()))
                .setSource(json)
                .get();
    }

    @Override
    public void close() throws IOException {
    }
}
