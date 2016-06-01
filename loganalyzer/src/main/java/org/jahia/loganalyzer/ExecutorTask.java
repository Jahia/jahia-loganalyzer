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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * An abstract task that contains runtime information about the task such as start and completion time, completion
 * pourcentage and a data map.
 */
public abstract class ExecutorTask<V> implements Callable<V> {

    private final String identifier = UUID.randomUUID().toString();
    private String name;
    private String description;
    private long startTime;
    private long completionTime;
    private double completionPourcentage;
    private Map<String, Object> dataMap = new ConcurrentHashMap<>();
    private Future<V> future;

    private ExecutorTaskService executorTaskService;

    public V call() throws Exception {
        startTime = System.currentTimeMillis();
        completionPourcentage = 0.0;
        if (executorTaskService != null) {
            executorTaskService.fireBeforeStarted(this);
        }
        V result = execute();
        completionTime = System.currentTimeMillis();
        completionPourcentage = 1.0;
        if (executorTaskService != null) {
            executorTaskService.fireAfterEnd(this);
        }
        return result;
    }

    public abstract V execute() throws Exception;

    public ExecutorTaskService getExecutorTaskService() {
        return executorTaskService;
    }

    public void setExecutorTaskService(ExecutorTaskService executorTaskService) {
        this.executorTaskService = executorTaskService;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public double getCompletionPourcentage() {
        return completionPourcentage;
    }

    public void setCompletionPourcentage(double completionPourcentage) {
        this.completionPourcentage = completionPourcentage;
        if (executorTaskService != null) {
            executorTaskService.firePourcentageChanged(this);
        }
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public Future<V> getFuture() {
        return future;
    }

    public void setFuture(Future<V> future) {
        this.future = future;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExecutorTask<?> that = (ExecutorTask<?>) o;

        return identifier.equals(that.identifier);

    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
