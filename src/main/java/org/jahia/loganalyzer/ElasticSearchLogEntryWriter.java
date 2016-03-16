package org.jahia.loganalyzer;

import org.apache.commons.io.FilenameUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by loom on 11.03.16.
 */
public class ElasticSearchLogEntryWriter implements LogEntryWriter {

    BulkProcessor bulkProcessor = ElasticSearchService.getInstance().getBulkProcessor();
    Client client = ElasticSearchService.getInstance().getClient();
    String indexBaseName = null;
    String indexTimestampSuffix = "";
    String typeName = null;
    AtomicLong idGenerator = new AtomicLong(0);
    Set<String> initializedIndices = new HashSet<String>();

    public ElasticSearchLogEntryWriter(File htmlFile, LogEntry logEntry, LogParserConfiguration logParserConfiguration) {
        indexBaseName = FilenameUtils.getBaseName(logParserConfiguration.getInputFile().getName()).toLowerCase();
        if (indexBaseName.contains(".")) {
            indexBaseName = indexBaseName.replaceAll("\\.", "-");
        }
        typeName = FilenameUtils.getBaseName(htmlFile.getPath()).toLowerCase();
        if (typeName.contains(".")) {
            typeName = typeName.replaceAll("\\.", "-");
        }

    }

    public void initIndex(String indexName) {
        if (initializedIndices.contains(indexName)) {
            return;
        }

        final IndicesExistsResponse res = client.admin().indices().prepareExists(indexBaseName + indexTimestampSuffix).execute().actionGet();
        if (res.isExists()) {
            final DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(indexBaseName + indexTimestampSuffix);
            delIdx.execute().actionGet();
        }

        client.admin().indices().prepareCreate(indexBaseName + indexTimestampSuffix)
                .addMapping(typeName, "{\n" +
                        "    \"" + typeName + "\": {\n" +
                        "      \"properties\": {\n" +
                        "        \"logType\": {\n" +
                        "          \"type\": \"string\",\n" +
                        "          \"index\":  \"not_analyzed\"" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }")
                .get();

        initializedIndices.add(indexName);
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
        // this is a hack to get around issue : https://github.com/elastic/kibana/issues/5684
        json.put("logType", typeName);
        if (logEntry instanceof TimestampedLogEntry) {
            TimestampedLogEntry timestampedLogEntry = (TimestampedLogEntry) logEntry;
            Date timestamp = timestampedLogEntry.getTimestamp();
            if (timestamp != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                indexTimestampSuffix = "-" + dateFormat.format(timestamp);
            }
        }
        initIndex(indexBaseName + indexTimestampSuffix);
        IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexBaseName + indexTimestampSuffix, typeName, Long.toString(idGenerator.incrementAndGet()))
                .setSource(json);

        bulkProcessor.add(indexRequestBuilder.request());

    }

    @Override
    public void close() throws IOException {
    }
}
