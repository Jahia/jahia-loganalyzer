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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.jahia.loganalyzer.api.LogEntry;
import org.jahia.loganalyzer.api.LogEntryWriter;
import org.jahia.loganalyzer.writers.ElasticSearchService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A log entry writer that output to an ElasticSearch service
 */
public class ElasticSearchLogEntryWriter implements LogEntryWriter {

    static Map<String, Set<String>> indexMappings = new HashMap<String, Set<String>>();
    ElasticSearchService elasticSearchService = null;
    String indexBaseName = "loganalyzer";
    String indexTimestampSuffix = "";
    String typeName = null;
    AtomicLong idGenerator = new AtomicLong(0);

    public ElasticSearchLogEntryWriter(File htmlFile, ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
        typeName = FilenameUtils.getBaseName(htmlFile.getPath()).toLowerCase();
        if (typeName.contains(".")) {
            typeName = typeName.replaceAll("\\.", "-");
        }
    }

    public String getFileExtension() {
        return "";
    }

    public void initIndex(String indexName) {
        if (indexMappings.containsKey(indexName)) {
            return;
        }

        final IndicesExistsResponse res = elasticSearchService.getClient().admin().indices().prepareExists(indexName).execute().actionGet();
        if (res.isExists()) {
            //final DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(indexName);
            // delIdx.execute().actionGet();
            new Throwable("WARNING: " + indexName + " already exists, this normally shouldn't happen ! This might happen if you have another instance of the LogAnalyzer running !").printStackTrace();
        } else {
            elasticSearchService.getClient().admin().indices().prepareCreate(indexName).get();
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

        PutMappingRequestBuilder putMappingRequestBuilder = elasticSearchService.getClient().admin().indices().preparePutMapping(indexName);
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
        if (logEntry.getTimestamp() != null) {
            Date timestamp = logEntry.getTimestamp();
            if (timestamp != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                indexTimestampSuffix = "-" + dateFormat.format(timestamp);
            }
        }
        initIndex(indexBaseName + indexTimestampSuffix);
        initMapping(indexBaseName + indexTimestampSuffix, typeName);
        IndexRequestBuilder indexRequestBuilder = elasticSearchService.getClient().prepareIndex(indexBaseName + indexTimestampSuffix, typeName, Long.toString(idGenerator.incrementAndGet()))
                .setSource(json);

        elasticSearchService.getBulkProcessor().add(indexRequestBuilder.request());

    }

    @Override
    public void close() throws IOException {
    }
}
