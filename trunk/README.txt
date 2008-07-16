
Jahia Log Analyzer Tool

DISCLAIMER
--------------------------------------------------------------------------------
This tool is provided without any guarantees, but isn't risky at all because all 
it does is parse a log file :)

Features
--------------------------------------------------------------------------------
- Parsing of request time logs to generate performance reports
- Parsing of exceptions to generate exception reports
- Parsing of threads dumps to generate thread dump evolution
- Parsing of the rest of log lines to report the most common lines and filter
  on log level

Compiling
--------------------------------------------------------------------------------

Requirements : 
- Maven 2.0.9 or more recent
- JDK 1.5 or more recent

To compile :

mvn package

Web Start Installation (optional)
--------------------------------------------------------------------------------
This project now builds a JNLP project that can be deployed to a web server 
for Java Web Start execution. The ZIP file that must be deployed is contained
in the target/ directory. Simply unzip on the webserver and point your users
to the directory, the rest should be automatic.

Execution
--------------------------------------------------------------------------------

Requirements : 
- JDK 1.5 or more recent

To run : 

cd target 
jar -jar loganalyzer-VERSION.jar

Output of parsing is generated in CSV files in the same directory from which the 
application is run. They should open fine with Microsoft Excel.

See the TODO.txt file for information about stuff that isn't done yet :)

Output files
--------------------------------------------------------------------------------
Here is a brief descriptions of the output files and their generated content 

- jahia-perf-details.csv
  Contains the full list of extracted Jahia request performance data. 
  
- jahia-perf-summary.csv
  Contains a view of cumulated request times for a specific Jahia pages
  
- jahia-exception-details.csv
  Contains details of all the exceptions found in the log
  
- jahia-exception-summary.csv
  Contains a summary of the number of times the same exception was encountered
  in the log files

- jahia-thread-details.csv
  Contains all the threads that were found in the thread dumps generated in the
  log file. 
  
- jahia-thread-summary.csv
  Contains a summary view of the number of threads in the different thread 
  dumps, as well as the differences between the thread dumps (new threads,
  old threads). Note : it is a good idea to generate lots of thread dumps to
  improve the quality of this data.

- jahia-standard-details.csv
  Contains all the log files NOT parsed by the performance analyzer, and that
  are above the logging level specified in the user interface.

- jahia-standard-summary.csv
  Contains a summary view of the number of times a specific message was logged,
  regardless of the logging level specified in the user interface.
  
Advanced configuration file
--------------------------------------------------------------------------------

By default the configuration is loaded directly from a configuration file 
included in the JAR. But you may override this configuration with a file that
you store at the same location as the location from which you execute the 
JAR. We provide a sample parser-config.xml.sample file that is actually a
copy of the default configuration file.

You might want to use this file if you have trouble with the default regular
expressions, maybe because the log files you are using include a different
logging format.