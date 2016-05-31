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


import java.io.IOException;
import java.util.Date;

/**
 * Common interface for all line analyzer implementations
 * User: Serge Huber
 * Date: August 22th, 2007
 * Time: 12:51:57
 */
public interface LineAnalyzer {

    /**
     * Get a unique key to identify this line analyzer
     *
     * @return
     */
    String getKey();

    /**
     * Checks whether the current line is going to be sent to a specific analyzer.
     * @return
     */
    boolean isForThisAnalyzer(LineAnalyzerContext context);

    /**
     * Process the line with the analyzer
     * @throws IOException
     */
    Date parseLine(LineAnalyzerContext context) throws IOException;

    /**
     * This method is called when this analyzer is being switched to another one by the composite analyzer,
     * so that it can cleanup it's state before passing control to another analyzer
     */
    void finishPreviousState(LineAnalyzerContext context);

    /**
     * Used to free any resources used by the line analyzer and write summary information
     */
    void stop() throws IOException;
}
