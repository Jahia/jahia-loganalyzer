package org.jahia.loganalyzer.services.taskexecutor;

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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An input stream that can monitor the number of bytes read and uses a call back interface to notify changes in the
 * number of bytes read.
 */
public class MonitorInputStream extends FilterInputStream {

    private Monitor monitor;
    private int bytesRead = 0;
    private int totalAvailableBytes = 0;

    public MonitorInputStream(Monitor monitor,
                              InputStream in) {
        super(in);
        this.monitor = monitor;
        try {
            totalAvailableBytes = in.available();
            monitor.setTotalBytes(totalAvailableBytes);
        } catch (IOException ioe) {
            totalAvailableBytes = 0;
            monitor.setTotalBytes(0);
        }
    }

    /**
     * Overrides <code>FilterInputStream.read</code>
     * to update the progress monitor after the read.
     */
    public int read() throws IOException {
        int c = in.read();
        if (c >= 0) monitor.setProgress(++bytesRead);
        return c;
    }

    public int read(byte b[]) throws IOException {
        int nr = in.read(b);
        if (nr > 0) monitor.setProgress(bytesRead += nr);
        return nr;
    }

    /**
     * Overrides <code>FilterInputStream.read</code>
     * to update the progress monitor after the read.
     */
    public int read(byte b[],
                    int off,
                    int len) throws IOException {
        int nr = in.read(b, off, len);
        if (nr > 0) monitor.setProgress(bytesRead += nr);
        return nr;
    }

    /**
     * Overrides <code>FilterInputStream.skip</code>
     * to update the progress monitor after the skip.
     */
    public long skip(long n) throws IOException {
        long nr = in.skip(n);
        if (nr > 0) monitor.setProgress(bytesRead += nr);
        return nr;
    }

    /**
     * Overrides <code>FilterInputStream.close</code>
     * to close the progress monitor as well as the stream.
     */
    public void close() throws IOException {
        in.close();
    }

    /**
     * Overrides <code>FilterInputStream.reset</code>
     * to reset the progress monitor as well as the stream.
     */
    public synchronized void reset() throws IOException {
        in.reset();
        bytesRead = totalAvailableBytes - in.available();
        monitor.setProgress(bytesRead);
    }


    public interface Monitor {
        void setTotalBytes(long totalBytes);

        void setProgress(long bytesRead);
    }
}
