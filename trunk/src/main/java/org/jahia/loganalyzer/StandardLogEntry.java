package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 9 juil. 2008
 * Time: 14:14:44
 * To change this template use File | Settings | File Templates.
 */
public class StandardLogEntry implements LogEntry {

    private Date standardLogDate;
    private String standardLogLevel;
    private String standardLogClassName;
    private String standardLogMessage;

    public Date getStandardLogDate() {
        return standardLogDate;
    }

    public void setStandardLogDate(Date standardLogDate) {
        this.standardLogDate = standardLogDate;
    }

    public String getStandardLogLevel() {
        return standardLogLevel;
    }

    public void setStandardLogLevel(String standardLogLevel) {
        this.standardLogLevel = standardLogLevel;
    }

    public String getStandardLogClassName() {
        return standardLogClassName;
    }

    public void setStandardLogClassName(String standardLogClassName) {
        this.standardLogClassName = standardLogClassName;
    }

    public String getStandardLogMessage() {
        return standardLogMessage;
    }

    public void setStandardLogMessage(String standardLogMessage) {
        this.standardLogMessage = standardLogMessage;
    }

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[4];
        result[0] = dateFormat.format(standardLogDate);
        result[1] = standardLogLevel;
        result[2] = standardLogClassName;
        result[3] = standardLogMessage; 
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[] getColumnKeys() {
        String[] result = new String[4];
        result[0] = "standardLogDate";
        result[1] = "standardLogLevel";
        result[2] = "standardLogClassName";
        result[3] = "standardLogMessage";
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
