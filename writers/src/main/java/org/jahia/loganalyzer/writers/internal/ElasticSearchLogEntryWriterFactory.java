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

import org.jahia.loganalyzer.api.LogEntryWriter;
import org.jahia.loganalyzer.api.LogEntryWriterFactory;
import org.jahia.loganalyzer.writers.ElasticSearchService;

import java.io.File;
import java.io.IOException;

/**
 * Created by loom on 04.05.16.
 */
public class ElasticSearchLogEntryWriterFactory implements LogEntryWriterFactory {

    private ElasticSearchService elasticSearchService;

    public void setElasticSearchService(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @Override
    public String getExtension() {
        return "es";
    }

    @Override
    public LogEntryWriter createLogWriter(File htmlFile) throws IOException {
        return new ElasticSearchLogEntryWriter(htmlFile, elasticSearchService);
    }
}
