package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 16:06:17
 * To change this template use File | Settings | File Templates.
 */
public interface LogEntry {

    LinkedHashMap<String, Object> getValues();

    String[] toStringArray(DateFormat dateFormat);

    String[] getColumnKeys();
}
