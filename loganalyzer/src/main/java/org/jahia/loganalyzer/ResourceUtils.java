package org.jahia.loganalyzer;

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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Various static utilities to make dealing with resources bundles and files a little easier.
 */
public class ResourceUtils {

    public static boolean copyResourceToFile(String resourceLocation, File targetDirectory) {
        InputStream resourceStream = ResourceUtils.class.getClassLoader().getResourceAsStream(resourceLocation);
        if (resourceStream == null) {
            System.err.println("Couldn't find resource " + resourceLocation + ", won't copy to " + targetDirectory);
            return false;
        }
        String fileName = FilenameUtils.getName(resourceLocation);
        File targetFile = new File(targetDirectory, fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(targetFile);
            IOUtils.copy(resourceStream, fileOutputStream);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
            IOUtils.closeQuietly(resourceStream);
        }
        return false;
    }

    public static boolean extractZipResource(String resourceLocation, File targetDirectory) {
        InputStream resourceStream = ResourceUtils.class.getClassLoader().getResourceAsStream(resourceLocation);
        if (resourceStream == null) {
            System.err.println("Couldn't find resource " + resourceLocation + ", won't extract to " + targetDirectory);
            return false;
        }
        ZipInputStream zipInputStream = new ZipInputStream(resourceStream);
        try {
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File entryDestination = new File(targetDirectory, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    OutputStream out = new FileOutputStream(entryDestination);
                    IOUtils.copy(zipInputStream, out);
                    IOUtils.closeQuietly(out);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(zipInputStream);
        }
        return false;
    }

    public static String getBundleString(String bundleKey) {
        if (bundleKey == null) {
            return null;
        }
        ResourceBundle resourceBundle = ResourceBundle.getBundle("loganalyzer_messages");
        try {
            return resourceBundle.getString(bundleKey);
        } catch (MissingResourceException mre) {
            System.err.println("!!! Missing bundle resource key: " + bundleKey + "!!!");
            return "[MISSING RESOURCE BUNDLE KEY:" + bundleKey + "]";
        }
    }

}
