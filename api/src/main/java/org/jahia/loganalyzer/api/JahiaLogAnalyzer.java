package org.jahia.loganalyzer.api;

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

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Main interface of the Jahia Log Analyzer, this is where the application is started and performs all
 * the most high-level operations
 */
public interface JahiaLogAnalyzer {

    void start(File inputFile);

    boolean analyze(java.awt.Component uiComponent) throws IOException;

    void retrieveBuildInformation();

    String getBuildNumber();

    Date getBuildTimestamp();

    String getVersion();

}
