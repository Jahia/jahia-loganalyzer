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

Jahia Log Analyzer Tool

DISCLAIMER
--------------------------------------------------------------------------------
This tool is provided without any guarantees, but isn't risky at all because all 
it does is parse log files :)

This product includes GeoLite2 data created by MaxMind, available from
<a href="http://www.maxmind.com">http://www.maxmind.com</a>.

License
--------------------------------------------------------------------------------
This tool is licensed under the Apache Software License, V2

Features
--------------------------------------------------------------------------------
- Parsing of a single file or all the files in a directory
- Parsing of request time logs to generate performance reports
- Parsing of exceptions to generate exception reports
- Parsing of threads dumps to generate thread dump evolution
- Parsing of the rest of log lines to report the most common lines and filter
  on log level
- Parsing of garbage collection activity 
- Parsing of Jahia DX request load activity
- Parsing of Jackrabbit bundle cache logs
- Outputs CSV & JSON reports
- Uses a distant ElasticSearch 5 server to stores the parsed data into it
- Compatible with Kibana 5 for advanced data visualization

Compiling
--------------------------------------------------------------------------------

Requirements : 
- Maven 2.0.9 or more recent
- JDK 1.7 or more recent

To compile :

    mvn clean install

Execution
--------------------------------------------------------------------------------

Requirements : 
- JDK 1.7 or more recent

To run : 

    ./launch.sh
    
or if you prefer doing it manually from the tar.gz in package/target

    cd bin
    ./karaf    
    
Once Karaf has decompressed and started you can launch a log file analysis by using the 
command:

    analyze TARGET_FILE_OR_DIRECTORY

where:
- TARGET_FILE_OR_DIRECTORY: an optional reference to a file or directory that will be used as
the source of parsing. If none is specified the tool will start the GUI that allows to select
a file and change some options.

This command will launch the analysis in the background. You can watch the logs using either:

    ld
    
or : 

    log:tail (ctrl-c to stop watching the logs)
    
You can also check which log analysis tasks are currently running by using the command :

    loganalyzer:list
    
Once you are done with the Log Analyzer, you can simply exit using : 

    shutdown

Output of parsing is generated in CSV and JSON files in the same directory from which the 
application is run. The CSV files should open fine with Microsoft Excel.

See the TODO.txt file for information about stuff that isn't done yet :)

ElasticSearch configuration
--------------------------------------------------------------------------------
   
As ElasticSearch no longer supports an embedded server, we require to use a remote ElasticSearch instance.
In the etc/org.jahia.loganalyzer.writers.cfg file we have the following default configuration:

    remoteServers = localhost:9300

where : 
- REMOTE_SERVER_LIST: is an optional list of remote ElasticSearch servers to use to output 
the result of the parsing. It should be a comma seperated list of host:port values such as:

    10.0.1.0:9300, 10.0.1.1:9300   

    
Kibana Usage
--------------------------------------------------------------------------------

To use the LogAnalyzer with Kibana, first launch an analysis, and once it has 
completed the embedded ElasticSearch will stay available. You can then start 
Kibana and configure it with the following index name : 

    loganalyzer-*
    
It will also ask you to select a timestamp field and there is only one in the
LogAnalyzer data so simply save the "timestamp" field.

Don't forget to set the proper date range on the top right corner otherwise
you will probably not see any data as it defaults to the last 15 minutes. 
You can select "Year to date" for example to see recent data.

We also provide an import file for Kibana default visualizations and dashboard.
You can find it in the file : 

    kibana-export.json 
    
at the root of this project. To import it, launch Kibana 5, go into Settings->
Objects and click on Import and use the import file. You should then be able
to open the saved visualizations and dashboards.

Output files
--------------------------------------------------------------------------------
Here is a brief descriptions of the output files and their generated content 

- performance-details.csv/.json
  Contains the full list of extracted Jahia request performance data. 
  
- performance-summary.csv/.json
  Contains a view of cumulated request times for a specific Jahia pages
  
- exception-details.csv/.json
  Contains details of all the exceptions found in the log
  
- exception-summary.csv/.json
  Contains a summary of the number of times the same exception was encountered
  in the log files

- threaddump-details.csv/.json
  Contains all the threads that were found in the thread dumps generated in the
  log file. 
  
- threaddump-summary.csv/.json
  Contains a summary view of the number of threads in the different thread 
  dumps, as well as the differences between the thread dumps (new threads,
  old threads). Note : it is a good idea to generate lots of thread dumps to
  improve the quality of this data.

- garbagecollection-details.csv/.json
  Contains details of the garbage collections found in the log
 
- jackrabbitbundlecache-details.csv/.json
  Contains details of the jackrabbit bundlecache status output found in the log
  
- requestload-details.csv/.json
  Contains details of the request load output found in the log. 
  
- loglevel-details.csv/.json
  Contains all the log files NOT parsed by the performance analyzer, and that
  are above the logging level specified in the user interface.

- loglevel-summary.csv/.json
  Contains a summary view of the number of times a specific message was logged,
  regardless of the logging level specified in the user interface.
  