<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

TODO 

This is a quick and dirty laundry list of ideas and improvements for future 
version of the Jahia Log Analysis tool :

- In Jahia, add sessionID output and support in the log analyzer for this output.
- Documentation with screenshots and maybe even a small movie to illustrate how to use
- Look into possibility to detect in which subsystem a thread is (Lucene, 
  etc...), could we do this through stack analysis ? What about global Jahia actions ? (see ThreadLogic that even uses
  stack traces to issue recommendations based on certain common traces)
- Add support for parsing /site/ in URL
- Fix URL parsing issues to reflect modern Jahia DX URLs
- Make it possible to develop plugins for the line analyzers
- Make it possible to configure new line analyzers simply by using regexp -> property mapping in ES
- Make it possible to incrementally parse logs so that we can for example schedule the log analyzer in cron jobs to
populate the ElasticSearch backend
- Fix a better solution for the configuration if we are to support plugins
- Make it possible to use scripts (Groovy,Javascript) to extend the log analyzer
- Transform it into an Apache Karaf OSGi application ?
