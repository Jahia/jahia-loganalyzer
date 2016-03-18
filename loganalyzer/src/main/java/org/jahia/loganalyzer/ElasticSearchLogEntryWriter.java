package org.jahia.loganalyzer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by loom on 11.03.16.
 */
public class ElasticSearchLogEntryWriter implements LogEntryWriter {

    static Map<String, Set<String>> indexMappings = new HashMap<String, Set<String>>();
    BulkProcessor bulkProcessor = ElasticSearchService.getInstance().getBulkProcessor();
    Client client = ElasticSearchService.getInstance().getClient();
    String indexBaseName = null;
    String indexTimestampSuffix = "";
    String typeName = null;
    AtomicLong idGenerator = new AtomicLong(0);

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
        if (indexMappings.containsKey(indexName)) {
            return;
        }

        final IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).execute().actionGet();
        if (res.isExists()) {
            System.err.println("WARNING: Deleting old index instance: " + indexName);
            final DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(indexName);
            delIdx.execute().actionGet();
        } else {
            client.admin().indices().prepareCreate(indexName).get();
        }

        indexMappings.put(indexName, new HashSet<String>());
    }

    public void initMapping(String indexName, String typeName) {
        if (indexMappings.containsKey(indexName) && indexMappings.get(indexName).contains(typeName)) {
            return;
        }
        Set<String> mappings = indexMappings.get(indexName);
        if (mappings == null) {
            mappings = new HashSet<>();
        }

        InputStream mappingInputStream = this.getClass().getClassLoader().getResourceAsStream("es-mappings/" + typeName + ".json");
        String mapping = null;
        try {
            if (mappingInputStream != null) {
                mapping = IOUtils.toString(mappingInputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mapping == null) {
            mapping = "{\n" +
                    "      \"properties\": {\n" +
                    "        \"logType\": {\n" +
                    "          \"type\": \"string\",\n" +
                    "          \"index\":  \"not_analyzed\"" +
                    "        }\n" +
                    "      }\n" +
                    "  }";
        }

        PutMappingRequestBuilder putMappingRequestBuilder = client.admin().indices().preparePutMapping(indexName);
        PutMappingResponse putMappingResponse = putMappingRequestBuilder.setType(typeName).setSource(mapping).get();

        mappings.add(typeName);
        indexMappings.put(indexName, mappings);
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
        initMapping(indexBaseName + indexTimestampSuffix, typeName);
        IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexBaseName + indexTimestampSuffix, typeName, Long.toString(idGenerator.incrementAndGet()))
                .setSource(json);

        bulkProcessor.add(indexRequestBuilder.request());

    }

    @Override
    public void close() throws IOException {
    }
}
