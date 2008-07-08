package org.jahia.loganalyzer;

import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 16:06:17
 * To change this template use File | Settings | File Templates.
 */
public interface LogEntry {

    public String[] toStringArray(DateFormat dateFormat);

    public String[] getColumnKeys();
}
