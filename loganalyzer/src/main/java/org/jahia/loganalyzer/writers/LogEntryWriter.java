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


import org.jahia.loganalyzer.LogEntry;

import java.io.IOException;

/**
 * Common interface for all log entry writers
 * User: Serge Huber
 * Date: August 22nd, 2007
 * Time: 12:08:37
 */
public interface LogEntryWriter {

    String getFileExtension();

    void write(LogEntry logEntry);

    void close() throws IOException;
}
