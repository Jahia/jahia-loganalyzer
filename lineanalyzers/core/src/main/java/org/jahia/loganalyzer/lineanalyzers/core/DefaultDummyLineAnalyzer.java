package org.jahia.loganalyzer.lineanalyzers.core;

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


import org.jahia.loganalyzer.api.LineAnalyzer;
import org.jahia.loganalyzer.api.LineAnalyzerContext;

import java.io.IOException;
import java.util.Date;

/**
 * This is the default line analyzer, that will always be called as a last resort, just to ignore lines that
 * aren't picked up by any other line analyzer
 *
 * User: Serge Huber
 * Date: July 8th, 2008
 * Time: 11:16:57
 */
public class DefaultDummyLineAnalyzer implements LineAnalyzer {

    public String getKey() {
        return "dummy";
    }

    public boolean isForThisAnalyzer(LineAnalyzerContext context) {
        return true;
    }

    public Date parseLine(LineAnalyzerContext context) {
        return null;
    }

    public void finishPreviousState(LineAnalyzerContext context) {
    }

    public void stop() throws IOException {
    }
}
